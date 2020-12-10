package nebula.plugin.resolutionrules

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
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


    void writeJavaSourceFile(String source, File projectDir  = getProjectDir()) {
        writeJavaSourceFile(source, 'src/main/java', projectDir)
    }

    void writeJavaSourceFile(String source, String sourceFolderPath, File projectDir = getProjectDir()) {
        File javaFile = createFile(sourceFolderPath + '/' + fullyQualifiedName(source).replaceAll(/\./, '/') + '.java', projectDir)
        javaFile.text = source
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    File createFile(String path, File baseDir = getProjectDir()) {
        File file = file(path, baseDir)
        if (!file.exists()) {
            assert file.parentFile.mkdirs() || file.parentFile.exists()
            file.createNewFile()
        }
        file
    }

    File file(String path, File baseDir = getProjectDir()) {
        def splitted = path.split('/')
        def directory = splitted.size() > 1 ? directory(splitted[0..-2].join('/'), baseDir) : baseDir
        def file = new File(directory, splitted[-1])
        file.createNewFile()
        file
    }


    File directory(String path, File baseDir = getProjectDir()) {
        new File(baseDir, path).with {
            mkdirs()
            it
        }
    }

    private String fullyQualifiedName(String sourceStr) {
        def pkgMatcher = sourceStr =~ /\s*package\s+([\w\.]+)/
        def pkg = pkgMatcher.find() ? (pkgMatcher[0] as List<String>)[1] + '.' : ''

        def classMatcher = sourceStr =~ /\s*(class|interface)\s+(\w+)/
        return classMatcher.find() ? pkg + (classMatcher[0] as List<String>)[2] : null
    }
}
