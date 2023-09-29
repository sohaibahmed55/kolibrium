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

package io.kolibrium.dsl.invalid

import io.kolibrium.dsl.chrome.executable
import io.kolibrium.dsl.driver
import io.kolibrium.dsl.driverService
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.net.PortProber
import java.net.ServerSocket

class DriverTests {

    @Test
    fun `ChromeDriver shall not be created - port in use`() {
        val port = PortProber.findFreePort()

        ServerSocket(port).use {
            val exception = assertThrows<RuntimeException> {
                driver<ChromeDriver> {
                    driverService<ChromeDriverService> {
                        this.port = port
                    }
                }
            }

            exception.message shouldBe
                """
                    |DriverService is not set up properly:
                    |Port $port already in use
                """.trimMargin()
        }
    }

    @Test
    fun `ChromeDriver shall not be created - wrong executable path`() {
        val exception = assertThrows<RuntimeException> {
            driver<ChromeDriver> {
                driverService<ChromeDriverService> {
                    executable = "does not exist"
                }
            }
        }

        exception.message shouldBe
            """
                |DriverService is not set up properly:
                |The following file does not exist at the specified path: does not exist
            """.trimMargin()
    }
}
