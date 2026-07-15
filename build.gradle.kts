/*
 * Copyright 2016-2019 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import nebula.plugin.contacts.Contact

plugins {
    id("com.netflix.nebula.root")
    id("com.netflix.nebula.plugin-plugin")
    id("java-library")
    id("com.netflix.nebula.integtest")
    id("groovy")
}

repositories {
    maven(url = "https://jitpack.io")
}

description = "Rules for the Resolution Rules plugin"

contacts {
    (addPerson("nebula-plugins-oss@netflix.com") as Contact).apply {
        moniker = "Nebula Plugins Maintainers"
        github = "nebula-plugins"
    }
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
tasks.named("publishPlugins") { enabled = false }
configurations {
    create("testKitPlugins")
}
dependencies {
    testImplementation("org.junit.vintage:junit-vintage-engine")
    testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
    testImplementation("org.spockframework:spock-junit4:2.3-groovy-4.0")
    add("testKitPlugins", "com.netflix.nebula:gradle-resolution-rules-plugin:latest.release")
    testImplementation("com.netflix.nebula:gradle-resolution-rules-plugin:latest.release")
    testImplementation("com.netflix.nebula:nebula-test:latest.release")

    testImplementation("org.apache.maven.indexer:indexer-core:6.0.0")
    testImplementation("org.eclipse.sisu:org.eclipse.sisu.plexus:0.3.3")
    testImplementation("org.sonatype.sisu:sisu-guice:3.2.6")
    testImplementation("org.apache.maven.wagon:wagon-http:2.10")
    testImplementation("org.apache.lucene:lucene-core:5.5.4")
    testImplementation("org.apache.lucene:lucene-queryparser:5.5.4")
    testImplementation("org.apache.lucene:lucene-analyzers-common:5.5.4")
    testImplementation("org.apache.lucene:lucene-highlighter:5.5.4")
    testImplementation("org.apache.lucene:lucene-backward-codecs:5.5.4")
    testImplementation("com.github.everit-org.json-schema:org.everit.json.schema:1.8.0")
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter()
        }
    }
}

tasks.named<PluginUnderTestMetadata>("pluginUnderTestMetadata") {
    pluginClasspath.from(configurations.named("testKitPlugins"))
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = "9.6.1"
    distributionSha256Sum = "9c0f7faeeb306cb14e4279a3e084ca6b596894089a0638e68a07c945a32c9e14"
}
