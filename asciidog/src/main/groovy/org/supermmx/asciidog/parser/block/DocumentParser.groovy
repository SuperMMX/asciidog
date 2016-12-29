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

        // set the document for the context
        context.document = doc

        return doc
    }

    @Override
    protected List<ChildParserInfo> doGetChildParserInfos(ParserContext context) {
        return [
            ChildParserInfo.zeroOrOne(HeaderParser.ID),
            ChildParserInfo.zeroOrOne(PreambleParser.ID).findHeader(),
            ChildParserInfo.zeroOrMore(SectionParser.ID).findHeader().doBeforeParsing { latestContext, document ->
                // correct expectedSectionLevel according to the document type
                def docTypeAttr = latestContext.attributes.getAttribute(Document.DOCTYPE)
                def docType = Document.DocType.valueOf(docTypeAttr.value)

                def expectedLevel = 1
                if (docType == Document.DocType.book) {
                    expectedLevel = 0
                }

                latestContext.childParserProps.expectedSectionLevel = expectedLevel

                return true
            }
        ]
    }
}
