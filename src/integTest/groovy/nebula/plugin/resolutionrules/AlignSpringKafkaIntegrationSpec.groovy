package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class AlignSpringKafkaIntegrationSpec extends RulesBaseSpecification {

    def setup() {
        def resolutionRulesFile = getClass().getClassLoader().getResource('align-spring-kafka.json')
        buildFile << """
            dependencies {
              resolutionRules files('$resolutionRulesFile')
            }
            """.stripIndent()
    }

    def 'can align spring kafka libraries'() {
        given:
        buildFile << """\
            dependencies {
              implementation 'org.springframework.kafka:spring-kafka:2.6.3'
              implementation 'org.springframework.kafka:spring-kafka-test:2.6.1'
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'spring-kafka')

        then:
        result.output.contains('By constraint : belongs to platform aligned-platform:align-spring-kafka-0-for-org.springframework.kafka:2.6.3')
        result.output.contains('org.springframework.kafka:spring-kafka-test:2.6.1 -> 2.6.3')
        result.output.contains('org.springframework.kafka:spring-kafka:2.6.3')
    }

    def 'can align spring kafka libraries with RELEASE in version'() {
        given:
        buildFile << """\
            dependencies {
              implementation 'org.springframework.kafka:spring-kafka:2.5.9.RELEASE'
              implementation 'org.springframework.kafka:spring-kafka-test:2.5.7.RELEASE'
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'spring-kafka')

        then:
        result.output.contains('By constraint : belongs to platform aligned-platform:align-spring-kafka-0-for-org.springframework.kafka:2.5.9.RELEASE')
        result.output.contains('org.springframework.kafka:spring-kafka-test:2.5.7.RELEASE -> 2.5.9.RELEASE')
        result.output.contains('org.springframework.kafka:spring-kafka:2.5.9.RELEASE')
    }
}
