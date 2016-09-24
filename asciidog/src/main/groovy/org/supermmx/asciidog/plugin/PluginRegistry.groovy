package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.parser.block.BlockParserPlugin

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

        loadPlugins()
    }

    /**
     * Test constructor that only loads external plugins and backends
     */
    protected PluginRegistry(String id) {
        loadBackends()

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
        def configSlurper = new ConfigSlurper()

        def configs = PluginRegistry.class.getClassLoader().getResources('asciidog.groovy')
        configs.each { URL url ->
            userLog.info('[PLUGIN] Loading AsciiDog Plugin from', url)
            def config = configSlurper.parse(url)
            log.debug('Plugin config = {}', config)

            userLog.info('[PLUGIN] Loading plugins from plugin \"{}\"...', config.asciidog.name)

            // suite first
            def suiteCls = config.asciidog.suite
            if (suiteCls != null) {
                // run the suite
                userLog.info("[PLUGIN] Loading plugins from suite \"{}\"...", suiteCls)
                def suite = suiteCls.newInstance()
                suite.plugins.each { plugin ->
                    register(plugin)
                }
            }

            // plugins
            def pluginClassList = config.asciidog.plugins
            if (pluginClassList != null) {
                userLog.info("[PLUGIN] Loading configured plugins...")
                pluginClassList.each { pluginCls ->
                    register(pluginCls.newInstance())
                }
            }
            userLog.info("[PLUGIN] Loading plugins from plugin \"{}\"...Done", config.asciidog.name)
        }

        /*
        ServiceLoader.load(PluginSuite.class).each { suite ->
            suite.plugins.each { plugin ->
                register(plugin)
            }
        }
        */
    }

    void register(Plugin plugin) {
        userLog.info("[PLUGIN] Registering plugin ID: \"{}\", Type: {}, Node Type: {}",
                     plugin.id, plugin.type, plugin.nodeType)

        if (plugins.find { it.id == plugin.id } == null) {
            plugins << plugin

            if (plugin.type == Plugin.Type.RENDERER) {
                // register to the backend
                plugin.renderers.each { renderer ->
                    def id = renderer.backendId
                    def backend = getBackend(id)
                    if (backend != null) {
                        backend.registerRenderer(renderer.nodeType, renderer)
                    }
                }
            }
        }
    }

    Plugin getPlugin(String id) {
        return plugins.find { it.id == id }
    }

    List<Plugin> getPlugins(Plugin.Type type) {
        return getPlugins { plugin ->
            plugin.type == type
        }
    }

    List<Plugin> getPlugins(Closure condition) {
        return plugins.findAll(condition)
    }

    List<InlineParserPlugin> getInlineParsers() {
        return plugins.findAll { plugin ->
            plugin.nodeType?.isInline() && plugin.type == Plugin.Type.PARSER
        }
    }

    List<BlockParserPlugin> getAllBlockParsers() {
        return plugins.findAll { plugin ->
            !plugin.nodeType?.isInline() && plugin.type == Plugin.Type.PARSER
        }
    }

    /**
     * Return all the normal blocks (non-document, non-structure blocks), except paragraph
     */
    List<BlockParserPlugin> getBlockParsers() {
        return plugins.findAll { plugin ->
            !plugin.nodeType?.isInline() &&
            plugin.type == Plugin.Type.PARSER &&
            plugin.nodeType.isBlock() &&
            plugin.nodeType != Node.Type.PARAGRAPH
        }
    }
}
