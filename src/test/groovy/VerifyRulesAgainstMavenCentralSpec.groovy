import com.google.common.base.Splitter
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import nebula.plugin.resolutionrules.*
import org.apache.lucene.index.MultiFields
import org.apache.maven.index.ArtifactInfo
import org.apache.maven.index.Indexer
import org.apache.maven.index.context.IndexCreator
import org.apache.maven.index.context.IndexingContext
import org.apache.maven.index.updater.IndexUpdateRequest
import org.apache.maven.index.updater.IndexUpdater
import org.apache.maven.index.updater.WagonHelper
import org.apache.maven.wagon.Wagon
import org.apache.maven.wagon.events.TransferEvent
import org.apache.maven.wagon.observers.AbstractTransferListener
import org.codehaus.plexus.DefaultContainerConfiguration
import org.codehaus.plexus.DefaultPlexusContainer
import org.codehaus.plexus.PlexusConstants
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.version.Version
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Shared
import spock.lang.Specification

class VerifyRulesAgainstMavenCentralSpec extends Specification {
    @Shared
    IndexingContext context
    @Shared
    Multimap<Rule, ArtifactInfo> artifactsByRule
    @Shared
    RuleSet ruleSet

    def setupSpec() {
        context = prepareIndexer()
        def plugin = new ResolutionRulesPlugin()
        plugin.apply(ProjectBuilder.builder().build())
        def ruleSets = new File("src/main/resources").listFiles().collect { plugin.parseJsonFile(it) }
        ruleSet = RulesKt.flatten(ruleSets)
        artifactsByRule = artifactsByRule(context, ruleSet)
    }

    static IndexingContext prepareIndexer() {
        def config = new DefaultContainerConfiguration()
        config.setClassPathScanning(PlexusConstants.SCANNING_INDEX)
        def container = new DefaultPlexusContainer(config)

        def baseDir = new File("build/maven-central")
        def localCache = new File(baseDir, "cache")
        def indexDir = new File(baseDir, "index")
        def indexers = Arrays.asList(container.lookup(IndexCreator, "min"))
        def indexer = container.lookup(Indexer)
        def context = indexer.createIndexingContext("central-context", "central", localCache, indexDir,
                "http://repo1.maven.org/maven2", null, true, true, indexers)

        def indexUpdater = container.lookup(IndexUpdater)
        def wagon = container.lookup(Wagon, "http")
        def listener = new AbstractTransferListener() {
            @Override
            void transferStarted(TransferEvent transferEvent) {
                println transferEvent
            }

            @Override
            void transferCompleted(TransferEvent transferEvent) {
                println transferEvent
            }
        }

        def resourceFetcher = new WagonHelper.WagonFetcher(wagon, listener, null, null)
        def updateRequest = new IndexUpdateRequest(context, resourceFetcher)
        def updateResult = indexUpdater.fetchAndUpdateIndex(updateRequest)
        println "Updated index. fullUpdate: ${updateResult.fullUpdate}"
        return context
    }

    static Multimap<Rule, ArtifactInfo> artifactsByRule(IndexingContext context, RuleSet ruleSet) {
        def artifactsByRule = ArrayListMultimap.create()
        def searcher = context.acquireIndexSearcher()
        def reader = searcher.getIndexReader()
        def liveDocs = MultiFields.getLiveDocs(reader)
        def maxDocs = reader.maxDoc()
        def fieldSplitter = Splitter.on('|')
        for (i in 0..maxDocs - 1) {
            if (liveDocs != null && !liveDocs.get(i)) {
                continue
            }
            def doc = reader.document(i)
            if (doc.get(ArtifactInfo.UINFO) == null) {
                if (doc.get(ArtifactInfo.DELETED) == null) {
                    println "Non-artifact document found at index $i. Document $doc"
                }
                // Otherwise, the document has been marked as deleted for incremental updates
                continue
            }

            def uinfo = fieldSplitter.split(doc.get(ArtifactInfo.UINFO))
            def groupId = uinfo[0]
            def artifactId = uinfo[1]
            def version = uinfo[2]
            def classifier = uinfo[3]
            def extension = uinfo[4]

            if (classifier == "NA" && extension == "jar") {
                def info = new ArtifactInfo("central", groupId, artifactId, version, classifier, extension)
                ruleSet.with {
                    String module = "$groupId:$artifactId"
                    align.each { rule ->
                        if (rule.ruleMatches(groupId, artifactId)) {
                            artifactsByRule.put(rule, info)
                        }
                    }
                    [deny, exclude, reject].flatten().each { rule ->
                        if (rule.module == module) {
                            artifactsByRule.put(rule, info)
                        }
                    }
                    [substitute, replace].flatten().each { rule ->
                        if (rule.module == module || rule.with == module) {
                            artifactsByRule.put(rule, info)
                        }
                    }
                }
            }
        }

        def classLoader = VerifyRulesAgainstMavenCentralSpec.classLoader
        def missingArtifacts = classLoader.getResourceAsStream("missing-artifacts-whitelist.txt").readLines().collect { new DefaultArtifact(it) }
        missingArtifacts.each { missingArtifact ->
            ruleSet.align.each { rule ->
                if (rule.ruleMatches(missingArtifact.groupId, missingArtifact.artifactId)) {
                    artifactsByRule.put(rule, new ArtifactInfo("missing", missingArtifact.groupId, missingArtifact.artifactId, missingArtifact.version, null, null))
                }
            }
        }

        return artifactsByRule
    }

    def 'align rules are able to align to the latest release across all artifacts'() {
        expect:
        def rules = artifactsByRule.keySet().findAll { it instanceof AlignRule }
        rules.each { rule ->
            def versionsByArtifact = artifactsByRule.get(rule)
                    .groupBy { "${it.groupId}:${it.artifactId}" }
                    .collectEntries { key, value -> [(key): value.collect { it.artifactVersion }] }
                    .each { it.value.sort() } as Map<String, List<Version>>;

            List<Version> lowestVersions = versionsByArtifact.collect { it.value.first() }.sort()
            List<Version> highestVersions = versionsByArtifact.collect { it.value.last() }.sort()

            // Current align rule implementation is biased towards supporting the latest version use case
            // We can't assume that versions have been contiguous for all time, so we verify starting with the common lowest version
            Version lowestCommonVersion = lowestVersions.last()
            Version lowestVersion = lowestVersions.first()
            if (lowestCommonVersion != lowestVersion) {
                println("Warning: Align rule $rule isn't valid for the entire history of the artifacts it covers." +
                        " Lowest common version is $lowestCommonVersion, lowest ever version $lowestVersion")
            }
            Version highestVersion = highestVersions.last()

            def matchedVersions = versionsByArtifact.collectEntries { key, value ->
                [(key): value.findAll { it >= lowestCommonVersion }]
            } as Map<String, List<Version>>;

            def expectedVersions = matchedVersions.entrySet().first().value
            matchedVersions.entrySet().each {
                def artifact = it.key
                def versions = it.value
                assert !versions.isEmpty() : "Align rule $rule is invalid: $artifact has no versions between $lowestCommonVersion and $highestVersion"
                assert versions.last() == highestVersion : "Align rule $rule is invalid: $artifact does not have a version $highestVersion"
                assert versions == expectedVersions : "Align rule $rule is invalid: $artifact has versions $versions but expected $expectedVersions"
            }
        }
    }

    def 'artifacts exist for all module rules'() {
        expect:
        ruleSet.with {
            [deny, exclude, reject, replace, substitute].flatten().each { rule ->
                assert artifactsByRule.keySet().contains(rule) : "No artifacts found for rule $rule"
            }
        }
    }
}
