package nebula.plugin.resolutionrules

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.rules.TestName
import spock.lang.Specification


abstract class RulesBaseSpecification extends Specification {
    @org.junit.Rule
    final TestName testName = new TestName()

    File projectDir
    File buildFile
    File settingsFile

    def setup() {
        projectDir = new File("build/nebulatest/${this.class.canonicalName}/${testName.methodName.replaceAll(/\W+/, '-')}").absoluteFile
        if (projectDir.exists()) {
            projectDir.deleteDir()
        }
        projectDir.mkdirs()

        buildFile = new File(projectDir, 'build.gradle')
        settingsFile = new File(projectDir, 'settings.gradle')
        settingsFile << '''\
            '''.stripIndent()

        buildFile << '''\
            buildscript {
              repositories {
                maven {
                  url "https://plugins.gradle.org/m2/"
                }
              }
              dependencies {
                classpath "com.netflix.nebula:gradle-resolution-rules-plugin:latest.release"
              }
            }
            apply plugin: 'nebula.resolution-rules'
            apply plugin: 'java'
            
            repositories {
                mavenCentral()
            }
            '''.stripIndent()
    }

    BuildResult runWithArgumentsSuccessfully(String... args) {
        List<String> finalArgs = args
        if(System.getenv("CI") && !finalArgs.contains('-g') && !finalArgs.contains('--gradle-user-home')) {
            String testWorkerNumber = System.getProperty('org.gradle.test.worker')
            String gradleHomeDir = System.getenv('WORKSPACE') ? "${System.getenv('WORKSPACE')}/.gradle-test-worker-$testWorkerNumber-home" : "${System.getProperty("user.home")}/.gradle-test-worker-$testWorkerNumber-home"
            finalArgs.add('-g')
            finalArgs.add(gradleHomeDir.toString())
        }
        GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(finalArgs)
                .build()
    }
}
