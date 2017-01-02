package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class ParagraphParser extends BlockParserPlugin {
    static final String ID = 'plugin:parser:block:paragraph'

    ParagraphParser() {
        nodeType = Node.Type.PARAGRAPH
        id = ID
    }

    @Override
    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        return (line != null) && (line.trim().length() > 0)
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def reader = context.reader

        Paragraph para = null

        def lines = []

        def line = reader.peekLine()

        log.debug('paragraph line = {}', line)

        while (line != null && line.length() > 0) {
            if (para == null) {
                para = new Paragraph()
                fillBlockFromHeader(para, header)

                context.blockHeader = null
                context.keepHeader = true
            }

            lines << line

            reader.nextLine()

            line = reader.peekLine()
            log.debug('paragraph line = {}', line)

            if (line == null || line.length() == 0) {
                reader.nextLine()

                context.blockHeader = null
                break
            }

            def isEnd = false
            def checkers = context.paragraphEndingCheckers
            for (def i = checkers.size() - 1; i >= 0; i--) {
                def parser = checkers[i]

                isEnd = parser.toEndParagraph(context, line)

                // just stop here no matter what ??
                if (isEnd) {
                    break
                }
            }

            if (isEnd) {
                break
            } else {
                if (header?.lines) {
                    lines.addAll(header?.lines)
                }
                context.blockHeader = null
            }
        }

        if (para != null) {
            // parse the inline nodes
            // the children has been added in the paragraph when parsing
            Parser.parseInlineNodes(para, lines.join('\n'))
        }

        log.debug('End parsing paragraph, parent type: {}', parent?.type)

        return para
    }
}
