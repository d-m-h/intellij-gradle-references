package com.github.dmh.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

abstract class ScalaVariant @javax.inject.Inject constructor(
    val name: String,
    private val p: Project
) {
    private val nameFmt = name.replace(".", "")
    private val mainSourceSetName = "scala$nameFmt"
    private val testSourceSetName = "testScala$nameFmt"

    // These are inspired from here: https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph
    private val mainApi = "${mainSourceSetName}Api"
    private val mainCompileOnlyApi = "${mainSourceSetName}CompileOnlyApi"
    private val mainCompileOnly = "${mainSourceSetName}CompileOnly"
    private val mainApiElements = "${mainSourceSetName}ApiElements"
    private val mainImplementation = "${mainSourceSetName}Implementation"
    private val mainRuntimeOnly = "${mainSourceSetName}RuntimeOnly"
    private val mainCompileClasspath = "${mainSourceSetName}CompileClasspath"
    private val mainRuntimeElements = "${mainSourceSetName}RuntimeElements"
    private val mainRuntimeClasspath = "${mainSourceSetName}RuntimeClasspath"

    private val testCompileOnly = "${testSourceSetName}CompileOnly"
    private val testImplementation = "${testSourceSetName}Implementation"
    private val testRuntimeOnly = "${testSourceSetName}RuntimeOnly"
    private val testCompileClasspath = "${testSourceSetName}CompileClasspath"
    private val testRuntimeClasspath = "${testSourceSetName}RuntimeClasspath"

    private fun mainSourceSet(): SourceSet = p.sourceSets().getByName(mainSourceSetName)

    internal fun configureVariant() {
        p.logger.lifecycle("Configuring Scala variant {}", name)
        configureSourceSets()
        configureConfigurations()
        configureTasks()
        configureArtifacts()
    }

    private fun configureTasks() {
        p.logger.lifecycle("Configuring tasks for Scala variant {}", name)
        val mainSourceSet = mainSourceSet()

        p.tasks.register<Jar>(mainSourceSet.jarTaskName) {
            group = "build"
            description = "Assembles a jar archive containing the main classes."
            dependsOn(mainSourceSet.classesTaskName)
            from(mainSourceSet.output)
            destinationDirectory.set(p.file("build/libs/scala-$name"))
        }
    }

    private fun configureArtifacts() {
        p.logger.lifecycle("Configuring artefacts for Scala variant {}", name)
        p.artifacts.add(mainRuntimeElements, p.tasks.named<Jar>(mainSourceSet().jarTaskName).map { it.archiveFile })
    }

    private fun Project.sourceSets() = extensions.getByType<SourceSetContainer>()

    private fun configureSourceSets() {
        p.logger.lifecycle("Configuring source sets for Scala variant {}", name)
        val sourceSetContainer = p.sourceSets()
        p.logger.lifecycle("Creating main source set for Scala variant {}", name)
        val mainSourceSet = sourceSetContainer.create(mainSourceSetName).apply {
            java.setSrcDirs(listOf("src/main/java"))
            resources.setSrcDirs(listOf("src/main/resources"))
        }

        p.logger.lifecycle("Creating test source set for Scala variant {}", name)
        sourceSetContainer.create(testSourceSetName).apply {
            java.setSrcDirs(listOf("src/test/java"))
            resources.setSrcDirs(listOf("src/test/resources"))

            compileClasspath += mainSourceSet.output
            runtimeClasspath += mainSourceSet.output
        }
    }

    private fun configureConfigurations() {
        p.logger.lifecycle("Configuring configurations for Scala variant {}", name)
        val configurations = p.configurations

        p.logger.lifecycle("Creating main configurations for Scala variant {}", name)
        val compileOnlyApi = configurations.maybeCreate(mainCompileOnlyApi).apply {
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        val api = configurations.maybeCreate(mainApi).apply {
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        val compileOnly = configurations.maybeCreate(mainCompileOnly).apply {
            extendsFrom(compileOnlyApi)
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        val apiElements = configurations.maybeCreate(mainApiElements).apply {
            extendsFrom(compileOnlyApi, api)
            isCanBeResolved = false
            isCanBeConsumed = true
        }
        val implementation = configurations.maybeCreate(mainImplementation).apply {
            extendsFrom(api)
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        val runtimeOnly = configurations.maybeCreate(mainRuntimeOnly).apply {
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        val compileClasspath = configurations.maybeCreate(mainCompileClasspath).apply {
            extendsFrom(compileOnly, implementation)
            isCanBeResolved = true
            isCanBeConsumed = false
        }
        val runtimeElements = configurations.maybeCreate(mainRuntimeElements).apply {
            extendsFrom(implementation, runtimeOnly)
            isCanBeResolved = false
            isCanBeConsumed = true
        }
        val runtimeClasspath = configurations.maybeCreate(mainRuntimeClasspath).apply {
            extendsFrom(implementation, runtimeOnly)
            isCanBeResolved = true
            isCanBeConsumed = false
        }

        p.logger.lifecycle("Creating test configurations for Scala variant {}", name)
        val testCompileOnly = configurations.maybeCreate(testCompileOnly).apply {
            extendsFrom(compileOnlyApi)
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        val testImplementation = configurations.maybeCreate(testImplementation).apply {
            extendsFrom(implementation)
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        val testRuntimeOnly = configurations.maybeCreate(testRuntimeOnly).apply {
            extendsFrom(runtimeOnly)
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        val testCompileClasspath = configurations.maybeCreate(testCompileClasspath).apply {
            extendsFrom(testCompileOnly, testImplementation)
            isCanBeResolved = true
            isCanBeConsumed = false
        }
        val testRuntimeClasspath = configurations.maybeCreate(testRuntimeClasspath).apply {
            extendsFrom(testImplementation, testRuntimeOnly)
            isCanBeResolved = true
            isCanBeConsumed = false
        }
    }

    fun setAsDefault() {
        p.logger.lifecycle("Setting Scala variant {} as default", name)
        val sourceSets = p.sourceSets()
        val mainSourceSet = sourceSets.getByName("main")
        mainSourceSet.compileClasspath = sourceSets.getByName(mainSourceSetName).compileClasspath
        mainSourceSet.runtimeClasspath = sourceSets.getByName(mainSourceSetName).runtimeClasspath

        val testSourceSet = sourceSets.getByName("test")
        testSourceSet.compileClasspath = sourceSets.getByName(testSourceSetName).compileClasspath
        testSourceSet.runtimeClasspath = sourceSets.getByName(testSourceSetName).runtimeClasspath
    }
}
