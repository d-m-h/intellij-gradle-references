plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}

gradlePlugin {
    plugins {
        create("convention") {
            id = "com.github.dmh.convention"
            implementationClass = "com.github.dmh.gradle.plugin.ConventionPlugin"
        }

        create("scalaVariants") {
            id = "com.github.dmh.scala-variants"
            implementationClass = "com.github.dmh.gradle.plugin.ScalaVariantPlugin"
        }
    }
}
