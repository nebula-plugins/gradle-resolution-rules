package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class ReplaceActivationSpec extends RulesBaseSpecification {
    def setup() {
        buildFile << """\
            dependencies {
                resolutionRules files('${new File('src/main/resources/replace-activation.json').absolutePath}')
            }
            """.stripIndent()
    }

    def 'check replacement works for com.sun.activation:jakarta.activation'() {
        buildFile << '''\
            dependencies {
                implementation 'javax.activation:activation:1.1'
                implementation 'com.sun.activation:jakarta.activation:1.2.2'
            }
            '''.stripIndent()
        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains('javax.activation:activation:1.1 -> com.sun.activation:jakarta.activation:1.2.2')
    }
}
