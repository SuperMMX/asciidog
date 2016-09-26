package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class HeaderParser extends BlockParserPlugin {
    static final String ID = 'plugin:parser:block:header'

    HeaderParser() {
        nodeType = Node.Type.HEADER
        id = ID
    }

    @Override
    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        return expected
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def docHeader = new Header()

        return docHeader
    }

    @Override
    protected String doGetNextChildParser(ParserContext context, Block block) {
        def childParser = null

        def lastParser = context.lastParserId

        if (lastParser == null) {
            context.childParserProps.expected = true
            childParser = AuthorParser.ID
        }

        context.lastParserId = childParser

        return childParser
    }
}
