package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class AlignSpockframeworkSpec extends RulesBaseSpecification {

    def setup() {
        def resolutionRulesFile = getClass().getClassLoader().getResource('align-spockframework.json')
        buildFile << """
            dependencies {
              resolutionRules files('$resolutionRulesFile')
            }
            """.stripIndent()
    }

    def 'can align org.spockframework libraries'() {
        given:
        buildFile << """\
            dependencies {
              implementation 'org.spockframework:spock-core:2.0-groovy-3.0'
              implementation 'org.spockframework:spock-guice:1.3-groovy-2.5'
              implementation 'org.spockframework:spock-junit4:2.0-groovy-2.5'
              implementation 'org.spockframework:spock-spring:1.3-groovy-2.5'
              implementation 'org.spockframework:spock-tapestry:1.3-groovy-2.5'
              implementation 'org.spockframework:spock-unitils:1.3-groovy-2.5'
              
              implementation 'org.spockframework:spock-groovy2-compat:2.0-groovy-2.5'
              implementation 'org.spockframework:spock-grails-support:0.7-groovy-2.0'
              implementation 'org.spockframework:spock-maven:0.7-groovy-2.0'
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dI', '--dependency', 'org.spockframework')

        then:
        result.output.contains('By constraint: belongs to platform aligned-platform:align-spockframework-0-for-org.spockframework:2.0-groovy-3.0')
        def alignedVersion = "2.0-groovy-3.0"
        // aligned
        result.output.contains("org.spockframework:spock-core:$alignedVersion\n")
        result.output.contains("org.spockframework:spock-guice:$alignedVersion\n")
        result.output.contains("org.spockframework:spock-spring:$alignedVersion\n")
        result.output.contains("org.spockframework:spock-junit4:$alignedVersion\n")
        result.output.contains("org.spockframework:spock-tapestry:$alignedVersion\n")
        result.output.contains("org.spockframework:spock-unitils:$alignedVersion\n")
        !result.output.contains('FAILED')

        // not aligned
        result.output.contains('org.spockframework:spock-groovy2-compat:2.0-groovy-2.5\n')
        result.output.contains('org.spockframework:spock-grails-support:0.7-groovy-2.0\n')
        result.output.contains('org.spockframework:spock-maven:0.7-groovy-2.0\n')
    }
}
