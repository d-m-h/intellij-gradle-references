plugins {
    id("java-library")
    id("com.diffplug.spotless")
    id("com.github.dmh.convention")
    id("com.github.dmh.scala-variants")
}

scalaVariants {
    variants {
        create("2.12")
        create("2.13")
    }

    defaultVariant("2.12")
}

val sparkVersion = "3.2.4"

fun DependencyHandlerScope.scala212Implementation(dependencyNotation: Any): Dependency =
    add("scala212Implementation", dependencyNotation)!!

fun DependencyHandlerScope.scala213Implementation(dependencyNotation: Any): Dependency =
    add("scala213Implementation", dependencyNotation)!!

dependencies {
    scala212Implementation("org.apache.spark:spark-sql_2.12:${sparkVersion}")
    scala212Implementation(project(path = ":core", configuration = "scala212RuntimeElements"))

    scala213Implementation("org.apache.spark:spark-sql_2.13:${sparkVersion}")
    scala213Implementation(project(path = ":core", configuration = "scala213RuntimeElements"))
}
