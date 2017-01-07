package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.builder.AsciidogCoreBuilderPlugin
import org.supermmx.asciidog.parser.inline.AttributeReferenceParser
import org.supermmx.asciidog.parser.inline.CrossReferenceParser

class AsciidogCorePluginSuite extends PluginSuite {
    AsciidogCorePluginSuite() {
        // add parsers
        addParserPlugins()

        // add builder factories
        addBuilderPlugins()

        // add processors
        addProcessorPlugins()

        // add renderers
        addRendererPlugins()
    }

    private void addParserPlugins() {
        // inline parsers
    }

    private void addBuilderPlugins() {
        plugins << new AsciidogCoreBuilderPlugin()
    }

    private void addProcessorPlugins() {
    }

    private void addRendererPlugins() {
    }
}
