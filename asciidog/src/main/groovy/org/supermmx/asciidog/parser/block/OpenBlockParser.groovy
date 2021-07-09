package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.OpenBlock
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class OpenBlockParser extends StyledBlockParser {
    static final String ID = 'plugin:parser:block:styled:open'
    static final String OPEN_DELIMITER = '--'

    OpenBlockParser() {
        nodeType = Node.Type.OPEN_BLOCK
        id = ID
        delimiter = OPEN_DELIMITER
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        return new OpenBlock()
    }
}
