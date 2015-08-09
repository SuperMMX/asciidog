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

    final static def TEXT_FORMATTING_PLUGINS_DATA = [
        // id, formatting type, constrained, pattern
        [ 'strong_unconstrained', FormattingNode.FormattingType.STRONG, false, Parser.STRONG_UNCONSTRAINED_PATTERN ],
        [ 'strong_constrained', FormattingNode.FormattingType.STRONG, true, Parser.STRONG_CONSTRAINED_PATTERN ],
        [ 'emphasis_unconstrained', FormattingNode.FormattingType.EMPHASIS, false, Parser.EMPHASIS_UNCONSTRAINED_PATTERN ],
        [ 'emphasis_constrained', FormattingNode.FormattingType.EMPHASIS, true, Parser.EMPHASIS_CONSTRAINED_PATTERN ],
    ]

    private void addParserPlugins() {
        // inline parsers

        TEXT_FORMATTING_PLUGINS_DATA.each { pluginData ->
            def (id, ftType, constrained, pattern) = pluginData
            def plugin = new TextFormattingInlineParserPlugin(id: id, formattingType: ftType,
                                                              constrained: constrained, pattern: pattern)

            plugins << plugin
        }

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
