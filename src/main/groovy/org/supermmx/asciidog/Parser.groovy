package org.supermmx.asciidog

import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section

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
  (\\S.*)        # 2, reference text
)?

\\]\\]          # ]] to end
'''
    static final def SECTION_PATTERN = ~'''(?x)
(={1,6})         # 1, section identifier
\\p{Blank}+
(                # 2, whole title
  \\S+
  (?:\\p{Blank}+\\S+)*
)
\\s*
'''

    // Reader
    Reader reader

    // latest attributes
    AttributeContainer attrContainer = new AttributeContainer()

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
        doc.type = Document.Type.valueOf(doctypeAttr.value)

        // get type
        def type = doc.type

        // preamble, as a section content
        def preambleBlocks = parseBlocks(doc)
        preambleBlocks.each { doc << it }

        // sections
        int startingLevel = 1
        if (doc.type == Document.Type.book) {
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
        reader.skipBlankLines()

        // check whether the next line is a section
        int level = -1
        String title = null
        (level, title) = isSection(reader.peekLine())

        if (level != expectedLevel) {
            // wrong section level
            return null
        }

        reader.nextLine()

        // current section
        Section section = new Section()
        section.parent = parent
        section.document = parent.document
        section.level = level
        section.title = title

        // blocks in the section
        def blocks = parseBlocks(section)
        blocks.each { section << it }

        // parse sub sections
        def line = null
        while ((line = reader.peekLine()) != null) {
            def subSection = parseSection(section, level + 1);
            if (subSection == null) {
                break
            } else {
                section << subSection
            }
        }

        return section
    }

    /**
     * Parse blocks
     *
     * @return a list of blocks
     */
    protected List<Block> parseBlocks(Block parent) {
        reader.skipBlankLines()

        def blocks = []

        def line = null
        while ((line = reader.peekLine()) != null) {
            def (level, title) = isSection(line)

            // section found
            if (level != -1) {
                break
            }

            def block = parseParagraph(parent)
            blocks << block

            reader.skipBlankLines()
        }

        return blocks
    }

    /**
     * Parse a paragraph
     *
     * @param parent the parent block
     */
    protected Paragraph parseParagraph(Block parent) {
        reader.skipBlankLines()

        Paragraph para = null

        def line = reader.peekLine()
        while (line != null && line.length() > 0) {
            if (para == null) {
                para = new Paragraph()
                para.parent = parent
                para.document = parent.document
            }
            para.lines << line

            reader.nextLine()

            line = reader.peekLine()
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
     * Whether the line represents a section
     *
     * @param line the line to check
     *
     * @return the section level, -1 if not a section,
     *          the section title, null if not a section
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
     * Whether the line represents an attribute definition
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
     * Whether a line represents a block anchor
     *
     * @param line the line to check
     *
     * @return the anchor id, null if not a block anchor
     *         the reference text, null if not a block anchor
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
}
