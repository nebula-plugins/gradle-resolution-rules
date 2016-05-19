Gradle Resolution Rules
=======

![Support Status](https://img.shields.io/badge/nebula-supported-brightgreen.svg)
![Version](https://img.shields.io/maven-central/v/com.netflix.nebula/gradle-resolution-rules.svg)
[![Build Status](https://travis-ci.org/nebula-plugins/gradle-resolution-rules.svg?branch=master)](https://travis-ci.org/nebula-plugins/gradle-resolution-rules)
[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/gradle-metrics-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Rules for the [Gradle Resolution Rules plugin](https://github.com/nebula-plugins/gradle-resolution-rules-plugin).

This project includes rules for OSS libraries available in Maven Central and other public repositories.

# Using the rules

Add a dependency on this project to apply the rules to the project:

    dependencies {
        resolutionRules 'com.netflix.nebula:gradle-resolution-rules:latest.release'
    }

# Included rules

Refer to the JSON files in `src/main/resources` for details of the included rules. Optional rules are documented below.

# Optional rules

Optional rules can be enabled by adding the name of the rule set to the list of `include`d rules:

    nebulaResolutionRules {
        optional = ['slf4j-bridge']
    }

| Rule Set Name | Description   |
| ------------- |:-------------:|
| `align-spring` | Aligns all current `org.springframework` and `org.springframework.integration` |
| `align-jackson` | Aligns all current `com.fasterxml.jackson.*` libraries |
| `replace-bouncycastle` | Replaces `bouncycastle` group dependencies with `org.bouncycastle` |
| `slf4j-bridge` | Replaces concrete logging implementations with SLF4J bridges |

# Contributing Rules

Contributions are more than welcome, however please keep these guidelines in mind when submitting a pull request:

- Default rules are intended to offer _correctness_. For example, a rule that tells Gradle that Google Collections was replaced by Guava provides correctness, as it causes those libraries with overlapping classes to conflict resolve
- Optional rules are intended for rules that might cause problems due to edge cases, or provide a useful opinion. For instance, replacing Log4J with the SLF4J bridge is an opinion, but useful and correct when using SLF4J
- Opinions that prefer one library over another, when it's not a compatible replacement (see 'correctness' above) are not suitable rules for this project
- Rules should be for libraries in commonly used, public repositories, that have broad use. For esoteric use cases, we recommend publishing your own rules using the [Producing rules](https://github.com/nebula-plugins/gradle-resolution-rules-plugin#producing-rules) instructions.

LICENSE
=======

Copyright 2016 Netflix, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
