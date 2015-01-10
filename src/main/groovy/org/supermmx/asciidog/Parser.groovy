package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header

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
    static final def SECTION_PATTERN = ~'''(?x)
(={1,6})         # 1, section identifier
\\p{Blank}+
(                # 2, whole title
  \\S+
  (?:\\p{Blank}+\\S+)*
)
\\s*
'''

    Reader reader

    Document parseString(String content) {
        reader = Reader.createFromString(content)

        return parseDocument()
    }

    Document parseFile(String filename) {
        null
    }

    protected Document parseDocument() {
        Document doc = new Document()

        if (reader.nextLine() == null) {
            return doc
        }

        doc.header = parseHeader()

        return doc
    }

    protected Header parseHeader() {
        Header header = null

        def line = reader.line

        if (line == null) {
            return null
        }

        def m = SECTION_PATTERN.matcher(line)

        if (!m.matches()
            || m[0][1].length() != 1) {
            // doesn't have a header
            return null
        }

        header = new Header()
        header.title = m[0][2]

        // parse author
        reader.nextLine()
        header.authors = parseAuthors()

        // parse revision
        // parse attributes

        return header
    }

    protected List<Author> parseAuthors() {
        def line = reader.line

        if (line == null) {
            return null
        }

        if (!AUTHOR_LINE_PATTERN.matcher(line).matches()) {
            return null
        }

        def authors = [] as List<Author>
        line.split(";").each {
            authors << createAuthor(it)
        }
        return authors
    }

    protected static Author createAuthor(String line) {
        // should always match from parser
        def m = Parser.AUTHOR_PATTERN.matcher(line)
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

            author = ([ firstname, middlename, lastname ] - null).join(' ')
        }

        return newAuthor
    }
}
