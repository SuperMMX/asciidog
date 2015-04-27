package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode

import groovy.util.logging.Slf4j

import org.slf4j.Logger

import java.util.ServiceLoader

/**
 * Plugin registry
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
@Singleton(strict=false)
class PluginRegistry {
    Map<String, Backend> backends = [:]
    List<Plugin> plugins = []

    PluginRegistry() {
        loadBackends()

        registerDefaultPlugins()

        loadPlugins()
    }

    /**
     * Get the backend with the specified id
     *
     * @param id the backend id
     *
     * @return the backend
     */
    Backend getBackend(String id) {
        return backends[(id)]
    }

    /**
     * Load backends found in classpath
     */
    private void loadBackends() {
        userLog.info('[BACKEND] Looking up backends...')
        ServiceLoader.load(Backend.class).each { backend ->
            backends[(backend.id)] = backend
            userLog.info("[BACKEND] Backend ${backend.id} found")
        }
    }

    private void loadPlugins() {
        userLog.info('[PLUGIN] Looking up plugins...')
        ServiceLoader.load(Plugin.class).each { plugin ->
            register(plugin)
        }
    }

    void register(Plugin plugin) {
        userLog.info "[PLUGIN] Registering plugin ID: '${plugin.id}', Type: ${plugin.type}, Node Type: ${plugin.nodeType}"

        if (plugins.find { it.id == plugin.id } == null) {
            plugins << plugin
        }
    }

    Plugin getPlugin(String id) {
        return plugins.find { it.id == id }
    }

    List<Plugin> getPlugins(Closure condition) {
        return plugins.findAll(condition)
    }

    List<InlineParserPlugin> getInlineParserPlugins() {
        return plugins.findAll { plugin ->
            plugin.nodeType?.isInline() && plugin.isParserPlugin()
        }
    }

    final static def TEXT_FORMATTING_PLUGINS_DATA = [
        // id, formatting type, constrained, pattern
        [ 'strong_unconstrained', FormattingNode.Type.STRONG, false, Parser.STRONG_UNCONSTRAINED_PATTERN ],
        [ 'strong_constrained', FormattingNode.Type.STRONG, true, Parser.STRONG_CONSTRAINED_PATTERN ],
        [ 'emphasis_unconstrained', FormattingNode.Type.EMPHASIS, false, Parser.EMPHASIS_UNCONSTRAINED_PATTERN ],
        [ 'emphasis_constrained', FormattingNode.Type.EMPHASIS, true, Parser.EMPHASIS_CONSTRAINED_PATTERN ],
    ]

    private void registerDefaultPlugins() {
        TEXT_FORMATTING_PLUGINS_DATA.each { pluginData ->
            def (id, ftType, constrained, pattern) = pluginData
            def plugin = new TextFormattingInlineParserPlugin(id: id, formattingType: ftType,
                                                              constrained: constrained, pattern: pattern)

            register(plugin)
        }

        // Cross Reference Inline Parser Plugin
        register(new CrossReferenceInlineParserPlugin())

        // Attribute Reference
        register(new AttributeReferenceInlineParserPlugin())
    }
}
