package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class AlignJacksonSpec extends RulesBaseSpecification {

    def setup() {
        def resolutionRulesFile = getClass().getClassLoader().getResource('align-jackson.json')
        buildFile << """
            dependencies {
              resolutionRules files('$resolutionRulesFile')
            }
            """.stripIndent()
    }

    def 'can align jackson 2.x libraries'() {
        given:
        buildFile << """\
            dependencies {
              implementation 'com.fasterxml.jackson.core:jackson-core:2.19.1'
              implementation 'com.fasterxml.jackson.core:jackson-annotations:2.17.0'
              implementation 'com.fasterxml.jackson.core:jackson-databind:2.18.0'
              implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0'
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'com.fasterxml.jackson')

        then:
        result.output.contains('By constraint: belongs to platform aligned-platform:align-jackson-0-for-com.fasterxml.jackson.core-or-dataformat-or-datatype-or-jaxrs-or-jr-or-module:2.19.1')
        def alignedVersion = "2.19.1"
        // aligned
        result.output.contains("com.fasterxml.jackson.core:jackson-core:$alignedVersion\n")
        result.output.contains("com.fasterxml.jackson.core:jackson-annotations:$alignedVersion\n")
        result.output.contains("com.fasterxml.jackson.core:jackson-databind:$alignedVersion\n")
        result.output.contains("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$alignedVersion\n")
        !result.output.contains('FAILED')
    }

    def 'can align jackson 2.20.x libraries with annotations change'() {
        // 2.20.x dropped the patch version for the annotations library:
        // https://github.com/FasterXML/jackson-annotations/issues/294

        given:
        buildFile << """\
            dependencies {
              implementation enforcedPlatform('com.fasterxml.jackson:jackson-bom:2.20.0')
              implementation 'com.fasterxml.jackson.core:jackson-core:2.20.0'
              implementation 'com.fasterxml.jackson.core:jackson-annotations:2.17.0'
              implementation 'com.fasterxml.jackson.core:jackson-databind:2.18.0'
              implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.19.0'
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'com.fasterxml.jackson')

        then:
        result.output.contains('By constraint: belongs to platform aligned-platform:align-jackson-0-for-com.fasterxml.jackson.core-or-dataformat-or-datatype-or-jaxrs-or-jr-or-module:2.20.0')
        def alignedVersion = "2.20.0"
        def alignedVersionAnno = "2.20"
        // aligned
        result.output.contains("com.fasterxml.jackson.core:jackson-core:$alignedVersion\n")
        result.output.contains("com.fasterxml.jackson.core:jackson-annotations:$alignedVersionAnno\n")
        result.output.contains("com.fasterxml.jackson.core:jackson-databind:$alignedVersion\n")
        result.output.contains("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$alignedVersion\n")
        !result.output.contains('FAILED')
    }
}
