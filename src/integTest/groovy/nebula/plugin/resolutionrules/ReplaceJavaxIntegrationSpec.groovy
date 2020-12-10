package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult

class ReplaceJavaxIntegrationSpec extends RulesBaseSpecification {
    def setup() {
        buildFile << """\
            dependencies {
                resolutionRules files('${new File('src/main/resources/replace-javax.json').absolutePath}')
            }
            """.stripIndent()
    }

    def 'check replacement works'() {
        buildFile << '''\
            dependencies {
                implementation 'javax.persistence:persistence-api:1.0'
                implementation 'javax.persistence:javax.persistence-api:2.2'
                implementation 'jakarta.persistence:jakarta.persistence-api:2.2.3'
            }
            '''.stripIndent()
        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains('javax.persistence:persistence-api:1.0 -> jakarta.persistence:jakarta.persistence-api:2.2.3')
        result.output.contains('javax.persistence:javax.persistence-api:2.2 -> jakarta.persistence:jakarta.persistence-api:2.2.3')
        result.output.contains('jakarta.persistence:jakarta.persistence-api:2.2.3')

        when:
        writeJavaSourceFile("""\
            package test.nebula.netflix.hello;
        
            import javax.persistence.*;
            import java.util.List;
            
            @Entity
            @Table(name = "my_table")
            public class MyClass {
            
                @Id
                @GeneratedValue(
                        generator = "id_seq",
                        strategy = GenerationType.SEQUENCE)
                private long id;
            
                @OneToMany(cascade = CascadeType.ALL, mappedBy = "output", orphanRemoval = true, fetch = FetchType.EAGER)
                private List<MyOtherClass> others;
            
            }

            """.stripIndent(), 'src/main/java', projectDir)

        writeJavaSourceFile("""\
            package test.nebula.netflix.hello;
        
            public class MyOtherClass {
                 String something;
            }
            
            """.stripIndent(), 'src/main/java', projectDir)

        then:
        runWithArgumentsSuccessfully('compileJava')
    }

    def 'uses `javax.persistence:javax.persistence-api` if `jakarta.persistence:jakarta.persistence-api` is not present'() {
        buildFile << '''\
            dependencies {
                implementation 'javax.persistence:javax.persistence-api:2.2'
            }
            '''.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains('javax.persistence:javax.persistence-api:2.2')
        !result.output.contains('jakarta.persistence:jakarta.persistence-api')
    }

    def 'uses `javax.persistence:persistence-api` if `jakarta.persistence:jakarta.persistence-api` is not present'() {
        buildFile << '''\
            dependencies {
                implementation 'javax.persistence:persistence-api:1.0'
            }
            '''.stripIndent()

        when:
        BuildResult result = runWithArgumentsSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.output.contains('javax.persistence:persistence-api:1.0')
        !result.output.contains('jakarta.persistence:jakarta.persistence-api')
    }
}
