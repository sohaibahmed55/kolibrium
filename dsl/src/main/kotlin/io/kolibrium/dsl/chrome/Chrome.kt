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

import io.kolibrium.dsl.AllowedIpsScope
import io.kolibrium.dsl.DriverServiceScope
import io.kolibrium.dsl.KolibriumDsl
import io.kolibrium.dsl.allowedIps
import io.kolibrium.dsl.internal.threadLocalLazyDelegate
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chromium.ChromiumDriverLogLevel

@KolibriumDsl
public var DriverServiceScope<ChromeDriverService>.appendLog: Boolean? by threadLocalLazyDelegate()

@KolibriumDsl
public var DriverServiceScope<ChromeDriverService>.buildCheckDisabled: Boolean? by threadLocalLazyDelegate()

@KolibriumDsl
public var DriverServiceScope<ChromeDriverService>.executable: String? by threadLocalLazyDelegate()

@KolibriumDsl
public var DriverServiceScope<ChromeDriverService>.logFile: String? by threadLocalLazyDelegate()

@KolibriumDsl
public var DriverServiceScope<ChromeDriverService>.logLevel: ChromiumDriverLogLevel? by threadLocalLazyDelegate()

@KolibriumDsl
public var DriverServiceScope<ChromeDriverService>.readableTimestamp: Boolean? by threadLocalLazyDelegate()

@KolibriumDsl
public fun DriverServiceScope<ChromeDriverService>.allowedIps(block: AllowedIpsScope.() -> Unit): Unit =
    allowedIps(builder, block)