package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.parser.ParserContext

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class PreambleParser extends BlockParserPlugin {
    static final String ID = 'plugin:parser:block:preamble'

    PreambleParser() {
        nodeType = Node.Type.PREAMBLE
        id = ID
    }

    @Override
    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        // header should be pre-filled
        def isStart = false

        if (expected) {
            if (header?.type != null
                && header?.type != Node.Type.SECTION) {
                isStart = true
            }
        }

        return isStart
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def preamble = new Preamble()

        context.keepHeader = true

        return preamble
    }

    @Override
    protected String doGetNextChildParser(ParserContext context, Block block) {
        def childParser = null

        def header = nextBlockHeader(context, true)

        if (header?.type != Node.Type.SECTION) {
            childParser = header?.parserId
        }

        return childParser
    }
}
