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

@file:Suppress("UNCHECKED_CAST")

package io.kolibrium.dsl

import io.kolibrium.dsl.chrome.appendLog
import io.kolibrium.dsl.chrome.binary
import io.kolibrium.dsl.chrome.buildCheckDisabled
import io.kolibrium.dsl.chrome.executable
import io.kolibrium.dsl.chrome.logFile
import io.kolibrium.dsl.chrome.logLevel
import io.kolibrium.dsl.chrome.readableTimestamp
import io.kolibrium.dsl.firefox.binary
import io.kolibrium.dsl.firefox.executable
import io.kolibrium.dsl.firefox.logFile
import io.kolibrium.dsl.firefox.logLevel
import io.kolibrium.dsl.firefox.profileDir
import io.kolibrium.dsl.firefox.profileRoot
import io.kolibrium.dsl.firefox.truncatedLogs
import io.kolibrium.dsl.safari.automaticInspection
import io.kolibrium.dsl.safari.automaticProfiling
import io.kolibrium.dsl.safari.logging
import io.kolibrium.dsl.safari.useTechnologyPreview
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.firefox.GeckoDriverService
import org.openqa.selenium.remote.AbstractDriverOptions
import org.openqa.selenium.remote.service.DriverService
import org.openqa.selenium.safari.SafariDriverService
import org.openqa.selenium.safari.SafariOptions
import java.io.File

@DslMarker
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
internal annotation class KolibriumDsl

@KolibriumDsl
public inline fun <reified T : DriverService> driverService(noinline block: DriverServiceScope<T>.() -> Unit): T {
    return when (T::class) {
        ChromeDriverService::class -> {
            val driverServiceScope =
                driverServiceScope(ChromeDriverService.Builder(), block) as DriverServiceScope<ChromeDriverService>
            configureChromeDriverService(driverServiceScope)
        }

        GeckoDriverService::class -> {
            val driverServiceScope =
                driverServiceScope(GeckoDriverService.Builder(), block) as DriverServiceScope<GeckoDriverService>
            configureFirefoxDriverService(driverServiceScope)
        }

        SafariDriverService::class -> {
            val driverServiceScope =
                driverServiceScope(SafariDriverService.Builder(), block) as DriverServiceScope<SafariDriverService>
            configureSafariDriverService(driverServiceScope)
        }

        else -> throw UnsupportedOperationException()
    }.build() as T
}

@PublishedApi
internal fun <T : DriverService> driverServiceScope(
    builder: DriverService.Builder<*, *>,
    block: DriverServiceScope<T>.() -> Unit
): DriverServiceScope<T> = DriverServiceScope<T>(builder).apply {
    block()
    configure()
}

@PublishedApi
internal fun configureChromeDriverService(driverServiceScope: DriverServiceScope<ChromeDriverService>):
    ChromeDriverService.Builder = (driverServiceScope.builder as ChromeDriverService.Builder).apply {
    with(driverServiceScope) {
        appendLog?.let { withAppendLog(it) }
        buildCheckDisabled?.let { withBuildCheckDisabled(it) }
        executable?.let { usingDriverExecutable(File(it)) }
        logFile?.let { withLogFile(File(it)) }
        logLevel?.let { withLogLevel(it) }
        readableTimestamp?.let { withReadableTimestamp(it) }
    }
}

@PublishedApi
internal fun configureFirefoxDriverService(driverServiceScope: DriverServiceScope<GeckoDriverService>):
    GeckoDriverService.Builder = (driverServiceScope.builder as GeckoDriverService.Builder).apply {
    with(driverServiceScope) {
        executable?.let { usingDriverExecutable(File(it)) }
        logFile?.let { withLogFile(File(it)) }
        logLevel?.let { withLogLevel(it) }
        profileRoot?.let { withProfileRoot(File(it)) }
        truncatedLogs?.let { withTruncatedLogs(it) }
    }
}

@PublishedApi
internal fun configureSafariDriverService(driverServiceScope: DriverServiceScope<SafariDriverService>):
    SafariDriverService.Builder = (driverServiceScope.builder as SafariDriverService.Builder).apply {
    with(driverServiceScope) {
        logging?.let { withLogging(it) }
    }
}

@KolibriumDsl
public inline fun <reified T : AbstractDriverOptions<*>> options(noinline block: OptionsScope<T>.() -> Unit): T {
    return when (T::class) {
        ChromeOptions::class -> {
            val optionsScope =
                optionsScope(ChromeOptions(), block) as OptionsScope<ChromeOptions>
            configureChromeOptions(optionsScope)
        }

        FirefoxOptions::class -> {
            val optionsScope =
                optionsScope(FirefoxOptions(), block) as OptionsScope<FirefoxOptions>
            configureFirefoxOptions(optionsScope)
        }

        SafariOptions::class -> {
            val optionsScope =
                optionsScope(SafariOptions(), block) as OptionsScope<SafariOptions>
            configureSafariOptions(optionsScope)
        }

        else -> throw UnsupportedOperationException()
    } as T
}

@PublishedApi
internal fun <T : AbstractDriverOptions<*>> optionsScope(
    options: AbstractDriverOptions<*>,
    block: OptionsScope<T>.() -> Unit
): OptionsScope<T> = OptionsScope<T>(options).apply {
    block()
    configure()
}

@PublishedApi
internal fun configureChromeOptions(optionsScope: OptionsScope<ChromeOptions>): ChromeOptions =
    (optionsScope.options as ChromeOptions).apply {
        optionsScope.binary?.let { setBinary(it) }
    }

@PublishedApi
internal fun configureFirefoxOptions(optionsScope: OptionsScope<FirefoxOptions>): FirefoxOptions =
    (optionsScope.options as FirefoxOptions).apply {
        with(optionsScope) {
            binary?.let { setBinary(it) }
            profileDir?.let { profile = FirefoxProfile(File(it)) }
        }
    }

@PublishedApi
internal fun configureSafariOptions(optionsScope: OptionsScope<SafariOptions>): SafariOptions =
    (optionsScope.options as SafariOptions).apply {
        with(optionsScope) {
            automaticInspection?.let { setAutomaticInspection(it) }
            automaticProfiling?.let { setAutomaticProfiling(it) }
            useTechnologyPreview?.let { setUseTechnologyPreview(it) }
        }
    }
