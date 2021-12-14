package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class SubstituteLog4J2Spec extends RulesBaseSpecification {
    def setup() {
        def ruleFile = new File(getClass().getResource('/substitute-log4j2.json').toURI())
        buildFile << """\
            dependencies {
                resolutionRules files('${ruleFile.absolutePath}')
            }
            """.stripIndent()
    }

    def 'substitute log4j version from #log4jVersion to 2.16.0'() {
        given:
        buildFile << """\
            dependencies {
                implementation "org.apache.logging.log4j:log4j-core:${log4jVersion}"
            }
            """.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains "org.apache.logging.log4j:log4j-core:${log4jVersion} -> 2.16.0"

        where:
        log4jVersion << [
                '2.9.1',
                '2.10.0',
                '2.11.2',
                '2.12.1',
                '2.13.3',
                '2.14.1',
                '2.15.0',
        ]
    }
}
