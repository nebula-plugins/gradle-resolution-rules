package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class AlignSpringFrameworkIntegrationSpec extends RulesBaseSpecification {

    def setup() {
        def resolutionRulesFile = getClass().getClassLoader().getResource('align-spring.json')
        buildFile << """
            dependencies {
              resolutionRules files('$resolutionRulesFile')
            }
            """.stripIndent()
    }

    def 'can align spring libraries'() {
        given:
        buildFile << """\
            dependencies {
              implementation ('org.springframework:spring-context:5.3.21') { transitive = false }
              implementation ('org.springframework:spring-orm:5.2.11.RELEASE') { transitive = false }
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'spring')

        then:
        result.output.contains('By constraint: belongs to platform aligned-platform:align-spring-0-for-org.springframework:5.3.21')
        result.output.contains('org.springframework:spring-orm:5.2.11.RELEASE -> 5.3.21')
        result.output.contains('org.springframework:spring-context:5.3.21')
    }
}
