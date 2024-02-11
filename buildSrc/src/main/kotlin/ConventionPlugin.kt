package com.github.dmh.gradle.plugin

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

class ConventionPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit =
        with(project) {
            group = "com.github.dmh"

            plugins.withType<JavaPlugin> {
                extensions.getByType<JavaPluginExtension>().apply {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }

                repositories.apply {
                    mavenLocal()
                    mavenCentral()
                }

                dependencies.apply {
                    add("testImplementation", platform("org.junit:junit-bom:5.10.2"))
                    add("testImplementation", "org.junit.jupiter:junit-jupiter")
                }

                tasks.withType<Test>().configureEach {
                    useJUnitPlatform()
                }
            }

            plugins.withType<SpotlessPlugin> {
                extensions.getByType<SpotlessExtension>().apply {
                    java {
                        removeUnusedImports()

                        googleJavaFormat()
                            .aosp()
                            .reflowLongStrings()
                            .reorderImports(true)
                    }
                }
            }
        }
}
