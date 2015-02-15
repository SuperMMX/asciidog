package org.supermmx.asciidog

import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.CommentLine
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.UnOrderedList

class Parser {
    static final def AUTHOR_NAME_REGEX = '\\w[\\w\\-\'\\.]*'
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
  (.*)          # 2, attrinute value
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
\\p{Blank}*
(                # 1, list character
  -
  |
  [*.]{1,5}
)
\\p{Blank}+
(                # 2, content
  .*
)
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

    /**
     * internal class
     */
    protected static class BlockHeader {
        static final String SECTION_TITLE = 'secTitle'
        static final String SECTION_LEVEL = 'secLevel'

        static final String LIST_MARKER = 'listMarker'
        static final String LIST_MARKER_LEVEL = 'listMarkerLevel'
        static final String LIST_FIRST_LINE = 'listFirstLine'

        static final String COMMENT_LINE_COMMENT = 'comment'

        Node.Type type
        def id
        def title
        // block attributes
        def attributes = [:] as LinkedHashMap<String, String>

        // other properties
        def properties = [:]
    }

    // Reader
    Reader reader

    // latest attributes
    AttributeContainer attrContainer = new AttributeContainer()

    // block header used to parse the next block, including section
    BlockHeader blockHeader

    Document parseString(String content) {
        reader = Reader.createFromString(content)

        return parseDocument()
    }

    Document parseFile(String filename) {
        null
    }

    protected Document parseDocument() {
        Document doc = new Document()

        if (reader.peekLine() == null) {
            return doc
        }

        doc.header = parseHeader()

        def doctypeAttr = attrContainer.getAttribute(Document.DOCTYPE)
        doc.docType = Document.DocType.valueOf(doctypeAttr.value)

        // get type
        def type = doc.docType

        // preamble, as a section content
        def preambleBlocks = parseBlocks(doc)

        // sections
        int startingLevel = 1
        if (type == Document.DocType.book) {
            startingLevel = 0
        }

        Section section = null
        while ((section = parseSection(doc, startingLevel)) != null) {
            doc << section
        }

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
        if (blockHeader?.type != Node.Type.SECTION) {
            return null
        }

        // check whether the next line is a section
        def id = blockHeader?.id
        def level = blockHeader?.properties[BlockHeader.SECTION_LEVEL]
        def title = blockHeader?.properties[BlockHeader.SECTION_TITLE]

        if (level == -1) {
            // not a section
            return null
        }

        if (level != expectedLevel) {
            // wrong section level
            return null
        }

        reader.nextLine()

        // current section
        Section section = new Section(parent: parent,
                                      id: id,
                                      document: parent.document,
                                      level: level,
                                      title: title)

        // blocks in the section
        def blocks = parseBlocks(section)

        // parse sub sections
        def subSection = null
        while ((subSection = parseSection(section, level + 1)) != null) {
            section << subSection
        }

        return section
    }

    /**
     * Parse blocks
     *
     * @return a list of blocks
     */
    protected List<Block> parseBlocks(Block parent) {
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
                parseBlockHeader()

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

                switch (blockHeader.type) {
                case Node.Type.COMMENT:
                    block = new CommentLine()
                    block.lines << blockHeader.properties[BlockHeader.COMMENT_LINE_COMMENT]

                    reader.nextLine()
                    break
                case Node.Type.PARAGRAPH:
                    block = parseParagraph(parent)
                    break
                case Node.Type.ORDERED_LIST:
                case Node.Type.UNORDERED_LIST:
                    // check marker and level first
                    def marker = blockHeader.properties[BlockHeader.LIST_MARKER]
                    def markerLevel = blockHeader.properties[BlockHeader.LIST_MARKER_LEVEL]
                    def list = parent.parent

                    if (inList && isListItem(parent, marker, markerLevel)) {
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
            parent << block

            if (inList) {
                // in list
                def line = reader.peekLine()

                // list continuation
                if (isListContinuation(line)) {
                    reader.nextLine()
                    listContinuation = true
                } else {
                    listContinuation = false
                }
            }
        }

        return blocks
    }

    protected AdocList parseList(Block parent) {
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

        list.parent = parent
        list.marker = blockHeader.properties[BlockHeader.LIST_MARKER]
        list.markerLevel = blockHeader.properties[BlockHeader.LIST_MARKER_LEVEL]
        list.level = 1
        if (parent.type == Node.Type.LIST_ITEM) {
            list.level = parent.parent.level + 1
        }

        // parse items
        ListItem item = null
        while ((item = parseListItem(list)) != null) {
        }

        return list
    }

    /**
     */
    protected ListItem parseListItem(AdocList list) {
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

        // first line of the list item
        def line = blockHeader.properties[BlockHeader.LIST_FIRST_LINE]

        ListItem item = new ListItem()
        item.parent = list
        item.document = list.document

        // parse list item blocks
        def blocks = parseBlocks(item)

        if (blocks.size() == 0) {
            item = null
        } else {
            Paragraph para = item.blocks[0]
            para.lines[0] = line

            list << item
        }

        return item
    }

    /**
     * Parse a paragraph
     *
     * @param parent the parent block
     */
    protected Paragraph parseParagraph(Block parent) {
        boolean inList = (parent instanceof ListItem)

        reader.skipBlankLines()

        Paragraph para = null

        boolean first = true
        def line = reader.peekLine()
        while (line != null && line.length() > 0) {
            if (inList && !first) {
                if (isListContinuation(line)) {
                    break
                }

                // check block header for every line
                parseBlockHeader()

                if (isList(blockHeader.type)) {
                    break
                }
            }

            if (para == null) {
                para = new Paragraph()
                para.parent = parent
                para.document = parent.document
            }
            para.lines << line

            reader.nextLine()

            line = reader.peekLine()

            first = false
        }

        return para
    }

    /**
     * Parse document header
     */
    protected Header parseHeader() {
        Header header = null

        reader.skipBlankLines()

        def line = reader.peekLine()

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
        header.authors = parseAuthors()

        // FIXME: parse revision

        // parse attributes
        AttributeEntry attr = null
        while ((attr = parseAttribute()) != null) {
            // update the latest value
            attrContainer.setAttribute(attr.name, attr.value)

            // track the attribute action
            header << attr
        }

        return header
    }

    /**
     * Parse authors from current line
     */
    protected List<Author> parseAuthors() {
        def line = reader.peekLine()

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
        return authors
    }

    /**
     * Parse an attribute from next line
     *
     * @return the parsed attribute, null if not an attribute
     */
    protected AttributeEntry parseAttribute() {
        def line = reader.peekLine()

        def (name, value) = isAttribute(line)
        if (name == null) {
            return null
        }

        reader.nextLine()

        // FIXME: value of multiple lines

        AttributeEntry attr = new AttributeEntry([ name: name, value: value ])

        return attr
    }

    /**
     * Parse a block header that is used to parse content further.
     * The id, attribute, title are read, but not the block start line.
     */
    protected BlockHeader parseBlockHeader() {
        reader.skipBlankLines()

        BlockHeader header = new BlockHeader()

        def line = null
        while ((line = reader.peekLine()) != null) {
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
                header.attributes << attrs

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
                header.type = Node.Type.COMMENT
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
            def (listType, listMarker, markerLevel, listFirstLine) = isList(line)
            if (listType != null) {
                header.type = listType
                header.properties[BlockHeader.LIST_MARKER] = listMarker
                header.properties[BlockHeader.LIST_MARKER_LEVEL] = markerLevel
                header.properties[BlockHeader.LIST_FIRST_LINE] = listFirstLine

                break
            }

            // check delimited block

            // normal paragraph
            header.type = Node.Type.PARAGRAPH
            break
        }

        blockHeader = header

        return header
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
     *         the first line of the list item content
     */
    protected static List isList(String line) {
        if (line == null) {
            return [ null, null, -1, null ]
        }

        def m = LIST_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ null, null, -1, null ]
        }

        Node.Type type = null

        def markers = m[0][1]
        String firstLine = m[0][2]
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

        return [ type, marker, markerLevel, firstLine ]
    }

    /**
     * Whether a line is the list continuation
     */
    protected static boolean isListContinuation(String line) {
        return '+' == line
    }

    /**
     * Whether a node type represents a list
     */
    protected static boolean isList(Node.Type type) {
        return type == Node.Type.ORDERED_LIST || type == Node.Type.UNORDERED_LIST
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
    protected boolean isListItem(Block parent, String marker, int markerLevel) {
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
}
