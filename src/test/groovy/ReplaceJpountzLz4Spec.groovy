package nebula.plugin.resolutionrules

import nebula.test.IntegrationSpec

class ReplaceJpountzLz4Spec extends IntegrationSpec {
    def setup() {
        def ruleFile = new File(getClass().getResource('/replace-jpountz-lz4.json').toURI())
        buildFile << """\
            apply plugin: 'java'
            apply plugin: 'nebula.resolution-rules'
            
            repositories {
                mavenCentral()
            }

            dependencies {
                resolutionRules files('${ruleFile.absolutePath}')
            }
            """.stripIndent()
    }

    def 'check replacement works'() {
        buildFile << '''\
            dependencies {
                implementation 'net.jpountz.lz4:lz4:latest.release'
                implementation 'org.lz4:lz4-java:latest.release'
            }
            '''.stripIndent()

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains 'net.jpountz.lz4:lz4:latest.release -> org.lz4:lz4-java:'
    }

    def 'leaves net.jpountz.lz4 if org.lz4:lz4-java is not present'() {
        buildFile << '''\
            dependencies {
                implementation 'net.jpountz.lz4:lz4:latest.release'
            }
            '''.stripIndent()

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        !result.standardOutput.contains('net.jpountz.lz4:lz4:latest.release -> org.lz4:lz4-java:')
    }
}
