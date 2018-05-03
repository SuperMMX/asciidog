package org.supermmx.asciidog

import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Blank
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.CommentLine
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.UnOrderedList

import org.supermmx.asciidog.lexer.Lexer
import org.supermmx.asciidog.lexer.Token

import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.block.DocumentParser
import org.supermmx.asciidog.parser.block.ParagraphParser

import org.supermmx.asciidog.plugin.Plugin
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class Parser {
    Document parseString(String content) {
        ParserContext context = new ParserContext()

        context.reader = Reader.createFromString(content)
        context.lexer = new Lexer(context.reader)

        return parseDocument(context)
    }

    Document parseFile(String filename) {
        ParserContext context = new ParserContext()

        context.reader = Reader.createFromFile(filename)
        context.lexer = new Lexer(context.reader)

        return parseDocument(context)
    }

    Document parseDocument(ParserContext context) {
        Document doc = (Document)parse(context)
        doc.attrs = context.attributes
        doc.attrs.inputFile = context.reader.inputFile

        return doc
    }

    static Block parse(ParserContext context) {
        def parserId = context.parserId

        // set the default parser
        if (parserId == null) {
            parserId = DocumentParser.ID
            context.parserId = parserId
        }

        // create document if the root parse is not document parser
        if (parserId != DocumentParser.ID
            && context.document == null) {
            context.document = new Document()
        }

        def parser = PluginRegistry.instance.getPlugin(parserId)
        if (parser == null) {
            userLog.error("Parser \"${parserId}\" specified in the context not found");
            return null;
        }

        return parser.parse(context);
    }

    /**
     * XML Name start chars
     */
    static final List<IntRange> ID_START_CHARS = [
        ((int)('A' as char))..((int)('Z' as char)),
        ((int)('_' as char))..((int)('_' as char)),
        ((int)('a' as char))..((int)('z' as char)),
        0xC0..0xD6,
        0xD8..0xF6,
        0xF8..0x2FF,
        0x370..0x37D,
        0x37F..0x1FFF,
        0x200C..0x200D,
        0x2070..0x218F,
        0x2C00..0x2FFF,
        0x3001..0xD7FF,
        0xF900..0xFDCF,
        0xFDF0..0xFFFD,
        0x10000..0xEFFFF
    ]

    /**
     * XML Name start chars
     */
    static final List<IntRange> ID_CHARS = ID_START_CHARS + [
            ((int)('-' as char))..((int)('-' as char)),
            ((int)('.' as char))..((int)('.' as char)),
            ((int)('0' as char))..((int)('9' as char)),
            0xB7..0xB7,
            0x0300..0x036F,
            0x203F..0x2040
    ]

    /**
     * Parse the text as a paragraph and get the children as inline nodes.
     *
     * @param value the string
     *
     * @return a list of inline nodes
     */
    public static List<Inline> parseInlines(String value) {
        def context = new ParserContext()
        def reader = Reader.createFromString(value)
        def lexer = new Lexer(reader)
        context.reader = reader
        context.lexer = lexer

        context.parserId = ParagraphParser.ID

        def para = PluginRegistry.instance.getPlugin(ParagraphParser.ID).parse(context)

        // FIXME: handle parent and document references correctly
        return para.children as List<Inline>
    }

    /**
     * Parse inline nodes
     *
     * @param context the parser context
     * @param parent the parent to attach the inline nodes to
     * @param endMatcher the matcher to end the inline parsing
     */
    public static void parseInlines(ParserContext context, InlineContainer parent, TokenMatcher endMatcher) {
        // the inline node stack
        def nodeStack = []
        // the corresponding parser stack
        def parserStack = []

        def lexer = context.lexer

        def buf = new StringBuilder()

        // last identified inline parser
        def lastParser = null
        // the current parent for next inline nodes
        def currentParent = parent

        while (lexer.hasNext()) {
            def token = lexer.peek()
            log.trace ''
            log.debug '==== next token = {}', token

            // whether to stop the inline parsing
            if (token.type == Token.Type.EOF) {
                break
            } else if (token.type == Token.Type.EOL) {
                lexer.next()

                def isEnd = endMatcher?.matches(context)
                log.trace '==== To end whole inline parsing: {}, next token = {}', isEnd, lexer.peek()
                if (isEnd) {
                    break
                } else {
                    if (lexer.peek().type != Token.Type.EOF) {
                        buf.append(token.value)
                    }
                    continue
                }
            }

            // check the end of the last parser
            log.trace '==== last parser = {}', lastParser?.id
            if (lastParser != null) {
                // check the end matcher of the current parent
                // TODO: check ends of all parents?
                def isEnd = lastParser.checkEnd(context, currentParent.parent)
                log.trace '==== check inline end = {}, next token = {}', isEnd, lexer.peek()
                if (isEnd) {
                    if (buf.length() > 0) {
                        TextNode textNode = new TextNode(buf.toString())
                        buf = new StringBuilder()
                        currentParent << textNode
                    }

                    if (parserStack.size() > 0) {
                        lastParser = parserStack.pop()
                    } else {
                        lastParser = null
                    }
                    currentParent = nodeStack.pop()

                    continue
                }
            }

            // find new parser
            log.trace '==== last parser = {}, parent = {}', lastParser?.id, currentParent.type
            def parserFound = false
            for (def plugin: PluginRegistry.instance.getInlineParsers()) {
                parserFound = plugin.checkStart(context, currentParent)
                if (parserFound) {
                    if (lastParser != null) {
                        parserStack.push(lastParser)
                    }
                    lastParser = plugin
                    break
                }
            }

            log.trace '==== parser found = {}, ID = {}', parserFound, lastParser?.id
            if (parserFound) {
                // start a new inline node
                def node = lastParser.parse(context, currentParent)

                if (buf.length() > 0) {
                    TextNode textNode = new TextNode(buf.toString())
                    buf = new StringBuilder()
                    currentParent << textNode
                }
                currentParent << node

                nodeStack.push(currentParent)
                currentParent = node
            } else {
                // append as text
                // TODO: CJKV new line checking
                buf.append(token.value)
                lexer.next()
            }
        }

        // remaining stuf
        if (buf.length() > 0) {
            TextNode textNode = new TextNode(buf.toString())
            buf = new StringBuilder()
            // should be the last one
            currentParent << textNode
        }
    }

    /**
     * Update document references for the specified node
     */
    private void updateReference(Node node) {
        def id = null

        if (node.id == null) {
            // TODO: duplicated id
            Utils.generateId(node)
        }

        id = node.id

        if (id != null && node.document != null) {
            id = Utils.normalizeId(id)
            node.document.references[(id)] = node
        }
    }

    void walk(Node node, Closure closure) {
        closure(node)
        if (node instanceof Block) {
            node.blocks.each { block ->
                walk(node, closure)
            }
        }
    }
}
