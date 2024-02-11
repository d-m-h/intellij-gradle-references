package com.github.dmh.gradle.plugin

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

abstract class ScalaVariantPluginExtension @javax.inject.Inject constructor(private val p: Project) {
    private val scalaVariants: NamedDomainObjectContainer<ScalaVariant> =
        p.objects.domainObjectContainer(ScalaVariant::class.java)

    init {
        scalaVariants.whenObjectAdded {
            configureVariant()
        }
    }

    fun variants(action: NamedDomainObjectContainer<ScalaVariant>.() -> Unit) {
        scalaVariants.action()
    }

    fun defaultVariant(name: String) {
        val variant = scalaVariants.getByName(name)
        variant.setAsDefault()
    }
}
