package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Authors
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.ParserContext

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class AuthorParser extends BlockParserPlugin {
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
    static final def HEADER_PROPERTY_AUTHOR_LINE = "authorLine"

    static final String ID = 'plugin:parser:block:author'

    AuthorParser() {
        nodeType = Node.Type.AUTHORS
        id = ID
    }

    @Override
    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        def isStart = false
        if (expected) {
            isStart = AUTHOR_LINE_PATTERN.matcher(line).matches()
            if (isStart) {
                header.properties[(HEADER_PROPERTY_AUTHOR_LINE)] = line
            }
        }

        return isStart
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def authors = new Authors(parent: parent,
                                  document: parent?.document)

        return authors
    }

    @Override
    protected boolean doIsValidChild(ParserContext context, Block block, BlockHeader header) {
        return false
    }

    @Override
    protected List<Node> doParseChildren(ParserContext context, Block parent) {
        def reader = context.reader

        def children = []

        def line = reader.nextLine()

        line.split(";").each {
            def author = createAuthor(it)
            author.parent = parent
            author.document = parent?.document

            children << author
        }

        return children
    }

    /**
     * Create an author from a string
     */
    protected static Author createAuthor(String authorText) {
        // should always match from parser
        def m = AUTHOR_PATTERN.matcher(authorText)
        if (!m.matches()) {
            return null
        }

        def groups = m[0]

        Author newAuthor = new Author()
        newAuthor.with {
            first = groups[1]
            if (groups[3] != null) {
                middle = groups[2]
                last = groups[3]
            } else {
                last = groups[2]
            }
            email = groups[4]

            def names = [ first , middle, last ] - null

            initials = names.collect{ it[0] }.join('')
        }

        return newAuthor
    }

}
