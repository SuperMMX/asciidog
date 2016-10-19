package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.builder.AsciidogCoreBuilderPlugin
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.Parser

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

        // Cross Reference Inline Parser Plugin
        plugins << new CrossReferenceInlineParserPlugin()

        // Attribute Reference
        plugins << new AttributeReferenceInlineParserPlugin()
    }

    private void addBuilderPlugins() {
        plugins << new AsciidogCoreBuilderPlugin()
    }

    private void addProcessorPlugins() {
    }

    private void addRendererPlugins() {
    }
}
