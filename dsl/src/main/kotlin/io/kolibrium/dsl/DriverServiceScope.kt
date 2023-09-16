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

import org.openqa.selenium.remote.service.DriverService
import kotlin.time.Duration
import kotlin.time.toJavaDuration

public sealed class BaseDriverServiceScope(@PublishedApi internal val builder: DriverService.Builder<*, *>) {

    @KolibriumDsl
    public var port: Int? = null

    @KolibriumDsl
    public var timeout: Duration? = null

    @PublishedApi
    internal fun configure() {
        builder.apply {
            port?.let { usingPort(it) }
            timeout?.let { withTimeout(it.toJavaDuration()) }
        }
    }

    @KolibriumDsl
    public fun environment(block: EnvironmentScope.() -> Unit) {
        val envScope = EnvironmentScope().apply(block)
        if (envScope.environmentVariables.isNotEmpty()) {
            builder.withEnvironment(envScope.environmentVariables)
        }
    }
}

public class DriverServiceScope<T : DriverService>(builder: DriverService.Builder<*, *>) :
    BaseDriverServiceScope(builder)