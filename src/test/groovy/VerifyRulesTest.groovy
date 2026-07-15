import groovy.transform.CompileStatic
import nebula.test.dsl.GroovyTestProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static nebula.test.dsl.TestKitAssertions.assertThat

@CompileStatic
class VerifyRulesTest {
    @TempDir
    File projectDir

    @Test
    void 'rules apply'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()
        def runner = GroovyTestProjectBuilder.testProject(projectDir) {
            rootProject {
                plugins {
                    id("java")
                    id("com.netflix.nebula.resolution-rules")
                }
                repositories {
                    mavenCentral()
                }
                dependencies(
                        "implementation 'com.google.guava:guava:19.0'",
                        "resolutionRules fileTree('$rulesDir').include('*.json')"
                )
            }
        }

        def result = runner.run('dependencies', '--configuration', 'compileClasspath', "--debug")

        assertThat(rulesFiles).hasSizeGreaterThan(0)
        assertThat(result.output).contains('a dependency rules source')
    }

    @Test
    void 'jackson pr1'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()
        def runner = GroovyTestProjectBuilder.testProject(projectDir) {
            rootProject {
                plugins {
                    id("java")
                    id("com.netflix.nebula.resolution-rules")
                }
                repositories {
                    mavenCentral()
                }
                dependencies(
                        "implementation 'com.fasterxml.jackson.core:jackson-core:2.9.0.pr1'",
                        "implementation 'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.8.6'",
                        "resolutionRules fileTree('$rulesDir').include('align-jackson.json')"
                )
            }
        }

        def result = runner.run('dependencies', '--configuration', 'compileClasspath')

        assertThat(rulesFiles).hasSizeGreaterThan(0)
        assertThat(result.output)
                .contains('com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.8.6 -> 2.9.0.pr1\n')
    }

    @Test
    void 'jackson databind 2_8_8_1 first'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()
        def runner = GroovyTestProjectBuilder.testProject(projectDir) {
            rootProject {
                plugins {
                    id("java")
                    id("com.netflix.nebula.resolution-rules")
                }
                repositories {
                    mavenCentral()
                }
                dependencies(
                        "implementation 'com.fasterxml.jackson.core:jackson-databind:2.8.8.1'",
                        "implementation 'com.fasterxml.jackson.core:jackson-core:2.8.2'",
                        "resolutionRules fileTree('$rulesDir').include('align-jackson.json')"
                )
            }
        }

        def result = runner.run('dependencies', '--configuration', 'compileClasspath')

        assertThat(rulesFiles).hasSizeGreaterThan(0)

        assertThat(result.output)
                .contains('com.fasterxml.jackson.core:jackson-databind:2.8.8.1\n')
                .contains('com.fasterxml.jackson.core:jackson-core:2.8.2 -> 2.8.8\n')
    }

    @Test
    void 'jackson databind 2_8_8_1 last'() {
        def rulesDir = new File('src/main/resources').absoluteFile
        def rulesFiles = rulesDir.list()
        def runner = GroovyTestProjectBuilder.testProject(projectDir) {
            rootProject {
                plugins {
                    id("java")
                    id("com.netflix.nebula.resolution-rules")
                }
                repositories {
                    mavenCentral()
                }
                dependencies(
                        "implementation 'com.fasterxml.jackson.core:jackson-core:2.8.2'",
                        "implementation 'com.fasterxml.jackson.core:jackson-databind:2.8.8.1'",
                        "resolutionRules fileTree('$rulesDir').include('align-jackson.json')"
                )
            }
        }

        def result = runner.run('dependencies', '--configuration', 'compileClasspath')

        assertThat(rulesFiles).hasSizeGreaterThan(0)

        assertThat(result.output)
                .contains('com.fasterxml.jackson.core:jackson-databind:2.8.8.1\n')
                .contains('com.fasterxml.jackson.core:jackson-core:2.8.2 -> 2.8.8\n')
    }
}
