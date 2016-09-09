package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.parser.ParserContext

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
    boolean isStart(String line, BlockHeader header) {
        return (line != null) && (line.trim().length() > 0)
    }

    @Override
    Block parse(ParserContext context) {
        def reader = context.reader
        def parent = context.parent
        def parentParser = context.parentParser
        def blockHeader = context.blockHeader

        log.debug('Start parsing paragraph, parent type: {}', parent.type)

        reader.skipBlankLines()

        Paragraph para = null
        context.currentNode = para

        def lines = []

        def line = reader.peekLine()

        log.debug 'paragraph line = {}', line

        while (line != null && line.length() > 0) {
            if (para == null) {
                para = new Paragraph()
                para.parent = parent
                para.document = parent.document

                fillBlockFromHeader(para, context.blockHeader)

                context.currentNode = para
            }

            lines << line

            reader.nextLine()

            line = reader.peekLine()
            log.debug('paragraph line = {}', line)

            if (parentParser.toEndParagraph(context, line)) {
                break
            }
        }

        if (para != null) {
            // parse the inline nodes
            //parseInlineNodes(para, lines.join('\n'))
            para.lines = lines
        }

        log.debug('End parsing paragraph, parent type: {}', parent.type)

        if (line == null || line.length() == 0) {
            blockHeader = null
        }

        return para
    }
}
