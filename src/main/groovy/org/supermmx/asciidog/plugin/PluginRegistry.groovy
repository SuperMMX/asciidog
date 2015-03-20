package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Singleton
class PluginRegistry {
    Map<Node.Type, List<Plugin>> plugins = [:]

    void register(Plugin plugin) {
        Node.Type type = plugin.nodeType

        log.info "Registering plugin ID: '${plugin.id}', Node Type: ${type}"
        def list = plugins[(type)]
        if (list == null) {
            list = [] as List<Plugin>
            plugins[(type)] = list
        }

        list << plugin
    }

    List<InlineParserPlugin> getInlineParserPlugins() {
        def list = plugins[(Node.Type.INLINE)]
        def resultList = list.findAll { plugin ->
            plugin instanceof InlineParserPlugin
        }

        return resultList
    }
}
