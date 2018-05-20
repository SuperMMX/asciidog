package org.supermmx.asciidog.critic

import org.supermmx.asciidog.critic.builder.CriticBuilderPlugin
import org.supermmx.asciidog.critic.parser.*

import org.supermmx.asciidog.backend.html5.Html5Backend

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
        TemplateManager.instance.registerTemplateDirectory(Html5Backend.HTML5_BACKEND_ID,
                                                           '/org/supermmx/asciidog/critic/html5/',
                                                           true)
    }
}
