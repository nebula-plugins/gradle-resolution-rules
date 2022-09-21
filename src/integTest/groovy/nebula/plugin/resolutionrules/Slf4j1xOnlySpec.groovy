package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult
import spock.lang.Unroll

class Slf4j1xOnlySpec extends RulesBaseSpecification {
    def setup() {
        def ruleFile = new File(getClass().getResource('/optional-slf4j1x-only.json').toURI())
        buildFile << """\
            dependencies {
                resolutionRules files('${ruleFile.absolutePath}')
            }
            
            nebulaResolutionRules {
                optional = ['slf4j1x-only']
            }
            """.stripIndent()
    }

    @Unroll
    def 'downgrade slf4j: #version'() {
        given:
        buildFile << """\
            dependencies {
                implementation "org.slf4j:slf4j-api:${version}"
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains "org.slf4j:slf4j-api:$version -> 1.7.36"

        where:
        version << ['2.0.0', '2.0.1']
    }

    @Unroll
    def 'remove slf4j 2.x log4j implementation'() {
        given:
        buildFile << """\
            dependencies {
                implementation "org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0"
                implementation "org.apache.logging.log4j:log4j-slf4j-impl:2.19.0"
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains "org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0 -> org.apache.logging.log4j:log4j-slf4j-impl:2.19.0"
    }
}
