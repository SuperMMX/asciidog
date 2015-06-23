package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.AttributeReferenceNode
import org.supermmx.asciidog.ast.AttributeSettingNode
import org.supermmx.asciidog.ast.CounterSettingNode

import java.util.regex.Matcher

/**
 * Attribute Refrence parser plugin
 */
class AttributeReferenceInlineParserPlugin extends InlineParserPlugin {
    AttributeReferenceInlineParserPlugin() {
        id = 'inline_parser_attribute_reference'
        nodeType = Node.Type.ATTRIBUTE_REFERENCE

        pattern = Parser.ATTRIBUTE_REFERENCE_PATTERN
    }


    protected Inline createNode(Matcher m, List<String> groups) {
        Inline inline = null;

        def action = groups[3]
        if (action == 'set') {
            inline = new AttributeSettingNode()
        } else if (action == 'counter'
            || action == 'counter2') {
            inline = new CounterSettingNode()
        } else {
            // normal reference
            inline = new AttributeReferenceNode()
            inline.name = groups[6]
        }

        return inline
    }

    protected boolean fillNode(Inline inline, Matcher m, List<String> groups) {
        inline.info.with {
            escaped = (groups[0] == '')
            contentStart = m.start(2)
            contentEnd = m.end(2)
        }

        return true
    }
}

