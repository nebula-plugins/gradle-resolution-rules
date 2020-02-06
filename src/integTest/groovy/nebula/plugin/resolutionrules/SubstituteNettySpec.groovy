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

class SubstituteNettySpec extends AbstractRulesSpec {
    def setup() {
        buildFile << """\
            dependencies {
                resolutionRules files('${new File("src/main/resources/substitute-netty.json").absolutePath}')
            }
            """.stripIndent()
    }

    @Unroll
    def "substitution range for #declaredVersion"() {
        given:
        buildFile << """
            dependencies {
                implementation "io.netty:netty-codec-http:${declaredVersion}"
            }
            """.stripIndent()

        when:
        def result = runTasks('dependencies', '--configuration', 'compileClasspath')

        then:
        !result.output.contains("FAIL")
        !result.output.contains('io.netty:netty-codec-http:4.1.44.Final\n')
        result.output.contains(output)

        where:
        declaredVersion | output
        '4.1.44.Final'  | 'io.netty:netty-codec-http:4.1.44.Final -> 4.1.45.Final'
        '4.1.45.Final'  | 'io.netty:netty-codec-http:4.1.45.Final'
        '4.+'           | 'io.netty:netty-codec-http:4.'
    }
}
