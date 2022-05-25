package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class CodehausJacksonSpec extends RulesBaseSpecification {
    def setup() {
        def ruleFile = new File(getClass().getResource('/codehaus-jackson.json').toURI())
        buildFile << """\
            dependencies {
                resolutionRules files('${ruleFile.absolutePath}')
            }
            """.stripIndent()
    }

    def 'duplicates are replaced and aligned'() {
        given:
        buildFile << """\
            dependencies {
                implementation "org.codehaus.jackson:jackson-core-lgpl:1.9.13"
                implementation "org.codehaus.jackson:jackson-core-asl:1.9.13"
                implementation "org.codehaus.jackson:jackson-mapper-asl:1.9.12"
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains "org.codehaus.jackson:jackson-core-lgpl:1.9.13 -> org.codehaus.jackson:jackson-core-asl:1.9.13"
        result.output.contains "org.codehaus.jackson:jackson-mapper-asl:1.9.12 -> 1.9.13"
    }
}
