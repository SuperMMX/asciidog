package org.supermmx.asciidog.critic.renderer

import org.supermmx.asciidog.plugin.RendererPlugin

class CriticRendererPlugin extends RendererPlugin {
    public CriticRendererPlugin() {
        id = 'renderer_critic'
        renderers << new CriticHtml5Renderer()
    }
}
