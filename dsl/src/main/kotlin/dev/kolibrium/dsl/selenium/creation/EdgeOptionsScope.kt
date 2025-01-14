
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

package dev.kolibrium.dsl.selenium.creation

import org.openqa.selenium.edge.EdgeOptions

@KolibriumDsl
public class EdgeOptionsScope(override val options: EdgeOptions) : ChromiumOptionsScope(options) {
    private val argsScope by lazy { ArgumentsScope<Edge>() }

    @KolibriumPropertyDsl
    public var useWebView: Boolean? = null

    override fun configure() {
        super.configure()
        options.apply {
            useWebView?.let { useWebView(it) }
        }
    }

    @KolibriumDsl
    public fun arguments(block: ArgumentsScope<Edge>.() -> Unit) {
        argsScope.apply(block)
        options.addArguments(argsScope.args.map { it.value })
    }

    override fun toString(): String {
        return "EdgeOptionsScope(acceptInsecureCerts=$acceptInsecureCerts, argumentsScope=$argsScope, " +
            "binary=$binary, browserVersion=$browserVersion, experimentalOptionsScope=$expOptionsScope, " +
            "extensionsScope=$extensionsScope, pageLoadStrategy=$pageLoadStrategy, platform=$platform, " +
            "proxyScope=$proxyScope, strictFileInteractability=$strictFileInteractability, " +
            "timeoutsScope=$timeoutsScope, unhandledPromptBehaviour=$unhandledPromptBehaviour, " +
            "useWebView=$useWebView)"
    }
}
