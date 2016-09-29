package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Authors
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.block.HeaderParser
import org.supermmx.asciidog.parser.block.PreambleParser
import org.supermmx.asciidog.parser.block.SectionParser
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class DocumentParser extends BlockParserPlugin {
    static final String HEADER_PROPERTY_DOCUMENT_TITLE = 'docTitle'

    static final String ID = 'plugin:parser:block:document'

    private HeaderParser headerParser
    private PreambleParser preambleParser
    private SectionParser sectionParser

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

        PluginRegistry pluginRegistry = PluginRegistry.instance
        headerParser = pluginRegistry.getPlugin(HeaderParser.ID)
        preambleParser = pluginRegistry.getPlugin(PreambleParser.ID)
        sectionParser = pluginRegistry.getPlugin(SectionParser.ID)

        return doc
    }

    @Override
    protected String doGetNextChildParser(ParserContext context, Block block) {
        def childParser = null

        def lastParser = context.lastParserId

        log.debug('Parser: {}, Last child parser = {}', id, lastParser)
        if (lastParser == null) {
            childParser = HeaderParser.ID
            context.childParserProps.expected = true
        } else {
            def header = nextBlockHeader(context)

            if (header?.type == Node.Type.SECTION) {
                // FIXME: correct expectedSectionLevel
                context.childParserProps.expectedSectionLevel = 1
                childParser = SectionParser.ID
            } else if (header?.type != null){
                context.childParserProps.expected = true
                childParser = PreambleParser.ID
            }
        }

        context.lastParserId = childParser

        log.debug('Parser: {}, Child parser = {}', id, childParser)
        return childParser
    }
}
