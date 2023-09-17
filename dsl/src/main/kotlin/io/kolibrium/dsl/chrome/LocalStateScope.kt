/*
 * Copyright 2023 Attila Fazekas
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

package io.kolibrium.dsl.chrome

import io.kolibrium.dsl.UnaryPlus

public class LocalStateScope {
    internal val localStatePrefs = mutableMapOf<String, Any>()

    public fun browserEnabledLabsExperiments(block: BrowserEnabledLabsExperiments.() -> Unit) {
        val experiments = BrowserEnabledLabsExperiments().apply(block)
        if (experiments.experimentalFlags.isNotEmpty()) {
            localStatePrefs["browser.enabled_labs_experiments"] = experiments.experimentalFlags
        }
    }
}

public class BrowserEnabledLabsExperiments : UnaryPlus<ExperimentalFlag> {
    internal val experimentalFlags = mutableSetOf<String>()

    override fun ExperimentalFlag.unaryPlus() {
        experimentalFlags.add(this.name)
    }
}

@JvmInline
public value class ExperimentalFlag(public val name: String)

public object ExperimentalFlags {
    public val same_site_by_default_cookies: ExperimentalFlag = ExperimentalFlag("same-site-by-default-cookies@2")
    public val cookies_without_same_site_must_be_secure: ExperimentalFlag =
        ExperimentalFlag("cookies-without-same-site-must-be-secure@2")
}
