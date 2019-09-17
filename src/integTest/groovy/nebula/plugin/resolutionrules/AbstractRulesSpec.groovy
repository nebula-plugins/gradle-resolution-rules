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

import nebula.test.IntegrationTestKitSpec

class AbstractRulesSpec extends IntegrationTestKitSpec {
    def setup() {
        buildFile << """\
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
            """.stripIndent()
        keepFiles = true
        debug = true
    }
}
