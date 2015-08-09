package org.supermmx.asciidog.builder

import org.supermmx.asciidog.plugin.Plugin
import org.supermmx.asciidog.plugin.PluginRegistry
import org.supermmx.asciidog.builder.factory.*

import groovy.util.FactoryBuilderSupport
import groovy.util.logging.Slf4j

@Slf4j
class AsciiDocBuilder extends FactoryBuilderSupport {
    {
        PluginRegistry pluginRegistry = PluginRegistry.instance

        pluginRegistry.getPlugins(Plugin.Type.BUILDER).each { plugin ->
            plugin.factories.each { factory ->
                registerFactory(factory)
            }
        }
    }

    protected void registerFactory(AbstractNodeFactory nodeFactory) {
        registerFactory(nodeFactory.name, nodeFactory)
    }
}
