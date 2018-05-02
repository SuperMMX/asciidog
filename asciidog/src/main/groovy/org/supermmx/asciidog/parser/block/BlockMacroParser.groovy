package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.BlockMacro
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.block.BlockParserPlugin
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * The block macro parser.
 * name::target[attributes]
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class BlockMacroParser extends BlockParserPlugin {
    static final String ID = 'plugin:parser:block:macro'

    static final CHECK_MATCHER = sequence([
        // name
        oneOrMore('name', regex('\\w*')),
        literal('::'),
        // target
        optional(oneOrMore('target', not([
            type(Token.Type.WHITE_SPACES),
            type(Token.Type.EOL),
            type(Token.Type.EOF),
            literal('[')
        ]))),
        // attribute
        sequence([
            literal('['),
            zeroOrMore(not([
                type(Token.Type.EOL),
                type(Token.Type.EOF),
                literal(']')
            ])),
            literal(']'),
            firstOf([
                type(Token.Type.EOL),
                type(Token.Type.EOF)
            ])
        ], true)
    ])

    static final String HEADER_PROPERTY_MACRO_NAME = 'macroName'
    static final String HEADER_PROPERTY_MACRO_TARGET = 'macroTarget'

    BlockMacroParser() {
        nodeType = Node.Type.BLOCK_MACRO
        id = ID
    }

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        def isStart = CHECK_MATCHER.matches(context, ['header': header], false,
                                            { name, matcherContext, props, matched ->
                if (!matched) {
                    return
                }

                def matcherHeader = props.header
                def tokenString = matcherContext.lexer.joinTokensFromMark()
                if (name == 'name') {
                    matcherHeader.properties[HEADER_PROPERTY_MACRO_NAME] = tokenString
                } else if (name == 'target') {
                    matcherHeader.properties[HEADER_PROPERTY_MACRO_TARGET] = tokenString
                }
            })

        return isStart
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def lexer = context.lexer

        def name = header?.properties[HEADER_PROPERTY_MACRO_NAME]
        def target = header?.properties[HEADER_PROPERTY_MACRO_TARGET]

        BlockMacro macro = new BlockMacro(name: name, target: target)

        // TODO: parse attributes
        // []
        lexer.next(2)

        return macro
    }
}
