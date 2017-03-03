import nebula.test.IntegrationSpec

class VerifyRulesSpec extends IntegrationSpec {
    def 'rules apply'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()

        buildFile << """
        apply plugin: 'java'
        apply plugin: 'nebula.resolution-rules'

        repositories {
            jcenter()
        }

        dependencies {
            compile 'com.google.guava:guava:19.0'

            resolutionRules fileTree('$rulesDir').include('*.json')
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compile', '--debug')

        then:
        assert rulesFiles.size() > 0
        result.standardOutput.contains('a dependency rules source')
    }

    def 'jackson pr1'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()

        buildFile << """
        apply plugin: 'java'
        apply plugin: 'nebula.resolution-rules'

        repositories {
            jcenter()
        }

        dependencies {
            compile 'com.fasterxml.jackson.core:jackson-core:2.9.0.pr1'
            compile 'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.8.6'

            resolutionRules fileTree('$rulesDir').include('align-jackson.json')
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compile')

        then:
        assert rulesFiles.size() > 0
        result.standardOutput.contains('com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.8.6 -> 2.9.0.pr1')
    }
}
