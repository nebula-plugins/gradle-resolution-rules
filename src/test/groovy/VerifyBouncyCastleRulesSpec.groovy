import nebula.test.IntegrationSpec

class VerifyBouncyCastleRulesSpec extends IntegrationSpec {
    def rulesDir = new File('src/main/resources').absoluteFile

    def setup() {
        buildFile << """
        apply plugin: 'java'
        apply plugin: 'com.netflix.nebula.resolution-rules'

        repositories {
            mavenCentral()
        }

        dependencies {
            resolutionRules fileTree('${rulesDir}').include('replace-bouncycastle-with-*.json')
        }
        """.stripIndent()
    }

    def 'bcprov-jdk15on is replaced by bcprov-jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcprov-jdk15on:1.70'
            implementation 'org.bouncycastle:bcprov-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcprov-jdk15on:1.70 -> org.bouncycastle:bcprov-jdk18on:1.78.1')
    }

    def 'bcpkix-jdk15to18 is replaced by bcpkix-jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcpkix-jdk15to18:1.76'
            implementation 'org.bouncycastle:bcpkix-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcpkix-jdk15to18:1.76 -> org.bouncycastle:bcpkix-jdk18on:1.78.1')
    }

    def 'bcpg-jdk15on is replaced by bcpg-jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcpg-jdk15on:1.70'
            implementation 'org.bouncycastle:bcpg-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcpg-jdk15on:1.70 -> org.bouncycastle:bcpg-jdk18on:1.78.1')
    }

    def 'bctls-jdk15on is replaced by bctls-jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bctls-jdk15on:1.70'
            implementation 'org.bouncycastle:bctls-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bctls-jdk15on:1.70 -> org.bouncycastle:bctls-jdk18on:1.78.1')
    }

    def 'bcmail-jdk15on is replaced by bcmail-jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcmail-jdk15on:1.70'
            implementation 'org.bouncycastle:bcmail-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcmail-jdk15on:1.70 -> org.bouncycastle:bcmail-jdk18on:1.78.1')
    }

    def 'bcutil-jdk15on is replaced by bcutil-jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcutil-jdk15on:1.70'
            implementation 'org.bouncycastle:bcutil-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcutil-jdk15on:1.70 -> org.bouncycastle:bcutil-jdk18on:1.78.1')
    }

    def 'multiple jdk15on artifacts are replaced by jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcprov-jdk15on:1.70'
            implementation 'org.bouncycastle:bcpkix-jdk15on:1.70'
            implementation 'org.bouncycastle:bcpg-jdk15on:1.70'
            implementation 'org.bouncycastle:bcprov-jdk18on:1.78.1'
            implementation 'org.bouncycastle:bcpkix-jdk18on:1.78.1'
            implementation 'org.bouncycastle:bcpg-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcprov-jdk15on:1.70 -> org.bouncycastle:bcprov-jdk18on:1.78.1')
        result.standardOutput.contains('org.bouncycastle:bcpkix-jdk15on:1.70 -> org.bouncycastle:bcpkix-jdk18on:1.78.1')
        result.standardOutput.contains('org.bouncycastle:bcpg-jdk15on:1.70 -> org.bouncycastle:bcpg-jdk18on:1.78.1')
    }

    def 'mixed jdk15on and jdk15to18 artifacts are replaced by jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcprov-jdk15on:1.70'
            implementation 'org.bouncycastle:bcpkix-jdk15to18:1.76'
            implementation 'org.bouncycastle:bcutil-jdk15on:1.70'
            implementation 'org.bouncycastle:bcprov-jdk18on:1.78.1'
            implementation 'org.bouncycastle:bcpkix-jdk18on:1.78.1'
            implementation 'org.bouncycastle:bcutil-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcprov-jdk15on:1.70 -> org.bouncycastle:bcprov-jdk18on:1.78.1')
        result.standardOutput.contains('org.bouncycastle:bcpkix-jdk15to18:1.76 -> org.bouncycastle:bcpkix-jdk18on:1.78.1')
        result.standardOutput.contains('org.bouncycastle:bcutil-jdk15on:1.70 -> org.bouncycastle:bcutil-jdk18on:1.78.1')
    }

    def 'jdk15on and jdk15to18 of the same artifact both resolve to jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcprov-jdk15on:1.70'
            implementation 'org.bouncycastle:bcprov-jdk15to18:1.76'
            implementation 'org.bouncycastle:bcprov-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcprov-jdk15on:1.70 -> org.bouncycastle:bcprov-jdk18on:1.78.1')
        result.standardOutput.contains('org.bouncycastle:bcprov-jdk15to18:1.76 -> org.bouncycastle:bcprov-jdk18on:1.78.1')
    }

    def 'all bcpkix versions are replaced by bcpkix-jdk18on'() {
        buildFile << """
        dependencies {
            implementation 'org.bouncycastle:bcpkix-jdk14:1.46'
            implementation 'org.bouncycastle:bcpkix-jdk15on:1.70'
            implementation 'org.bouncycastle:bcpkix-jdk15to18:1.76'
            implementation 'org.bouncycastle:bcpkix-jdk18on:1.78.1'
        }
        """

        when:
        def result = runTasksSuccessfully('dependencies', '--configuration', 'compileClasspath')

        then:
        result.standardOutput.contains('org.bouncycastle:bcpkix-jdk14:1.46 -> org.bouncycastle:bcpkix-jdk18on:1.78.1')
        result.standardOutput.contains('org.bouncycastle:bcpkix-jdk15on:1.70 -> org.bouncycastle:bcpkix-jdk18on:1.78.1')
        result.standardOutput.contains('org.bouncycastle:bcpkix-jdk15to18:1.76 -> org.bouncycastle:bcpkix-jdk18on:1.78.1')
    }
}
