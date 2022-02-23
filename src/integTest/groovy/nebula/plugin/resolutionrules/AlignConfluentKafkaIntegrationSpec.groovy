package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class AlignConfluentKafkaIntegrationSpec extends RulesBaseSpecification {
    def setup() {
        def resolutionRulesFile = getClass().getClassLoader().getResource('align-confluent-kafka.json')
        buildFile << """
            dependencies {
                resolutionRules files('$resolutionRulesFile')
            }
            """
    }

    def 'can align confluent kafka libraries'() {
        given:
        buildFile << """
            dependencies {
                implementation 'io.confluent:kafka-connect-avro-converter:6.0.0'
                implementation 'io.confluent:kafka-streams-avro-serde:7.0.0'
            }

            repositories {
                maven {
                    url 'https://packages.confluent.io/maven/'
                }
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'kafka')

        then:
        result.output.contains('By constraint : belongs to platform aligned-platform:align-confluent-kafka-0-for-io.confluent:7.0.0')
        result.output.contains('io.confluent:kafka-connect-avro-converter:6.0.0 -> 7.0.0')
        result.output.contains('io.confluent:kafka-streams-avro-serde:7.0.0')
    }
}
