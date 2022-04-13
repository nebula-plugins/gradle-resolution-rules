package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult
import spock.lang.Unroll

class SubstituteSnakeyamlSpec extends RulesBaseSpecification {
    def setup() {
        def ruleFile = new File(getClass().getResource('/substitute-snakeyaml.json').toURI())
        buildFile << """\
            dependencies {
                resolutionRules files('${ruleFile.absolutePath}')
            }
            """.stripIndent()
    }

    @Unroll
    def 'substitute version from #snakeyamlVersion to 1.29'() {
        given:
        buildFile << """\
            dependencies {
                implementation "org.yaml:snakeyaml:${snakeyamlVersion}"
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains "org.yaml:snakeyaml:1.30 -> 1.29"

        where:
        snakeyamlVersion << ['1.30']
    }

    @Unroll
    def 'do not substitute other versions'() {
        given:
        buildFile << """\
            dependencies {
                implementation "org.yaml:snakeyaml:${snakeyamlVersion}"
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains "org.yaml:snakeyaml:1.29"

        where:
        snakeyamlVersion << ['1.29']
    }
}
