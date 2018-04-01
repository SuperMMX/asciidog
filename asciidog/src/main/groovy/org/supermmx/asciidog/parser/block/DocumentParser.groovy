package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Authors
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher
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

    DocumentParser() {
        nodeType = Node.Type.DOCUMENT
        id = ID
    }

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        return true
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def lexer = context.lexer

        def (markToken, wsToken, titleToken) = lexer.peek(3)

        if (markToken == null) {
            return null
        }

        def doc = new Document()
        if (markToken.value == '='
            && wsToken?.type == Token.Type.WHITE_SPACES
            && titleToken?.type != Token.Type.EOL) {
            // normal document

            // advance to title
            lexer.next(2)

            // TODO: parse the title as inlines
            def title = lexer.combineTo(TokenMatcher.type(Token.Type.EOL))
            doc.title = title
        } else {
            // inline document
            context.attributes.setAttribute(Document.DOCTYPE, Document.DocType.inline.toString())
        }

        // set the document for the context
        context.document = doc

        return doc
    }

    @Override
    protected List<ChildParserInfo> doGetChildParserInfos(ParserContext context) {
        // any block for inline without header and preamble
        if (context.attributes.getAttribute(Document.DOCTYPE).value == Document.DocType.inline.toString()) {
            return [
                ChildParserInfo.find()
            ]
        }

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
