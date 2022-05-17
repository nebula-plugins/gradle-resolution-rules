package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class AlignApacheKafkaIntegrationSpec extends RulesBaseSpecification {
    def setup() {
        def resolutionRulesFile = getClass().getClassLoader().getResource('align-apache-kafka.json')
        buildFile << """
            dependencies {
                resolutionRules files('$resolutionRulesFile')
            }
            """
    }

    def 'can align apache kafka libraries'() {
        given:
        buildFile << """
            dependencies {
                implementation 'org.apache.kafka:connect-runtime:2.0.0'
                implementation 'org.apache.kafka:kafka-clients:3.0.0'
                implementation 'org.apache.kafka:kafka-streams:1.0.0'
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'kafka')

        then:
        result.output.contains('By constraint: belongs to platform aligned-platform:align-apache-kafka-0-for-org.apache.kafka:3.0.0')
        result.output.contains('org.apache.kafka:connect-runtime:2.0.0 -> 3.0.0')
        result.output.contains('org.apache.kafka:kafka-clients:3.0.0')
        result.output.contains('org.apache.kafka:kafka-streams:1.0.0 -> 3.0.0')
    }
}
