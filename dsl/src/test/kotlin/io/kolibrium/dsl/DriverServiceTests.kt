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

import io.kolibrium.dsl.chrome.allowedIps
import io.kolibrium.dsl.chrome.appendLog
import io.kolibrium.dsl.chrome.buildCheckDisabled
import io.kolibrium.dsl.chrome.executable
import io.kolibrium.dsl.chrome.logFile
import io.kolibrium.dsl.chrome.logLevel
import io.kolibrium.dsl.chrome.readableTimestamp
import io.kolibrium.dsl.firefox.allowedHosts
import io.kolibrium.dsl.firefox.logFile
import io.kolibrium.dsl.firefox.logLevel
import io.kolibrium.dsl.firefox.profileRoot
import io.kolibrium.dsl.firefox.truncatedLogs
import io.kolibrium.dsl.safari.logging
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chromium.ChromiumDriverLogLevel.DEBUG
import org.openqa.selenium.firefox.FirefoxDriverLogLevel.TRACE
import org.openqa.selenium.firefox.GeckoDriverService
import org.openqa.selenium.remote.service.DriverService
import org.openqa.selenium.safari.SafariDriverService
import java.io.File
import java.nio.file.Path
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Suppress("UNCHECKED_CAST")
@Disabled("Temporarily disabled due to build issue with CI")
class DriverServiceTests : DslTest() {
    lateinit var ds: DriverService

    @AfterEach
    fun stopDriverService() {
        ds.stop()
    }

    @Test
    fun `empty driverService block should create default DriverService`() {
        ds = driverService<ChromeDriverService> {
        }

        ds.start()

        val executable = ds.getField("executable") as String
        executable.shouldNotBeEmpty()

        val timeout = ds.invokeMethod("getTimeout") as Duration
        timeout shouldBe 20.seconds.toJavaDuration()

        val args = ds.invokeMethod("getArgs") as List<String>
        args shouldHaveSize 1

        val environment = ds.invokeMethod("getEnvironment") as Map<String, String>
        environment.shouldBeEmpty()
    }

    @Test
    fun `custom ChromeDriverService shall be created`(@TempDir tempDir: Path) {
        val logFilePath = tempDir.resolve("chrome.log").toString()
        val executablePath = Path.of("src/test/resources/executables/chromedriver").toAbsolutePath().toString()

        ds = driverService<ChromeDriverService> {
            appendLog = true
            buildCheckDisabled = true
            executable = executablePath
            logFile = logFilePath
            logLevel = DEBUG
            port = 7000
            readableTimestamp = true
            timeout = 5.seconds
            allowedIps {
                +"192.168.0.50"
                +"192.168.0.51"
            }
            environment {
                +("key1" to "value1")
                +("key2" to "value2")
            }
        }

        ds.start()

        val executable = ds.getField("executable") as String
        executable.shouldNotBeEmpty()

        val args = ds.invokeMethod("getArgs") as List<String>
        args shouldHaveSize 7
        args.shouldContainExactlyInAnyOrder(
            "--allowed-ips=192.168.0.50, 192.168.0.51",
            "--append-log",
            "--disable-build-check",
            "--log-level=DEBUG",
            "--log-path=$logFilePath",
            "--port=7000",
            "--readable-timestamp"
        )

        val timeout = ds.invokeMethod("getTimeout") as Duration
        timeout shouldBe 5.seconds.toJavaDuration()

        val environment = ds.invokeMethod("getEnvironment") as Map<String, String>
        environment shouldBe mapOf("key1" to "value1", "key2" to "value2")
    }

    @Test
    fun `custom GeckoDriverService shall be created`(@TempDir tempDir: Path) {
        val logFilePath = tempDir.resolve("firefox.log").toString()

        ds = driverService<GeckoDriverService> {
            logFile = logFilePath
            logLevel = TRACE
            port = 7001
            profileRoot = tempDir.toString()
            truncatedLogs = false
            allowedHosts {
                +"192.168.0.50"
                +"192.168.0.51"
            }
            environment {
                +("key1" to "value1")
                +("key2" to "value2")
            }
        }

        ds.start()

        val args = ds.invokeMethod("getArgs") as List<String>
        args shouldHaveSize 14
        args.shouldContainAll(
            "--port=7001",
            "--log",
            "trace",
            "--allow-hosts",
            "192.168.0.50",
            "192.168.0.51",
            "--log-no-truncate",
            "--profile-root",
            tempDir.toString()
        )

        val environment = ds.invokeMethod("getEnvironment") as Map<String, String>
        environment shouldBe mapOf("key1" to "value1", "key2" to "value2")

        File(logFilePath).exists() shouldBe true
    }

    @Test
    fun `custom SafariDriverService shall be created`() {
        ds = driverService<SafariDriverService> {
            logging = true
            port = 7002
            environment {
                +("key1" to "value1")
                +("key2" to "value2")
            }
        }

        ds.start()

        val args = ds.invokeMethod("getArgs") as List<String>
        args shouldHaveSize 3
        args.shouldContainExactly("--port", "7002", "--diagnose")

        val environment = ds.invokeMethod("getEnvironment") as Map<String, String>
        environment shouldBe mapOf("key1" to "value1", "key2" to "value2")
    }
}