package com.github.dmh.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.withType

class ScalaVariantPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit =
        with(project) {
            plugins.withType<JavaPlugin> {
                extensions.create("scalaVariants", ScalaVariantPluginExtension::class.java)
            }

            tasks.register("dumpProjectConfiguration") {
                group = "debug"

                doLast {
                    println("Project name: $name\n")
                    println("Project configurations: ${configurations.names.joinToString("\n - ", "\n - ")}\n")
                    println("Project tasks: ${tasks.names.joinToString("\n - ", "\n - ")}\n")
                }
            }
        }
}
