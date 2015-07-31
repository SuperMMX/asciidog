package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.builder.factory.AbstractNodeFactory

/**
 * The abstract class for builder plugin
 */
abstract class BuilderPlugin extends Plugin {
    List<AbstractNodeFactory> factories = []

    BuilderPlugin() {
        type = Type.BUILDER
    }
}
