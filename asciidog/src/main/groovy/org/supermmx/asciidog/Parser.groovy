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

import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.block.DocumentParser

import org.supermmx.asciidog.plugin.Plugin
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class Parser {
    static Node parse(ParserContext context) {
        Block rootBlock = null
        def parserId = context.parserId
        if (parserId == null) {
            parserId = DocumentParser.ID
            context.parserId = parserId
        }

        def lastParserId = null
        def lastCursor = null

        while (parserId != null) {
            def parent = context.parent

            log.trace('Parser = {}, parent = {}',
                      parserId, parent?.getClass())

            if (parserId == lastParserId
                && context.reader.cursor == lastCursor) {
                log.error('Infinite loop detected, current parser: {}, cursor: {}',
                          parserId, context.reader.cursor)
            }

            def parser = PluginRegistry.instance.getPlugin(parserId)

            def block = context.block
            log.trace('Block class = {}, title = {}',
                      block?.getClass(), block?.title)

            // create the new block
            if (block == null) {
                block = parser.parse(context)
                log.trace('New block class = {}, title = {}',
                          block?.getClass(), block?.title)

                if (block != null) {
                    block.parent = parent
                    block.document = context.document

                    if (parent != null) {
                        parent << block
                    } else {
                        rootBlock = block
                    }

                    context.block = block
                }
            }

            lastParserId = parserId
            lastCursor = context.reader.cursor.clone()

            // get next child parser
            def childParserId = null
            if (block != null) {
                context.childParserProps.clear()

                childParserId = parser.getNextChildParser(context)

                if (childParserId != null) {
                    log.trace('Push current context')
                    context.push()

                    context.parserId = childParserId
                    context.parent = block
                    context.properties.putAll(context.childParserProps)
                    context.childParserProps.clear()
                }
            }

            log.trace('Next child parser = {}', childParserId)

            if (context.stop) {
                log.debug('Stop parsing...')
                break
            }
            // back to root and there are no more child parers
            if (parent == null && childParserId == null) {
                log.trace('No more parsing...')
                break
            }

            // fail to create the block or there are no more child parsers
            if (block == null || childParserId == null) {
                log.trace('Pop next context stack')
                context.pop()
            }

            parserId = context.parserId
        }

        return rootBlock
    }

    static final def AUTHOR_NAME_REGEX = '(?U)\\w[\\w\\-\'\\.]*'
    static final def AUTHOR_NAME_PATTERN = ~AUTHOR_NAME_REGEX
    static final def AUTHOR_REGEX = """(?x)
\\p{Blank}*
(${AUTHOR_NAME_REGEX})     # 1, first name

(?:
  \\p{Blank}+
  (${AUTHOR_NAME_REGEX})   # 2, middle name
)?

(?:
  \\p{Blank}+
  (${AUTHOR_NAME_REGEX})   # 3, last name
)?

(?:
  \\p{Blank}+
  <
    ([^>]+)                # 4, email
  >
)?

\\p{Blank}*
"""
    static final def AUTHOR_PATTERN = ~AUTHOR_REGEX
    static final def AUTHOR_LINE_PATTERN = ~"""(?x)
${AUTHOR_REGEX}
(?:
  ;
  ${AUTHOR_REGEX}
)*
"""
    static final def ATTRIBUTE_PATTERN = ~"""(?x)
:
(
  !?\\w.*?      # 1, attribute name
)
:
(?:
  \\p{Blank}+
  (.*)          # 2, attribute value
)?
"""
    static final def BLOCK_ANCHOR_PATTERN = ~'''(?x)
\\[\\[          # [[ to start

(               # 1, idname
[\\p{Alpha}:_]
[\\w:.-]*
)

(?:
  ,
  \\p{Blank}*
  (\\S.*)       # 2, reference text
)?

\\]\\]          # ]] to end
'''
    static final def BLOCK_TITLE_PATTERN = ~'''(?x)
^
\\.             # start with .
(
  [^\\s.].*     # 1, title
)
$
'''
    static final def BLOCK_ATTRIBUTES_PATTERN = ~'''(?x)
^
\\[                   # start with [
(                     # 1, atrribute line
  \\p{Blank}*
  [\\w{},.\\#"'%].*   # '
)
\\]                   # end with ]
$
'''
    static final def SECTION_PATTERN = ~'''(?x)
(                # 1, section identifier
  ={1,6}
)
\\p{Blank}+
(                # 2, whole title
  \\S+
  (?:\\p{Blank}+\\S+)*
)
\\s*
'''
    static final def LIST_PATTERN = ~'''(?x)
^
(
  \\p{Blank}*    # 1, leading
)
(                # 2, list character
  -
  |
  [*.]{1,5}
)
\\p{Blank}+
(                # 3, content
  .*
)
$
'''
    static final def LIST_CONTINUATION_PATTERN = ~'''(?x)
^
(
  \\p{Blank}*    # 1, leading
)
\\+
$
'''
    static final def COMMENT_LINE_PATTERN = ~'''(?x)
^
//
(                # 1, comment
  (?!
    //
  )
  .*
)
$
'''
    static final def STRONG_UNCONSTRAINED_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escaped
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
\\*\\*
(.+?)               # 3, content
\\*\\*
'''
    static final def STRONG_CONSTRAINED_PATTERN = ~'''(?Usxm)
(?<=
  ^ | [^\\w;:}]
)
(\\\\?)             # 1, escape
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
(?<!
  [\\w*]
)
\\*
(                   # 3, text
  [\\S&&[^*]]
  |
  [\\S&&[^*]] .*? [\\S&&[^*]]
)
\\*
(?!
  [\\w\\*]
)
'''
    static final def EMPHASIS_UNCONSTRAINED_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escape
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
__
(.+?)               # 3, text
__
'''
    static final def EMPHASIS_CONSTRAINED_PATTERN = ~'''(?Usxm)
(?<=
  ^ | [^\\w;:}]
)
(\\\\?)             # 1, escape
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
_
(                   # 3, text
  \\S
  |
  \\S .*? \\S
)
_
(?!
  \\w
)
'''
    static final def CROSS_REFERENCE_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escape
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
<<
(.*?)          # 3, id, allow any characters
>>
'''
    static final def ATTRIBUTE_REFERENCE_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escape
\\{
(                   # 2
  (set|counter2?)   # 3, set or counter
  :
  ([^:]+?)          # 4, attribute name
  (?:
    :
    (.*?)           # 5, attribute value
  )?
  |
  (                 # 6, attribute name
    [\\w-.]+?
  )
)
\\}
'''

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
     * internal class
     */
    protected static class BlockHeader {
        static final String SECTION_TITLE = 'secTitle'
        static final String SECTION_LEVEL = 'secLevel'

        static final String LIST_LEAD = 'listLead'
        static final String LIST_MARKER = 'listMarker'
        static final String LIST_MARKER_LEVEL = 'listMarkerLevel'
        static final String LIST_CONTENT_START = 'listContentStart'

        static final String COMMENT_LINE_COMMENT = 'comment'

        Node.Type type
        def id
        def title
        // block attributes
        def attributes = [:] as LinkedHashMap<String, String>

        // other properties
        def properties = [:]

        def actionBlocks = []

        String toString() {
            return """Block Header:
  Type: ${type}, ID: ${id}, Title: ${title}
  Attributes: ${attributes}
  Properties: ${properties}
  Action Blocks: ${actionBlocks}"""
        }
    }

    // Reader
    Reader reader

    // latest attributes
    AttributeContainer attrContainer = new AttributeContainer()

    // block header used to parse the next block, including section
    BlockHeader blockHeader

    ParserContext context = new ParserContext()

    Document parseString(String content) {
        reader = Reader.createFromString(content)

        return parseDocument()
    }

    Document parseFile(String filename) {
        reader = Reader.createFromFile(filename)

        Document doc = parseDocument()

        return doc
    }

    protected Document parseDocument() {
        log.debug('Start parsing document...')

        Document doc = new Document()
        doc.document = doc

        context.reader = reader
        context.document = doc

        Header header = parseHeader()

        def doctypeAttr = attrContainer.getAttribute(Document.DOCTYPE)
        doc.docType = Document.DocType.valueOf(doctypeAttr.value)
        log.debug("Document Type: ${doc.docType}")

        if (header == null) {
            return doc
        }

        doc << header
        doc.title = header.title

        // get type
        def type = doc.docType

        // preamble blocks
        log.debug('Start parsing document preamble blocks...')
        def preamble = new Preamble(parent: doc, document: doc)
        parseBlocks(preamble)
        if (preamble.blocks.size() > 0) {
            doc << preamble
        }

        log.debug('Start parsing document sections...')
        // sections
        int startingLevel = 1
        if (type == Document.DocType.book) {
            startingLevel = 0
        }

        log.trace("Document section starting level is ${startingLevel}")

        Section section = null
        while ((section = parseSection(doc, startingLevel)) != null) {
        }

        log.debug('End parsing document')
        return doc
    }

    /**
     * Parse a section.
     *
     * @param parent the parent block
     * @param expectedLevel the expected level the section should be in
     *
     * @return the parsed section, null if section is not found
     */
    protected Section parseSection(Block parent, int expectedLevel) {
        log.debug('Start parsing section for expectected level {}, parent type: {}',
                  expectedLevel, parent.type)

        if (blockHeader.type != Node.Type.SECTION) {
            return null
        }

        // check whether the next line is a section
        def id = blockHeader.id
        def level = blockHeader.properties[BlockHeader.SECTION_LEVEL]
        def title = blockHeader.properties[BlockHeader.SECTION_TITLE]

        if (level == -1) {
            // not a section
            return null
        }

        if (level != expectedLevel) {
            if (level > expectedLevel) {
                // wrong section level
                log.error('{}: Wrong section level {}, expected level is {}',
                          reader.cursor, level, expectedLevel)
                userLog.error('{}: Wrong section level {}, expected level is {}',
                              reader.cursor, level, expectedLevel)
            } else {
                if (parent.type == Node.Type.DOCUMENT) {
                    log.error('{}: Wrong section level {}, expected level is {}',
                              reader.cursor, level, expectedLevel)
                    userLog.error('{}: Wrong section level {}, expected level is {}',
                                  reader.cursor, level, expectedLevel)
                }
            }
            return null
        }

        // current section
        Section section = new Section(parent: parent,
                                      id: id,
                                      document: parent.document,
                                      level: level,
                                      title: title,
                                      attributes: blockHeader.attributes)
        if (parent != null) {
            parent << section

            // update reference
            updateReference(section)
        }

        // go over section line
        reader.nextLine()

        blockHeader = null

        // blocks in the section
        log.debug('Start parsing section blocks...')
        def blocks = parseBlocks(section)

        // parse sub sections
        log.debug('Start parsing subsections...')
        def subSection = null
        while ((subSection = parseSection(section, level + 1)) != null) {
        }

        log.debug('End parsing section for expectected level {}, parent type: {}',
                  expectedLevel, parent.type)
        return section
    }

    /**
     * Parse blocks
     *
     * @return a list of blocks
     */
    protected List<Block> parseBlocks(Block parent) {
        log.debug('Start parsing blocks, parent type: {}', parent.type)

        def inList = (parent instanceof ListItem)

        def blocks = []

        boolean listContinuation = false

        boolean first = true
        while (true) {
            def block = null

            // first one is considered as a simple paragraph
            if (inList && first) {
                first = false

                block = parseParagraph(parent)
            } else {
                first = false

                if (blockHeader == null) {
                    parseBlockHeader()
                }

                if (blockHeader.type == null) {
                    break
                }

                if (blockHeader.type == Node.Type.SECTION) {
                    break
                }

                // currently in list, but next block is not a list,
                // and not in list continuation
                if (inList && !isList(blockHeader.type) && !listContinuation) {
                    break
                }

                log.debug('current block header: ')
                log.debug('{}', blockHeader)

                switch (blockHeader.type) {
                case { it.isAction }:
                    block = new Blank()
                    fillBlockHeaders(block)

                    blockHeader = null
                    break
                case Node.Type.COMMENT_LINE:
                    block = new CommentLine()
                    fillBlockHeaders(block)
                    block.lines << blockHeader.properties[BlockHeader.COMMENT_LINE_COMMENT]

                    blockHeader = null

                    reader.nextLine()
                    break
                case Node.Type.PARAGRAPH:
                    block = parseParagraph(parent)
                    break
                case Node.Type.ORDERED_LIST:
                case Node.Type.UNORDERED_LIST:
                    // check marker and level first
                    def lead = blockHeader.properties[BlockHeader.LIST_LEAD]
                    def marker = blockHeader.properties[BlockHeader.LIST_MARKER]
                    def markerLevel = blockHeader.properties[BlockHeader.LIST_MARKER_LEVEL]
                    def list = parent.parent

                    if (inList && isListItem(parent, lead, marker, markerLevel)) {
                        // is the list item with same level
                    } else {
                        block = parseList(parent)
                    }
                    break
                }

            }

            if (block == null) {
                break;
            }

            blocks << block
            if (parent != null) {
                parent << block

                // update reference
                updateReference(block)
            }

            if (inList) {
                // in list
                def line = reader.peekLine()
                log.debug('ListItem Blocks: nextLine = {}', line)

                // list continuation
                def lead = isListContinuation(line)
                if (lead != null && lead == parent.parent.lead) {
                    reader.nextLine()
                    listContinuation = true
                    blockHeader = null
                } else {
                    listContinuation = false
                }

                log.debug('ListItem Blocks: lead = {}, list continuation = {}',
                          lead, listContinuation)

            }
        }

        log.debug('End parsing blocks, parent type: {}', parent.type)
        return blocks
    }

    protected AdocList parseList(Block parent) {
        log.debug('Start parsing list, parent type: {}', parent.type)

        if (blockHeader == null) {
            parseBlockHeader()
        }

        Node.Type type = blockHeader.type
        if (type != Node.Type.ORDERED_LIST
            && type != Node.Type.UNORDERED_LIST) {
            return null
        }

        def list = null
        switch (type) {
        case Node.Type.ORDERED_LIST:
            list = new OrderedList()
            break
        case Node.Type.UNORDERED_LIST:
            list = new UnOrderedList()
            break
        }

        fillBlockHeaders(list)

        list.parent = parent
        list.document = parent.document
        list.lead = blockHeader.properties[BlockHeader.LIST_LEAD]
        list.marker = blockHeader.properties[BlockHeader.LIST_MARKER]
        list.markerLevel = blockHeader.properties[BlockHeader.LIST_MARKER_LEVEL]
        list.level = 1

        // cleanup the header to parse the list items
        def newBlockHeader = new BlockHeader()
        newBlockHeader.type = blockHeader.type
        newBlockHeader.properties = blockHeader.properties
        blockHeader = newBlockHeader

        if (parent.type == Node.Type.LIST_ITEM) {
            list.level = parent.parent.level + 1
        }

        // parse items
        ListItem item = null
        while ((item = parseListItem(list)) != null) {
        }

        log.debug('End parsing list, parent type: {}', parent.type)
        return list
    }

    /**
     * Parse next list item
     */
    protected ListItem parseListItem(AdocList list) {
        log.debug('Start parsing list item, parent type: {}', list.type)

        if (!isList(blockHeader.type)) {
            return null
        }

        def marker = blockHeader.properties[BlockHeader.LIST_MARKER]
        def markerLevel = blockHeader.properties[BlockHeader.LIST_MARKER_LEVEL]

        // not the same level
        if (marker != list.marker
            || markerLevel != list.markerLevel) {
            return null
        }

        ListItem item = new ListItem()
        fillBlockHeaders(item)
        item.parent = list
        item.document = list.document

        // first line of the list item
        def line = reader.peekLine()
        def index = blockHeader.properties[BlockHeader.LIST_CONTENT_START]
        blockHeader = null

        // skip the list markers and blanks before the real content
        reader.skipChars(index)

        // parse list item blocks
        def blocks = parseBlocks(item)

        if (blocks.size() == 0) {
            item = null
        } else {
            list << item
        }

        log.debug('End parsing list item, parent type: {}', list.type)
        return item
    }

    /**
     * Parse a paragraph
     *
     * @param parent the parent block
     */
    protected Paragraph parseParagraph(Block parent) {
        log.debug('Start parsing paragraph, parent type: {}', parent.type)

        boolean inList = (parent instanceof ListItem)

        reader.skipBlankLines()

        Paragraph para = null

        def lines = []

        boolean first = true
        def line = reader.peekLine()
        if (log.isDebugEnabled()) {
            log.debug 'paragraph line = {}', line
        }
        while (line != null && line.length() > 0) {
            if (inList) {
                if (!first) {
                    // is list continuation
                    if (isListContinuation(line) != null) {
                        break
                    }

                    // check block header for every line
                    parseBlockHeader()

                    if (isList(blockHeader.type)) {
                        break
                    }
                }
            }

            if (para == null) {
                para = new Paragraph()
                para.parent = parent
                para.document = parent.document
                if (blockHeader != null) {
                    fillBlockHeaders(para)
                }
            }
            lines << line

            reader.nextLine()

            line = reader.peekLine()
            if (log.isDebugEnabled()) {
                log.debug 'paragraph line = {}', line
            }

            first = false
        }

        // parse the inline nodes
        parseInlineNodes(para, lines.join('\n'))

        log.debug('End parsing paragraph, parent type: {}', parent.type)

        if (line == null || line.length() == 0) {
            blockHeader = null
        }

        return para
    }

    /**
     * Parse document header
     */
    protected Header parseHeader() {
        log.debug('Start parsing document header...')

        Header header = null

        reader.skipBlankLines()

        def line = reader.peekLine()

        log.trace("Header Line is $line")
        if (line == null) {
            return null
        }

        def (level, title) = isSection(line)
        if (level != 0) {
            // section
            // doesn't have a header
            return null
        }

        reader.nextLine()

        header = new Header()
        header.title = title

        // parse author
        def authors = parseAuthors()
        log.debug("Authors: $authors")
        if (authors != null) {
            header.authors = authors
        }

        // FIXME: parse revision

        // parse attributes
        AttributeEntry attr = null
        while ((attr = parseAttribute()) != null) {
            log.trace("Header Attribute ${attr.name} = ${attr.value}")
            // update the latest value
            attrContainer.setAttribute(attr.name, attr.value)

            // track the attribute action
            header << attr
        }

        log.debug('End parsing document header')

        return header
    }

    /**
     * Parse authors from current line
     */
    protected List<Author> parseAuthors() {
        log.debug('Start parsing document authors...')

        def line = reader.peekLine()

        log.trace("Author line is $line")
        if (line == null) {
            return null
        }

        if (!AUTHOR_LINE_PATTERN.matcher(line).matches()) {
            return null
        }

        reader.nextLine()

        def authors = [] as List<Author>
        line.split(";").each {
            authors << createAuthor(it)
        }

        log.debug('End parsing document authors')
        return authors
    }

    /**
     * Parse an attribute from next line
     *
     * @return the parsed attribute, null if not an attribute
     */
    protected AttributeEntry parseAttribute() {
        log.debug('Start parsing attribute...')

        def line = reader.peekLine()

        def (name, value) = isAttribute(line)
        if (name == null) {
            return null
        }

        reader.nextLine()

        AttributeEntry attr = createAttribute(name, value)

        log.debug('End parsing attribute')

        return attr
    }

    protected AttributeEntry createAttribute(String name, String value) {
        // FIXME: value of multiple lines
        AttributeEntry attr = new AttributeEntry([ name: name, value: value ])
        return attr
    }

    /**
     * Fill common block headers for the current block
     */
    protected void fillBlockHeaders(Block block) {
        log.debug('Fill headers for block: {}', block.type)
        block.with {
            id = blockHeader.id
            title = blockHeader.title
            attributes = blockHeader.attributes
            blocks.addAll(0, blockHeader.actionBlocks)
        }
    }

    /**
     * Parse a block header that is used to parse content further.
     * The id, attribute, title are read, but not the block start line.
     */
    protected BlockHeader parseBlockHeader() {
        log.debug('Start parsing block header...')

        reader.skipBlankLines()

        BlockHeader header = new BlockHeader()

        def line = null
        while ((line = reader.peekLine()) != null) {
            if (line.length() == 0) {
                break
            }

            // check attribute definition
            def (attrName, attrValue) = isAttribute(line)
            if (attrName != null) {
                header.type = Node.Type.DEFINE_ATTRIBUTE

                reader.nextLine()
                header.actionBlocks << createAttribute(attrName, attrValue)

                continue
            }

            // check id
            def (anchorId, anchorRef) = isBlockAnchor(line)
            if (anchorId != null) {
                header.id = anchorId

                reader.nextLine()

                continue
            }

            // check attributes
            def attrs = isBlockAttributes(line)
            if (attrs != null) {
                header.attributes.putAll(attrs)

                reader.nextLine()

                continue
            }

            // check title
            def title = isBlockTitle(line)
            if (title != null) {
                header.title = title

                reader.nextLine()

                continue
            }

            // check comment line
            def comment = isCommentLine(line)
            if (comment != null) {
                header.type = Node.Type.COMMENT_LINE
                header.properties[BlockHeader.COMMENT_LINE_COMMENT] = comment

                break
            }

            // check section
            def (secLevel, secTitle) = isSection(line)
            if (secLevel != -1) {
                header.type = Node.Type.SECTION
                header.properties[BlockHeader.SECTION_LEVEL] = secLevel
                header.properties[BlockHeader.SECTION_TITLE] = secTitle

                break
            }

            // check list
            def (listType, listLead, listMarker, markerLevel, listContentStart) = isListLine(line)
            if (listType != null) {
                header.type = listType
                header.properties[BlockHeader.LIST_LEAD] = listLead
                header.properties[BlockHeader.LIST_MARKER] = listMarker
                header.properties[BlockHeader.LIST_MARKER_LEVEL] = markerLevel
                header.properties[BlockHeader.LIST_CONTENT_START] = listContentStart

                break
            }

            // check delimited block

            // normal paragraph
            header.type = Node.Type.PARAGRAPH
            break
        }

        blockHeader = header

        log.debug('{}', header)
        log.debug('End parsing block header')

        return header
    }

    /**
     * Parse inline nodes from a text and construct the node tree
     */
    protected static List<Inline> parseInlineNodes(InlineContainer parent, String text) {
        InlineInfo topInfo = new InlineInfo(start: 0,
                                            end: text.length(),
                                            contentStart: 0,
                                            contentEnd: text.length(),
                                            inlineNode: parent)

        // matchers, plugin id -> matcher
        def matchers = [:]
        // starting index -> plugin id
        def sortingPlugins = new TreeMap<Integer, String>()

        // go through all inline plugins
        PluginRegistry.instance.getInlineParsers().each { plugin ->
            log.debug 'Parse inline with plugin: {}', plugin.id
            def m = plugin.pattern.matcher(text)
            if (m.find()) {
                matchers[(plugin.id)] = m
                sortingPlugins[(m.start())] = plugin.id
            }
        }

        // find the parent info for an inline info starting container info
        def findParentInfo
        findParentInfo = { containerInfo, int start, int end ->
            log.debug("findParentInfo: container start = ${containerInfo.start}, end = ${containerInfo.end}")

            if (start < containerInfo.contentStart
                || end > containerInfo.contentEnd) {
                return null
            }

            def result = null

            for (def childInfo : containerInfo.children) {
                if (childInfo.inlineNode instanceof InlineContainer) {
                    result = findParentInfo(childInfo, start, end)
                }
                if (result != null) {
                    break
                }
            }

            if (result == null) {
                result = containerInfo
            }

            return result
        }

        def resultInlines = []

        // fill gap
        def fillGap
        fillGap = { InlineInfo containerInfo, inlineInfo ->
            if (inlineInfo == null) {
                containerInfo.children.each { childInfo ->
                    if (childInfo.inlineNode instanceof InlineContainer) {
                        fillGap(childInfo, inlineInfo)
                    }
                }
            }

            log.debug 'fillGap: container info = {}', containerInfo
            if (!containerInfo.fillGap) {
                return
            }

            def lastEnd = containerInfo.contentStart
            def lastNodeInfo = null
            if (containerInfo.children.size() > 0) {
                lastNodeInfo = containerInfo.children.last()
            }
            if (lastNodeInfo != null) {
                lastEnd = lastNodeInfo.end
            }

            def thisEnd = containerInfo.contentEnd
            if (inlineInfo != null) {
                thisEnd = inlineInfo.start
            }

            log.debug "fillGap: last end = ${lastEnd}, this end = ${thisEnd}"
            if (lastEnd < thisEnd) {
                def node = new TextNode(parent: containerInfo.inlineNode,
                                        document: containerInfo.inlineNode.document,
                                        text: text.substring(lastEnd, thisEnd))
                def nodeInfo = new InlineInfo()
                nodeInfo.with {
                    start = lastEnd
                    end = thisEnd
                    contentStart = start
                    contentEnd = end

                    inlineNode = node
                }

                log.debug "text node = ${node}"
                log.debug "text node info = ${nodeInfo}"

                containerInfo << nodeInfo
                containerInfo.inlineNode << node

                if (containerInfo == topInfo) {
                    resultInlines << node
                }
            }
        }

        while (sortingPlugins.size() > 0) {
            log.debug 'Sorted Plugins: {}', sortingPlugins

            def entry = sortingPlugins.find { true }
            def startIndex = entry.key
            def pluginId = entry.value

            def m = matchers[(pluginId)]
            def endIndex = m.end(0)

            if (log.debugEnabled) {
                log.debug "==== START ===="
                log.debug "Index: ${startIndex}, endIndex: ${endIndex}, From plugin: ${pluginId}, group = ${m.group()}"
            }

            def plugin = PluginRegistry.instance.getPlugin(pluginId)

            // check whether the matching region intersect with other inlines
            def nextFind = false
            def parentInfo = findParentInfo(topInfo, startIndex, endIndex)
            log.debug('Found parent info: {}', parentInfo)
            for (def childInfo : parentInfo.children) {
                if (childInfo.overlaps(startIndex, endIndex)) {
                    nextFind = true
                    break
                }
            }

            log.debug('Need to find next: {}', nextFind)
            if (nextFind) {
                sortingPlugins.remove(startIndex)
                log.debug('end index = {}, text length = {}', endIndex, text.length())
                if (startIndex < text.length() && m.find(startIndex + 1)) {
                    log.debug('New search for plugin {} from index {}', plugin.id, m.start())
                    sortingPlugins[(m.start())] = plugin.id
                }

                continue
            }

            def groupList = []
            (0..m.groupCount()).each { index ->
                groupList << m.group(index)
            }

            def infoList = plugin.parse(m, groupList)

            log.debug('Parsed info list: {}', infoList)
            for (def info: infoList) {
                // assume the inlines are in order and desn't exceed the parent's boundary

                //def parentInfo = findParentInfo(topInfo, info)

                if (parentInfo != null) {
                    def parentNode = parentInfo.inlineNode
                    def childNode = info.inlineNode

                    // whether the node can be fit into the parent node
                    def lastEnd = parentInfo.contentStart
                    def lastNodeInfo = null
                    if (parentInfo.children.size() > 0) {
                        lastNodeInfo = parentInfo.children.last()
                    }
                    if (lastNodeInfo != null) {
                        lastEnd = lastNodeInfo.end
                    }

                    if (log.debugEnabled) {
                        log.debug "Check new node in parent: lastEnd = ${lastEnd}, start = ${info.start}"
                    }
                    if (info.start < lastEnd) {
                        break
                    }

                    if (log.debugEnabled) {
                        log.debug("Fill the gap and append the new node");
                    }

                    // fill the gap before
                    fillGap(parentInfo, info)

                    parentInfo << info

                    parentNode << childNode
                    childNode.parent = parentNode
                    childNode.document = parentNode.document

                    if (parentInfo == topInfo) {
                        resultInlines << childNode
                    }
                } else {
                    break
                }
            }

            sortingPlugins.remove(startIndex)
            if (m.find()) {
                if (log.debugEnabled) {
                    log.debug "Next match: start = ${m.start()}"
                }
                sortingPlugins[(m.start())] = plugin.id
            }

            if (log.debugEnabled) {
                log.debug "==== END ===="
            }
        }

        log.info 'Filling all gaps...'
        fillGap(topInfo, null)

        return resultInlines
    }

    /**
     * Create an author from a string
     */
    protected static Author createAuthor(String authorText) {
        // should always match from parser
        def m = Parser.AUTHOR_PATTERN.matcher(authorText)
        if (!m.matches()) {
            return null
        }

        def groups = m[0]

        Author newAuthor = new Author()
        newAuthor.with {
            firstname = groups[1]
            if (groups[3] != null) {
                middlename = groups[2]
                lastname = groups[3]
            } else {
                lastname = groups[2]
            }
            email = groups[4]

            def names = [ firstname, middlename, lastname ] - null
            author = names.join(' ')

            authorinitials = names.collect{ it[0] }.join('')
        }

        return newAuthor
    }

    /**
     * Whether the line represents a section, like
     *
     * == Section Title
     *
     * @param line the line to check
     *
     * @return the section level, -1 if not a section,
     *         the section title, null if not a section,
     */
    protected static List<Object> isSection(String line) {
        if (line == null) {
            return [ -1, null ]
        }

        def m = SECTION_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ -1, null ]
        }

        int level = m[0][1].length() - 1
        String title = m[0][2]

        return [ level, title ]
    }

    /**
     * Whether the line represents an attribute definition, like
     *
     * :attr-name: attribute value
     *
     * @param line the line to check
     *
     * @return the attribute name, null if not an attribute
     *         the attribute value, null if not an attribute
     */
    protected static List<String> isAttribute(String line) {
        if (line == null) {
            return [ null, null ]
        }

        def m = ATTRIBUTE_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ null, null ]
        }

        String key = m[0][1]
        String value = m[0][2]

        return [ key, value ]
    }

    /**
     * Whether a line represents a block anchor, like
     *
     * [[block id]]
     *
     * @param line the line to check
     *
     * @return the anchor id, null if not a block anchor
     *         the reference text, null if not a block anchor
     *         or not specified
     */
    protected static List<String> isBlockAnchor(String line) {
        if (line == null) {
            return [ null, null ]
        }

        def m = BLOCK_ANCHOR_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ null, null ]
        }

        String id = m[0][1]
        String ref = m[0][2]

        return [ id, ref ]
    }

    /**
     * Whether a line represents a block title, like
     *
     * .BlockTitle
     */
    protected static String isBlockTitle(String line) {
        if (line == null) {
            return null
        }

        def m = BLOCK_TITLE_PATTERN.matcher(line)
        if (!m.matches()) {
            return null
        }

        String title = m[0][1]

        return title
    }

    /**
     * Whether the line represents block attributes definition, like
     *
     * [style, key="value" new-key='new value' ]
     */
    protected static Map<String, String> isBlockAttributes(String line) {
        if (line == null) {
            return null
        }

        def m = BLOCK_ATTRIBUTES_PATTERN.matcher(line)
        if (!m.matches()) {
            return null
        }

        line = m[0][1]

        return parseAttributes(line)
    }

    /**
     * Parse general attributes, not the document attributes.
     *
     * @param text the attributes text with []
     */
    protected static Map<String, String> parseAttributes(String text) {
        def line = text

        def attrs = [:] as LinkedHashMap<String, String>

        // size of the attribute line
        def size = line.length()

        def key = null
        def value = null

        def index = 0

        // main loop
        while (index < size) {
            def buf = []

            def quote = null

            def ch = line[index]

            // skip blanks
            while (ch == ' ') {
                index ++
                ch = line[index]
            }

            // starting quote
            if (ch == "'" || ch == '"') {
                quote = ch
                index ++

                ch = ''
            }

            // find the key or value in quotes or not
            while (index < size) {
                ch = line[index]

                if (quote == null) {
                    if (',='.indexOf(ch) >= 0) {
                        // not in quotes, and is a delimiter
                        break
                    }
                }

                // ending quote
                if ((ch == "'" || ch == '"')
                    && ch == quote) {
                    index ++
                    ch = ''
                    break
                }

                buf << ch
                ch = ''

                index ++
            }

            // join the characters
            def str = buf.join('')

            // trim the value if not in quote
            if (quote == null) {
                str = str.trim()
            } else {
                // skip all blanks after the quote
                while (index < size) {
                    ch = line[index]
                    if (ch == ' ') {
                        index ++
                    } else {
                        break
                    }
                }

                quote = null
            }

            if (index >= size) {
                ch = ','
            } else {
                ch = line[index]
            }

            if (ch == ',') {
                // end of the value
                // an attribute defintion is over

                if (key == null) {
                    key = str
                } else {
                    value = str
                }

                // add the attribute
                attrs[(key)] = value

                // reset
                key = null
                value = null
            } else if (ch == '=') {
                // end of the key
                key = str
            } else {
                // invalid
            }

            if (index < size) {
                index ++
            }
        }

        return attrs
    }

    /**
     * Whether the line is the start of a list, like
     *
     * *** abc
     * - abc
     * .. abc
     *
     * @return the type of list
     *         the list marker, *, - or .
     *         the level of the list
     *         the start index of the first line of the list item content
     */
    protected static List isListLine(String line) {
        if (line == null) {
            return [ null, null, null, -1, null ]
        }

        def m = LIST_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ null, null, null, -1, null ]
        }

        Node.Type type = null

        def lead = m[0][1]
        def markers = m[0][2]
        def contentStart = m.start(3);
        int markerLevel = markers.length()

        def marker = markers[0]
        switch (marker) {
        case '*':
        case '-':
            type = Node.Type.UNORDERED_LIST
            break
        case '.':
            type = Node.Type.ORDERED_LIST
            break
        default:
            // should not happen
            break
        }

        return [ type, lead, marker, markerLevel, contentStart ]
    }

    /**
     * Whether a line is the list continuation, like
     *
     * +
     *
     * or
     *
     *    +
     *
     * @return leading spaces if is a list continuation, or null
     */
    protected static String isListContinuation(String line) {
        if (line == null) {
            return null
        }

        def m = LIST_CONTINUATION_PATTERN.matcher(line)
        if (!m.matches()) {
            return null
        }

        String lead = m[0][1]

        return lead
    }

    /**
     * Whether a node type represents a list
     */
    protected static boolean isList(Node.Type type) {
        return type?.isList()
    }

    /**
     * Whether a line represents a comment line, like
     *
     * // this is a comment line
     *
     * @return the comment content, null if not a comment line
     */
    protected static String isCommentLine(String line) {
        if (line == null) {
            return null
        }

        def m = COMMENT_LINE_PATTERN.matcher(line)
        if (!m.matches()) {
            return null
        }

        String comment = m[0][1]

        return comment
    }

    /**
     * Whether the marker and marker level represent a new list
     * or an item of one of the ancestor lists, by checking the
     * marker and the marker level
     */
    protected boolean isListItem(Block parent, String lead, String marker, int markerLevel) {
        boolean result = false
        boolean found = false

        while (!found && parent != null) {
            switch (parent.type) {
            case Node.Type.LIST_ITEM:
                break
            case Node.Type.ORDERED_LIST:
            case Node.Type.UNORDERED_LIST:
                if (parent.marker == marker
                    && parent.markerLevel == markerLevel) {
                    found = true
                    result = true
                }
                break
            default:
                found = true
                break
            }

            if (parent.parent == parent) {
                break
            } else {
                parent = parent.parent
            }
        }

        return result
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
