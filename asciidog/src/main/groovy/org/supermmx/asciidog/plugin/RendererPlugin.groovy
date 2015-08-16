package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.backend.NodeRenderer

/**
 * The abstract class for renderer plugin
 */
abstract class RendererPlugin extends Plugin {
    List<NodeRenderer> renderers = []

    RendererPlugin() {
        type = Type.RENDERER
    }
}
