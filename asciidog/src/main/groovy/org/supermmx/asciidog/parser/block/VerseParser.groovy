package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Verse
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class VerseParser extends StyledBlockParser {
    static final String ID = 'plugin:parser:block:styled:verse'
    static final String VERSE_STYLE = 'verse'
    static final String VERSE_DELIMITER = '____'

    VerseParser() {
        nodeType = Node.Type.VERSE_BLOCK
        id = ID

        style = VERSE_STYLE
        delimiter = VERSE_DELIMITER
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        return new Verse()
    }
}
