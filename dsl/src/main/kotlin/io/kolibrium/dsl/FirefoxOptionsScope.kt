
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

package io.kolibrium.dsl

import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import java.io.File

@KolibriumDsl
public class FirefoxOptionsScope(override val options: FirefoxOptions) : OptionsScope() {

    public var binary: String? = null
    public var profileDir: String? = null

    override fun configure() {
        super.configure()
        options.apply {
            this@FirefoxOptionsScope.binary?.let { setBinary(it) }
            profileDir?.let { profile = FirefoxProfile(File(it)) }
        }
    }

    @KolibriumDsl
    public fun preferences(block: PreferencesScope<Firefox>.() -> Unit) {
        val preferencesScope = PreferencesScope<Firefox>().apply(block)
        if (preferencesScope.preferences.isNotEmpty()) {
            preferencesScope.preferences.forEach(options::addPreference)
        }
    }

    @KolibriumDsl
    public fun profile(block: FirefoxProfileScope.() -> Unit) {
        val ffProfileScope = FirefoxProfileScope().apply(block)
        if (ffProfileScope.preferences.isNotEmpty()) {
            val profile = FirefoxProfile()
            ffProfileScope.preferences.forEach(profile::setPreference)
            options.profile = profile
        }
    }
}