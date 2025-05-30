package nebula.plugin.resolutionrules

class SpringBootLog4j2Spec extends RulesBaseSpecification {

    def setup() {
        def ruleFile = new File(getClass().getResource('/optional-spring-boot-log4j2.json').toURI())
        buildFile << """\
            dependencies {
                resolutionRules files('${ruleFile.absolutePath}')
            }
            
            nebulaResolutionRules {
                optional = ['spring-boot-log4j2']
            }
            """.stripIndent()
    }

    def 'check slf4j-reload4j replacement works'() {
        buildFile << '''\
            dependencies {
                implementation 'org.slf4j:slf4j-reload4j:latest.release'
                implementation 'org.apache.logging.log4j:log4j-slf4j2-impl:latest.release'
            }
            '''.stripIndent()

        when:
        def result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains 'org.slf4j:slf4j-reload4j:latest.release -> org.apache.logging.log4j:log4j-slf4j2-impl:'
    }

    def 'leaves slf4j-reload4j if log4j-slf4j2-impl is not present'() {
        buildFile << '''\
            dependencies {
                implementation 'org.slf4j:slf4j-reload4j:latest.release'
            }
            '''.stripIndent()

        when:
        def result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        !result.output.contains('org.slf4j:slf4j-reload4j:latest.release -> org.apache.logging.log4j:log4j-slf4j2-impl:')
    }

}
