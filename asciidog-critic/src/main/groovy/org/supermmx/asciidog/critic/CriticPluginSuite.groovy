package org.supermmx.asciidog.critic

import org.supermmx.asciidog.critic.builder.CriticBuilderPlugin
import org.supermmx.asciidog.critic.parser.*

import org.supermmx.asciidog.plugin.PluginSuite
import org.supermmx.asciidog.backend.TemplateManager

class CriticPluginSuite extends PluginSuite {
    CriticPluginSuite() {
        // parsers
        plugins << new CriticAdditionParser()
        plugins << new CriticDeletionParser()
        plugins << new CriticCommentParser()
        plugins << new CriticSubstitutionParser()
        plugins << new CriticHighlightParser()

        // builder
        plugins << new CriticBuilderPlugin()

        // renderers
        TemplateManager.instance.registerTemplateDirectory('html5', '/org/supermmx/asciidog/critic/html5/', true)
    }
}
