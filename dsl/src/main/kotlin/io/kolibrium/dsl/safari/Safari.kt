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

package io.kolibrium.dsl.safari

import io.kolibrium.dsl.DriverServiceScope
import io.kolibrium.dsl.KolibriumDsl
import io.kolibrium.dsl.OptionsScope
import io.kolibrium.dsl.internal.threadLocalLazyDelegate
import org.openqa.selenium.safari.SafariDriverService
import org.openqa.selenium.safari.SafariOptions

@KolibriumDsl
public var DriverServiceScope<SafariDriverService>.logging: Boolean? by threadLocalLazyDelegate()

@KolibriumDsl
public var OptionsScope<SafariOptions>.automaticInspection: Boolean? by threadLocalLazyDelegate()

@KolibriumDsl
public var OptionsScope<SafariOptions>.automaticProfiling: Boolean? by threadLocalLazyDelegate()

@KolibriumDsl
public var OptionsScope<SafariOptions>.useTechnologyPreview: Boolean? by threadLocalLazyDelegate()
