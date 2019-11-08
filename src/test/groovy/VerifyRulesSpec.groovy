import nebula.test.IntegrationSpec
import spock.lang.Ignore

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
            implementation 'com.google.guava:guava:19.0'

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
            implementation 'com.fasterxml.jackson.core:jackson-core:2.9.0.pr1'
            implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.8.6'

            resolutionRules fileTree('$rulesDir').include('align-jackson.json')
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath', '-d')

        then:
        assert rulesFiles.size() > 0
        result.standardOutput.contains('com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.8.6 -> 2.9.0.pr1\n')
    }

    def 'jackson databind 2.8.8.1 first'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()

        buildFile << """
        apply plugin: 'java'
        apply plugin: 'nebula.resolution-rules'

        repositories {
            jcenter()
        }

        dependencies {
            implementation 'com.fasterxml.jackson.core:jackson-databind:2.8.8.1'
            implementation 'com.fasterxml.jackson.core:jackson-core:2.8.2'

            resolutionRules fileTree('$rulesDir').include('align-jackson.json')
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        assert rulesFiles.size() > 0
        result.standardOutput.contains('com.fasterxml.jackson.core:jackson-databind:2.8.8.1\n')
        result.standardOutput.contains('com.fasterxml.jackson.core:jackson-core:2.8.2 -> 2.8.8\n')
    }

    def 'jackson databind 2.8.8.1 last'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()

        buildFile << """
        apply plugin: 'java'
        apply plugin: 'nebula.resolution-rules'

        repositories {
            jcenter()
        }

        dependencies {
            implementation 'com.fasterxml.jackson.core:jackson-core:2.8.2'
            implementation 'com.fasterxml.jackson.core:jackson-databind:2.8.8.1'

            resolutionRules fileTree('$rulesDir').include('align-jackson.json')
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        assert rulesFiles.size() > 0
        result.standardOutput.contains('com.fasterxml.jackson.core:jackson-databind:2.8.8.1\n')
        result.standardOutput.contains('com.fasterxml.jackson.core:jackson-core:2.8.2 -> 2.8.8\n')
    }

    @Ignore
    def 'jackson databind 2.8.8.1 last should fail with compile configuration'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()

        buildFile << """
        apply plugin: 'java'
        apply plugin: 'nebula.resolution-rules'

        repositories {
            jcenter()
        }

        dependencies {
            implementation 'com.fasterxml.jackson.core:jackson-core:2.8.2'
            implementation 'com.fasterxml.jackson.core:jackson-databind:2.8.8.1'

            resolutionRules fileTree('$rulesDir').include('align-jackson.json')
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compile')

        then:
        assert rulesFiles.size() > 0
        result.standardOutput.contains('com.fasterxml.jackson.core:jackson-databind:2.8.8.1\n')
        result.standardOutput.contains('com.fasterxml.jackson.core:jackson-core:2.8.2 -> 2.8.8\n')
    }
}
