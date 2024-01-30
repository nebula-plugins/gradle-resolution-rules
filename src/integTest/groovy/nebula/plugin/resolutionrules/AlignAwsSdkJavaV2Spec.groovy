package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class AlignAwsSdkJavaV2Spec extends RulesBaseSpecification {

    def setup() {
        def resolutionRulesFile = getClass().getClassLoader().getResource('align-aws-sdk-java-v2.json')
        buildFile << """
            dependencies {
              resolutionRules files('$resolutionRulesFile')
            }
            """.stripIndent()
    }

    def 'can align software.amazon.awssdk libraries'() {
        given:
        buildFile << """\
            dependencies {
              implementation 'software.amazon.awssdk:aws-core:2.22.0'
              implementation 'software.amazon.awssdk:ec2:2.23.9'
              implementation 'software.amazon.awssdk:sqs:2.20.136'
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'software.amazon.awssdk')

        then:
        result.output.contains('By constraint: belongs to platform aligned-platform:align-aws-sdk-java-v2-0-for-software.amazon.awssdk:2.23.9')
        def alignedVersion = "2.23.9"
        // aligned
        result.output.contains("software.amazon.awssdk:aws-core:$alignedVersion\n")
        result.output.contains("software.amazon.awssdk:ec2:$alignedVersion\n")
        result.output.contains("software.amazon.awssdk:sqs:$alignedVersion\n")
        !result.output.contains('FAILED')
    }
}
