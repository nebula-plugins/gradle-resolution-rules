/**
 *
 *  Copyright 2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package nebula.plugin.resolutionrules

import spock.lang.Unroll

class SubstituteJacksonSpec extends AbstractRulesSpec {
    def setup() {
        buildFile << """\
            dependencies {
                resolutionRules files('${new File("src/main/resources/substitute-jackson.json").absolutePath}')
            }
            """.stripIndent()
    }

    @Unroll
    def "substitution range for #declaredVersion"() {
        given:
        buildFile << """
            dependencies {
                compile "com.fasterxml.jackson.core:jackson-databind:${declaredVersion}"
            }
            """.stripIndent()

        when:
        def result = runTasks('dependencies', '--configuration', 'compileClasspath')

        then:
        !result.output.contains("FAIL")
        result.output.contains(output)

        where:
        declaredVersion     | output
        '2.9.9'             | 'com.fasterxml.jackson.core:jackson-databind:2.9.9 -> 2.9.9.3'
        '2.9.9.2'           | 'com.fasterxml.jackson.core:jackson-databind:2.9.9.2 -> 2.9.9.3'
        '2.9.9.3'           | 'com.fasterxml.jackson.core:jackson-databind:2.9.9.3'
    }
}
