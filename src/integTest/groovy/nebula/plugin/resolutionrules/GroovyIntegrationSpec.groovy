package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class GroovyIntegrationSpec extends RulesBaseSpecification {
    def setup() {
        buildFile << """\
            dependencies {
                resolutionRules files('${new File('src/main/resources/align-groovy.json').absolutePath}', '${new File('src/main/resources/align-apache-groovy.json').absolutePath}', '${new File('src/main/resources/replace-groovy.json').absolutePath}')
            }
            """.stripIndent()
    }

    def 'check replacement works'() {
        buildFile << '''\
            dependencies {
                implementation 'org.codehaus.groovy:groovy:3.0.22'
                implementation 'org.codehaus.groovy:groovy-all:3.0.22'
                implementation 'org.codehaus.groovy:groovy-ant:3.0.22'
                implementation 'org.codehaus.groovy:groovy-astbuilder:3.0.22'
                implementation 'org.codehaus.groovy:groovy-backports-compat23:3.0.22'
                implementation 'org.codehaus.groovy:groovy-binary:3.0.22'
                implementation 'org.codehaus.groovy:groovy-bom:3.0.22'
                implementation 'org.codehaus.groovy:groovy-bsf:3.0.22'
                implementation 'org.codehaus.groovy:groovy-cli-commons:3.0.22'
                implementation 'org.codehaus.groovy:groovy-cli-picocli:3.0.22'
                implementation 'org.codehaus.groovy:groovy-console:3.0.22'
                implementation 'org.codehaus.groovy:groovy-contracts:3.0.22'
                implementation 'org.codehaus.groovy:groovy-datetime:3.0.22'
                implementation 'org.codehaus.groovy:groovy-dateutil:3.0.22'
                implementation 'org.codehaus.groovy:groovy-docgenerator:3.0.22'
                implementation 'org.codehaus.groovy:groovy-ginq:3.0.22'
                implementation 'org.codehaus.groovy:groovy-groovydoc:3.0.22'
                implementation 'org.codehaus.groovy:groovy-groovysh:3.0.22'
                implementation 'org.codehaus.groovy:groovy-jaxb:3.0.22'
                implementation 'org.codehaus.groovy:groovy-jmx:3.0.22'
                implementation 'org.codehaus.groovy:groovy-json:3.0.22'
                implementation 'org.codehaus.groovy:groovy-jsr223:3.0.22'
                implementation 'org.codehaus.groovy:groovy-macro:3.0.22'
                implementation 'org.codehaus.groovy:groovy-macro-library:3.0.22'
                implementation 'org.codehaus.groovy:groovy-nio:3.0.22'
                implementation 'org.codehaus.groovy:groovy-servlet:3.0.22'
                implementation 'org.codehaus.groovy:groovy-sql:3.0.22'
                implementation 'org.codehaus.groovy:groovy-swing:3.0.22'
                implementation 'org.codehaus.groovy:groovy-templates:3.0.22'
                implementation 'org.codehaus.groovy:groovy-test:3.0.22'
                implementation 'org.codehaus.groovy:groovy-test-junit5:3.0.22'
                implementation 'org.codehaus.groovy:groovy-testng:3.0.22'
                implementation 'org.codehaus.groovy:groovy-toml:3.0.22'
                implementation 'org.codehaus.groovy:groovy-typecheckers:3.0.22'
                implementation 'org.codehaus.groovy:groovy-xml:3.0.22'
                implementation 'org.codehaus.groovy:groovy-yaml:3.0.22'

                implementation 'org.apache.groovy:groovy:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-all:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-ant:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-astbuilder:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-backports-compat23:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-binary:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-bom:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-bsf:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-cli-commons:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-cli-picocli:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-console:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-contracts:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-datetime:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-dateutil:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-docgenerator:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-ginq:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-groovydoc:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-groovysh:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-jaxb:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-jmx:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-json:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-jsr223:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-macro:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-macro-library:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-nio:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-servlet:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-sql:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-swing:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-templates:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-test:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-test-junit5:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-testng:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-toml:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-typecheckers:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-xml:4.0.0-alpha-1'
                implementation 'org.apache.groovy:groovy-yaml:4.0.0-alpha-1'
            }
            '''.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains('org.codehaus.groovy:groovy:3.0.22 -> org.apache.groovy:groovy:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-all:3.0.22 -> org.apache.groovy:groovy-all:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-ant:3.0.22 -> org.apache.groovy:groovy-ant:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-astbuilder:3.0.22 -> org.apache.groovy:groovy-astbuilder:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-backports-compat23:3.0.22 -> org.apache.groovy:groovy-backports-compat23:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-binary:3.0.22 -> org.apache.groovy:groovy-binary:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-bom:3.0.22 -> org.apache.groovy:groovy-bom:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-bsf:3.0.22 -> org.apache.groovy:groovy-bsf:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-cli-commons:3.0.22 -> org.apache.groovy:groovy-cli-commons:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-cli-picocli:3.0.22 -> org.apache.groovy:groovy-cli-picocli:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-console:3.0.22 -> org.apache.groovy:groovy-console:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-contracts:3.0.22 -> org.apache.groovy:groovy-contracts:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-datetime:3.0.22 -> org.apache.groovy:groovy-datetime:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-dateutil:3.0.22 -> org.apache.groovy:groovy-dateutil:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-docgenerator:3.0.22 -> org.apache.groovy:groovy-docgenerator:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-ginq:3.0.22 -> org.apache.groovy:groovy-ginq:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-groovydoc:3.0.22 -> org.apache.groovy:groovy-groovydoc:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-groovysh:3.0.22 -> org.apache.groovy:groovy-groovysh:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-jaxb:3.0.22 -> org.apache.groovy:groovy-jaxb:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-jmx:3.0.22 -> org.apache.groovy:groovy-jmx:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-json:3.0.22 -> org.apache.groovy:groovy-json:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-jsr223:3.0.22 -> org.apache.groovy:groovy-jsr223:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-macro:3.0.22 -> org.apache.groovy:groovy-macro:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-macro-library:3.0.22 -> org.apache.groovy:groovy-macro-library:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-nio:3.0.22 -> org.apache.groovy:groovy-nio:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-servlet:3.0.22 -> org.apache.groovy:groovy-servlet:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-sql:3.0.22 -> org.apache.groovy:groovy-sql:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-swing:3.0.22 -> org.apache.groovy:groovy-swing:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-templates:3.0.22 -> org.apache.groovy:groovy-templates:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-test:3.0.22 -> org.apache.groovy:groovy-test:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-test-junit5:3.0.22 -> org.apache.groovy:groovy-test-junit5:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-testng:3.0.22 -> org.apache.groovy:groovy-testng:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-toml:3.0.22 -> org.apache.groovy:groovy-toml:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-typecheckers:3.0.22 -> org.apache.groovy:groovy-typecheckers:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-xml:3.0.22 -> org.apache.groovy:groovy-xml:4.0.0-alpha-1')
        result.output.contains('org.codehaus.groovy:groovy-yaml:3.0.22 -> org.apache.groovy:groovy-yaml:4.0.0-alpha-1')
    }

    def 'check alignment works'() {
        buildFile << '''\
            dependencies {
                implementation 'org.codehaus.groovy:groovy-toml:3.0.22'
                implementation 'org.apache.groovy:groovy:4.0.22'
                implementation 'org.apache.groovy:groovy-toml:4.0.0-alpha-1'
            }
            '''.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains('org.apache.groovy:groovy:4.0.22')
        result.output.contains('org.codehaus.groovy:groovy-toml:3.0.22 -> org.apache.groovy:groovy-toml:4.0.22')
    }
}
