package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * Plugin registry
 */
@Slf4j
@Singleton
class PluginRegistry {
    List<Plugin> plugins = []

    void register(Plugin plugin) {
        log.info "Registering plugin ID: '${plugin.id}', Type: ${plugin.type}, Node Type: ${plugin.nodeType}"

        if (plugins.find { it.id == plugin.id } == null) {
            plugins << plugin
        }
    }

    List<Plugin> getPlugins(Closure condition) {
        return plugins.findAll(condition)
    }

    List<InlineParserPlugin> getInlineParserPlugins() {
        return plugins.findAll { plugin ->
            plugin.nodeType.isInline() && plugin.isParserPlugin()
        }
    }
}
