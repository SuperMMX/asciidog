package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Authors
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class DocumentParser extends BlockParserPlugin {
    static final String HEADER_PROPERTY_DOCUMENT_TITLE = 'docTitle'

    static final String ID = 'plugin:parser:block:document'

    DocumentParser() {
        nodeType = Node.Type.DOCUMENT
        id = ID
    }

    @Override
    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        SectionParser sectionParser =
            PluginRegistry.instance.getPlugin(SectionParser.ID)

        // if the first line is level 0 section,
        def isSection = sectionParser.checkStart(line, header, true)
        def level = header.properties[(SectionParser.HEADER_PROPERTY_SECTION_LEVEL)]
        def title = header.properties[(SectionParser.HEADER_PROPERTY_SECTION_TITLE)]

        if (isSection && level == 0) {
            header.properties[(HEADER_PROPERTY_DOCUMENT_TITLE)] = title
        }

        return true
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def reader = context.reader

        def title = header.properties[HEADER_PROPERTY_DOCUMENT_TITLE]
        Document doc = new Document(title: title)

        reader.nextLine()

        // make ready for child parsers
        context.lastParser = null

        return doc
    }

    protected BlockParserPlugin doGetNextChildParser(ParserContext context, Block block) {
        def childParser = null

        def lastParser = context.lastParser

        log.debug('Last child paser = {}', lastParser?.getClass())
        if (lastParser == null) {
            childParser = PluginRegistry.instance.getPlugin(HeaderParser.ID)
        } else if (lastParser in HeaderParser || lastParser in SectionParser) {
            def header = nextBlockHeader(context)
            if (header?.type == Node.Type.SECTION) {
                childParser = PluginRegistry.instance.getPlugin(SectionParser.ID)
            }
        }

        context.lastParser = childParser

        log.info('Child parser = {}', childParser?.getClass())
        return childParser
    }
}
