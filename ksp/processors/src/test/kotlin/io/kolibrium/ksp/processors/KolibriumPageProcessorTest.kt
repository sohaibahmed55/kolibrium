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

@file:Suppress("LongMethod")

package io.kolibrium.ksp.processors

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.io.TempDir
import java.io.File

class KolibriumPageProcessorTest {

    @ValidTest
    fun `enum class annotated with KolibriumPage and enum entries annotated with locators`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
            package io.kolibrium.ksp.processors.test  

            import io.kolibrium.ksp.annotations.*

            @KolibriumPage
            enum class KolibriumTestPageLocators {
                @ClassName("className")
                entry1,

                @Css("css")
                entry2,

                @Id("id")
                entry3,

                @LinkText("linkText")
                entry4,

                @Name("name")
                entry5,

                @PartialLinkText("partialLinkText")
                entry6,

                @TagName("tagName")
                entry7,

                @Xpath("xpath")
                entry8
            }
            """.trimIndent()
        )

        val compilation = getCompilation(path, sourceFile)
        verifyExitCode(compilation.compile(), KotlinCompilation.ExitCode.OK)

        assertSourceEquals(
            """
                // Code generated by kolibrium-codegen. Do not edit.
                package io.kolibrium.ksp.processors.test.generated

                import io.kolibrium.selenium.className
                import io.kolibrium.selenium.css
                import io.kolibrium.selenium.id
                import io.kolibrium.selenium.linkText
                import io.kolibrium.selenium.name
                import io.kolibrium.selenium.partialLinkText
                import io.kolibrium.selenium.tagName
                import io.kolibrium.selenium.xpath
                import org.openqa.selenium.WebDriver
                import org.openqa.selenium.WebElement
                
                context(WebDriver)
                public class KolibriumTestPage {
                  public val entry1: WebElement by className<WebElement>("className")

                  public val entry2: WebElement by css<WebElement>("css")

                  public val entry3: WebElement by id<WebElement>("id")

                  public val entry4: WebElement by linkText<WebElement>("linkText")

                  public val entry5: WebElement by name<WebElement>("name")

                  public val entry6: WebElement by partialLinkText<WebElement>("partialLinkText")

                  public val entry7: WebElement by tagName<WebElement>("tagName")

                  public val entry8: WebElement by xpath<WebElement>("xpath")
                }
            """.trimIndent(),
            compilation = compilation
        )
    }

    @ValidTest
    fun `enum class annotated with KolibriumPage and enum entries have collectToList = true set`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
            package io.kolibrium.ksp.processors.test  

            import io.kolibrium.ksp.annotations.*

            @KolibriumPage
            enum class KolibriumTestPageLocators {
                @ClassName("className", true)
                entry1,

                @Css("css", true)
                entry2,

                @Id("id", true)
                entry3,

                @LinkText("linkText", true)
                entry4,

                @Name("name", true)
                entry5,

                @PartialLinkText("partialLinkText", true)
                entry6,

                @TagName("tagName", true)
                entry7,

                @Xpath("xpath", true)
                entry8
            }
            """.trimIndent()
        )

        val compilation = getCompilation(path, sourceFile)
        verifyExitCode(compilation.compile(), KotlinCompilation.ExitCode.OK)

        assertSourceEquals(
            """
                // Code generated by kolibrium-codegen. Do not edit.
                package io.kolibrium.ksp.processors.test.generated
 
                import io.kolibrium.selenium.WebElements
                import io.kolibrium.selenium.className
                import io.kolibrium.selenium.css
                import io.kolibrium.selenium.id
                import io.kolibrium.selenium.linkText
                import io.kolibrium.selenium.name
                import io.kolibrium.selenium.partialLinkText
                import io.kolibrium.selenium.tagName
                import io.kolibrium.selenium.xpath
                import org.openqa.selenium.WebDriver
                
                context(WebDriver)
                public class KolibriumTestPage {
                  public val entry1: WebElements by className<WebElements>("className")

                  public val entry2: WebElements by css<WebElements>("css")

                  public val entry3: WebElements by id<WebElements>("id")

                  public val entry4: WebElements by linkText<WebElements>("linkText")

                  public val entry5: WebElements by name<WebElements>("name")

                  public val entry6: WebElements by partialLinkText<WebElements>("partialLinkText")

                  public val entry7: WebElements by tagName<WebElements>("tagName")

                  public val entry8: WebElements by xpath<WebElements>("xpath")
                }
            """.trimIndent(),
            compilation = compilation
        )
    }

    @ValidTest
    fun `locator strategy annotation is missing`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                enum class KolibriumTestPageLocators {
                    entry
                }
            """.trimIndent()
        )

        val compilation = getCompilation(path, sourceFile)
        verifyExitCode(compilation.compile(), KotlinCompilation.ExitCode.OK)

        assertSourceEquals(
            """
                // Code generated by kolibrium-codegen. Do not edit.
                package io.kolibrium.ksp.processors.test.generated

                import io.kolibrium.selenium.idOrName
                import org.openqa.selenium.WebDriver
                import org.openqa.selenium.WebElement
                
                context(WebDriver)
                public class KolibriumTestPage {
                  public val entry: WebElement by idOrName<WebElement>("entry")
                }
            """.trimIndent(),
            compilation = compilation
        )
    }

    @ValidTest
    fun `locator strategy annotation has Mustache template`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                enum class KolibriumTestPageLocators {
                    @Css(locator = "//a[@class='{{color}}'][contains(text(),'{{shape}}')]")
                    tableCell
                }
            """.trimIndent()
        )

        val compilation = getCompilation(path, sourceFile)
        verifyExitCode(compilation.compile(), KotlinCompilation.ExitCode.OK)

        assertSourceEquals(
            """
                // Code generated by kolibrium-codegen. Do not edit.
                package io.kolibrium.ksp.processors.test.generated

                import io.kolibrium.selenium.css
                import kotlin.String
                import org.openqa.selenium.WebDriver
                import org.openqa.selenium.WebElement
                
                context(WebDriver)
                public class KolibriumTestPage {
                  public fun tableCell(color: String, shape: String): WebElement {
                    val locator = ${'"'}${'"'}${'"'}//a[@class='${'$'}color'][contains(text(),'${'$'}shape')]${'"'}${'"'}${'"'}
                    val element: WebElement by css<WebElement>(locator)
                    return element
                  }
                }
            """.trimIndent(),
            compilation = compilation
        )
    }

    @ValidTest
    fun `KolibriumPage has valid URL`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
              package io.kolibrium.ksp.processors.test  
    
              import io.kolibrium.ksp.annotations.*
    
              @KolibriumPage
              @Url("https://www.google.com")
              enum class KolibriumTestPageLocators {
                searchBar
            }
            """.trimIndent()
        )

        val compilation = getCompilation(path, sourceFile)
        verifyExitCode(compilation.compile(), KotlinCompilation.ExitCode.OK)

        assertSourceEquals(
            """
                // Code generated by kolibrium-codegen. Do not edit.
                package io.kolibrium.ksp.processors.test.generated

                import io.kolibrium.selenium.idOrName
                import org.openqa.selenium.WebDriver
                import org.openqa.selenium.WebElement
                
                context(WebDriver)
                public class KolibriumTestPage {
                  init {
                    get("https://www.google.com")
                  }

                  public val searchBar: WebElement by idOrName<WebElement>("searchBar")
                }
            """.trimIndent(),
            compilation = compilation
        )
    }

    @ValidTest
    fun `class name retained`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPage.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                enum class KolibriumTestPage {
                    entry
                }
            """.trimIndent()
        )

        val compilation = getCompilation(path, sourceFile)
        verifyExitCode(compilation.compile(), KotlinCompilation.ExitCode.OK)

        assertSourceEquals(
            """
                // Code generated by kolibrium-codegen. Do not edit.
                package io.kolibrium.ksp.processors.test.generated

                import io.kolibrium.selenium.idOrName
                import org.openqa.selenium.WebDriver
                import org.openqa.selenium.WebElement
                
                context(WebDriver)
                public class KolibriumTestPage {
                  public val entry: WebElement by idOrName<WebElement>("entry")
                }
            """.trimIndent(),
            compilation = compilation
        )
    }

    @ValidTest
    fun `generated class name set to generatedClassName value`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPage.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage("KolibriumForm")
                enum class KolibriumTestPage {
                    entry
                }
            """.trimIndent()
        )

        val compilation = getCompilation(path, sourceFile)
        verifyExitCode(compilation.compile(), KotlinCompilation.ExitCode.OK)

        assertSourceEquals(
            """
                // Code generated by kolibrium-codegen. Do not edit.
                package io.kolibrium.ksp.processors.test.generated

                import io.kolibrium.selenium.idOrName
                import org.openqa.selenium.WebDriver
                import org.openqa.selenium.WebElement
                
                context(WebDriver)
                public class KolibriumForm {
                  public val entry: WebElement by idOrName<WebElement>("entry")
                }
            """.trimIndent(),
            actual = "KolibriumForm.kt",
            compilation = compilation
        )
    }

    @InvalidTest
    fun `class annotated with KolibriumPage`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                class KolibriumTestPageLocator {                
                }
            """.trimIndent()
        )

        val result = getCompilation(path, sourceFile).compile()
        verifyExitCode(result, KotlinCompilation.ExitCode.COMPILATION_ERROR)
        result.messages shouldContain """
            Only enum classes can be annotated with @KolibriumPage. Please make sure "KolibriumTestPageLocator" is an enum class.
        """.trimIndent()
    }

    @InvalidTest
    fun `data class annotated with KolibriumPage`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                data class KolibriumTestPageLocator {                
                }
            """.trimIndent()
        )

        val result = getCompilation(path, sourceFile).compile()
        verifyExitCode(result, KotlinCompilation.ExitCode.COMPILATION_ERROR)
        result.messages shouldContain """
            Only enum classes can be annotated with @KolibriumPage. Please make sure "KolibriumTestPageLocator" is an enum class.
        """.trimIndent()
    }

    @InvalidTest
    fun `object annotated with KolibriumPage`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                object KolibriumTestPageLocator {                
                }
            """.trimIndent()
        )

        val result = getCompilation(path, sourceFile).compile()
        verifyExitCode(result, KotlinCompilation.ExitCode.COMPILATION_ERROR)
        result.messages shouldContain """
            Only enum classes can be annotated with @KolibriumPage. Please make sure "KolibriumTestPageLocator" is an enum class.
        """.trimIndent()
    }

    @InvalidTest
    fun `no enum entry defined`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                class KolibriumTestPageLocator {                
                }
            """.trimIndent()
        )

        val result = getCompilation(path, sourceFile).compile()
        verifyExitCode(result, KotlinCompilation.ExitCode.COMPILATION_ERROR)
        result.messages shouldContain "At least one enum shall be defined in \"KolibriumTestPageLocator\"."
    }

    @InvalidTest
    fun `more than one locator annotation present`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                enum class KolibriumTestPageLocators {
                  @Id("usr")
                  @Name
                  username,

                  @Id("pass")
                  @Name
                  password
                }
            """.trimIndent()
        )

        val result = getCompilation(path, sourceFile).compile()
        verifyExitCode(result, KotlinCompilation.ExitCode.COMPILATION_ERROR)
        result.messages shouldContain "More than one locator annotation found on \"username\": @Id, @Name"
    }

    @InvalidTest
    fun `url is invalid`(@TempDir path: File) {
        val sourceFile = SourceFile.kotlin(
            "KolibriumTestPageLocators.kt",
            """
                package io.kolibrium.ksp.processors.test  
    
                import io.kolibrium.ksp.annotations.*
    
                @KolibriumPage
                @Url("https://www")
                enum class KolibriumTestPageLocators { 
                  @Id
                  username
                }
            """.trimIndent()
        )

        val result = getCompilation(path, sourceFile).compile()
        verifyExitCode(result, KotlinCompilation.ExitCode.COMPILATION_ERROR)
        result.messages shouldContain "Provided URL in \"KolibriumTestPageLocators\" is invalid: https://www"
    }
}

private fun getCompilation(path: File, vararg sourceFiles: SourceFile) = KotlinCompilation().apply {
    workingDir = path.absoluteFile
    inheritClassPath = true
    sources = sourceFiles.asList()
    symbolProcessorProviders = listOf(KolibriumPageProcessorProvider())
    verbose = false
}

private fun verifyExitCode(result: KotlinCompilation.Result, exitCode: KotlinCompilation.ExitCode) =
    result.exitCode shouldBe exitCode

private fun assertSourceEquals(
    @Language("kotlin") expected: String,
    actual: String = "KolibriumTestPage.kt",
    compilation: KotlinCompilation
) = compilation.getGeneratedSource(actual).trimIndent() shouldBe expected.trimIndent()

private fun KotlinCompilation.getGeneratedSource(fileName: String) = kspSourcesDir.walkTopDown().first {
    it.name == fileName
}.readText()
