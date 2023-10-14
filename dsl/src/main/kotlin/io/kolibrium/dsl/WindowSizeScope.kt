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

import mu.KotlinLogging
import kotlin.properties.Delegates

private const val WIDTH = 1280
private const val HEIGHT = 720

@KolibriumDsl
public class WindowSizeScope<T : Browser> {
    private val logger = KotlinLogging.logger { }

    public var width: Int by Delegates.vetoable(WIDTH) { _, oldValue, newValue ->
        if (newValue < oldValue) {
            logger.debug("Requested window width $newValue < the minimum of $oldValue. Setting width to $oldValue")
            false
        } else {
            true
        }
    }

    public var height: Int by Delegates.vetoable(HEIGHT) { _, oldValue, newValue ->
        if (newValue < oldValue) {
            logger.debug("Requested window height $newValue < the minimum of $oldValue. Setting height to $oldValue")
            false
        } else {
            true
        }
    }
}
