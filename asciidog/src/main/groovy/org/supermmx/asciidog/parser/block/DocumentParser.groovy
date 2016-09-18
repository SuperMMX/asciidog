package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
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
        Document doc = new Document()

        doc.parent = doc

        context.document = doc

        reader.nextLine()

        return doc
    }

    @Override
    protected boolean doIsValidChild(ParserContext context, Block block, BlockHeader header) {
        def doc = block

        // article doctype
        def valid = (header.type == NodeType.SECTION)

        return valid
    }

    @Override
    protected List<Node> doParseChildren(ParserContext context, Block parent) {
        def children = []

        def doc = parent

        def isFullDoc = true

        if (isFullDoc) {
            // full document that expects document headers,
            // then sections

            // authors

            // revision

            // attributes

            // preamble

            // sections
            /*
            SectionParser sectionParser =
                PluginRegistry.instance.getPlugin(SectionParser.ID)

            def section = null

            while ((section = sectionParser.parse(context)) != null) {
                // doc type
                children << section
            }
            */

        } else {
            // simple document that expects blocks including sections

            // any block
        }

        return children
    }
}
