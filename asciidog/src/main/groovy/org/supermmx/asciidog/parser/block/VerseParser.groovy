package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.Verse
import org.supermmx.asciidog.lexer.Token
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
        Verse verse = new Verse()

        def headerDelimiter = header.properties[HEADER_PROPERTY_STYLED_DELIMITER]

        // read until the delimiter
        String content = context.lexer.joinTokensTo(sequence([
            type(Token.Type.EOL),
            literal(headerDelimiter),
            type(Token.Type.EOL)
        ]), false)

        def token = context.lexer.peek()
        if (token != null && token.type == Token.Type.EOL) {
            content += token.value

            context.lexer.next(3)
        }

        def textNode = new TextNode(content)
        textNode.parent = verse
        verse.children << textNode

        return verse
    }

    @Override
    protected List<ChildParserInfo> doGetChildParserInfos(ParserContext context) {
        return []
    }
}
