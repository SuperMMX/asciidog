package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.ast.Blank
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.ParserPlugin
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.transform.Canonical
import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * Block parser plugin that parses a specific type of block
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
abstract class BlockParserPlugin extends ParserPlugin {
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

    /**
     * Whether to skip blank lines before the block
     */
    protected boolean isSkippingBlankLines = true

    /**
     * Parse the block based on current context
     */
    Block parse(ParserContext context) {
        // checking
        def header = context.blockHeader
        def reader = context.reader

        def parent = context.parent
        log.trace('Parser: {}, Trying parse block type {} with header {}',
                  id, nodeType, header)

        if (header == null) {
            header = new BlockHeader()
        }

        // skip blank lines if necessary
        if (header?.type == null && isSkippingBlankLines) {
            reader.skipBlankLines()
        }

        def line = reader.peekLine()
        log.debug('Parser: {}, line = {}', id, line)
        def isStart = checkStart(line, header, context.expected ?: false)
        log.debug('Parser: {}, check start = {}', id, isStart)

        if (!isStart) {
            return null
        }

        log.debug('Parser: {}, Parsing block type {}, parent type {}, parent seq {}',
                  id, nodeType, parent?.type, parent?.seq)

        // create the block
        Block block = createBlock(context, parent, header)
        if (block ==  null) {
            return null
        }

        log.debug('Parser: {}, keepHeader = {}', id, context.keepHeader)
        if (!context.keepHeader) {
            context.blockHeader = null
        }

        log.debug('Parser: {}, Parsing block {}, parent type {}, parent seq {}...Done',
                  id, nodeType, parent?.type, parent?.seq)

        return block
    }

    /**
     * Check weather the line is the start of the block,
     * if yes, some necessary information is saved in the header.
     *
     * @param line the next line
     * @param header the new block header to fill
     * @param expected whether the type of the block of this plugin parses
     *        is expected by the parent parser, or the parent parser just
     *        does the wild guess
     */
    boolean checkStart(String line, BlockHeader header, boolean expected) {
        return doCheckStart(line, header, expected)
    }

    abstract protected boolean doCheckStart(String line, BlockHeader header, boolean expected)

    /**
     * Create the block from current context, especially from current block header
     */
    protected Block createBlock(ParserContext context, Block parent, BlockHeader header) {
        return doCreateBlock(context, parent, header)
    }

    abstract protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header)

    protected List<ChildParserInfo> getChildParserInfos(ParserContext context) {
        return doGetChildParserInfos(context)
    }

    protected List<ChildParserInfo> doGetChildParserInfos(ParserContext context) {
        return []
    }

    /**
     * Get the next child parser
     */
    String getNextChildParser(ParserContext context) {
        // copy expected child parsers
        if (context.childParsers == null) {
            context.childParsers = getChildParserInfos().collect { it }
        }

        String childParser = null

        def parent = context.block

        // check known parsers first
        boolean found = false
        boolean remove = false

        while (context.childParsers.size() > 0 && !found) {
            def childParserInfo = context.childParsers.head()

            log.debug('Trying pre-configured child parser {}', childParserInfo)

            def childCount = parent?.children?.size() ?: 0

            switch (childParserInfo.type) {
            case ParserInfoType.ONE:
            case ParserInfoType.ZERO_OR_ONE:
                // try only once
                found = true
                remove = true
                break
            case ParserInfoType.ZERO_OR_MORE:
            case ParserInfoType.ONE_OR_MORE:
            case ParserInfoType.FIND:
                // try at least once
                if (context.parserCallingCount == null) {
                    // first to use this parser
                    found = true

                    context.parserCallingCount = 1
                    context.parserStartIndex = childCount
                } else if (context.parserStartIndex + context.parserCallingCount == childCount) {
                    // a block is parsed last time, keep trying with this parser
                    found = true
                    context.parserCallingCount ++
                } else {
                    // no block is parsed last time, try next parser

                    // TODO: check error
                    remove = true
                }
                break
            }

            log.debug('Child parser: found: {}, remove: {}', found, remove)

            if (found) {
                if (childParserInfo.findHeader) {
                    nextBlockHeader(context)
                    log.debug('Block header found: {}', context.blockHeader)
                }

                context.childParserProps.expected = childParserInfo.expected

                childParser = childParserInfo.parserId
                if (childParserInfo.type == ParserInfoType.FIND) {
                    childParser = context.blockHeader?.parserId
                }

                // do any actions or checkings before parsing with the parser
                if (childParserInfo.action) {
                    log.debug('Execute parser action...')
                    def result = childParserInfo.action.call(context, parent)
                    log.debug('Parser action result = {}', result)

                    if (!result) {
                        // to end the parser here
                        childParser = null
                        remove = true
                    }
                }
            }

            if (remove) {
                log.debug('Parser remove from configured parser list: {}', childParserInfo)
                context.childParsers.remove(0)

                context.parserCallingCount = null
                context.parserStartIndex = null
            }
        }

        log.debug('Configured child parser: {}', childParser)

        // get dynamic child parser from code
        if (childParser == null) {
            log.debug('Running custom code to get next child parser...')
            childParser = doGetNextChildParser(context, context.block)
        }

        log.debug('Next child parser is {}', childParser)

        return childParser
    }

    protected String doGetNextChildParser(ParserContext context, Block block) {
        return null
    }

    /**
     * This block parser should determine whether to end the current child
     * paragraph parsing or not.
     */
    protected boolean toEndParagraph(ParserContext context, String line) {
        return doToEndParagraph(context, line)
    }

    protected boolean doToEndParagraph(ParserContext context, String line) {
        return false
    }

    /**
     * internal class
     */
    @Canonical
    protected static class BlockHeader {
        static final String COMMENT_LINE_COMMENT = 'comment'

        Node.Type type
        String parserId

        def id
        def title
        // block attributes
        def attributes = [:] as LinkedHashMap<String, String>

        // other properties
        def properties = [:]

        def actionBlocks = []

        /**
         * Set by a block parse to determine whether to stop trying
         * even a block start has been found.  The default value is true,
         * meaning if a block is found, stop trying for next one.
         */
        boolean stop = true

        String toString() {
            return """Block Header:
  Parser ID: ${parserId}
  Type: ${type}, ID: ${id}, Title: ${title}
  Attributes: ${attributes}
  Properties: ${properties}
  Action Blocks: ${actionBlocks}"""
        }
    }

    /**
     * Utility to get next block header that is used to parse content further.
     * The id, attribute, title are read, but not the block start line.
     * These will determine what type the next block is.
     *
     * The blockHeader in context is returned if it is not null, otherwise
     * try to find next one.
     *
     * @param context the parser context
     *
     * @return the block header and save into the context
     */
    protected BlockHeader nextBlockHeader(ParserContext context) {
        def header = context.blockHeader
        if (header != null) {
            return header
        }

        def reader = context.reader

        log.debug('Parser: {}, Start parsing block header...', id)

        reader.skipBlankLines()

        header = new BlockHeader()

        def line = null
        while ((line = reader.peekLine()) != null) {
            if (line.length() == 0) {
                break
            }

            log.debug('Parser: {}, line = {}', id, line)

            // check id
            def (anchorId, anchorRef) = isBlockAnchor(line)
            if (anchorId != null) {
                header.id = anchorId

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

            // check attributes
            def attrs = isBlockAttributes(line)
            if (attrs != null) {
                header.attributes.putAll(attrs)

                reader.nextLine()

                continue
            }

            SectionParser sectionParser = PluginRegistry.instance.getPlugin(SectionParser.ID)
            if (sectionParser.checkStart(line, header, false)) {
                header.parserId = sectionParser.id

                break
            }

            // go through all block parsers to determine what block it is
            for (BlockParserPlugin plugin: PluginRegistry.instance.getBlockParsers()) {
                log.debug('Parser: {}, Try parser wth id: {}', id, plugin.id)
                if (plugin.checkStart(line, header, false)) {
                    log.debug('Parser: {}, Parser {} matches', id, plugin.id)
                    header.parserId = plugin.id

                    break
                }
            }

            // paragraph as the final result if non matches above
            header.type = Node.Type.PARAGRAPH
            header.parserId = ParagraphParser.ID
            break
        }

        log.debug('{}', header)
        log.debug('Parser: {}, End parsing block header', id)

        if (header.type == null) {
            // no block is found

            /*
              def actionBlocks = header.actionBlocks
              if (actionBlocks.size() > 0) {
              // check the action blocks
              def blank = new Blank()
              blank.blocks.addAll(actionBlocks)

              context.parent << blank
              }
            */

            header = null
        }

        context.blockHeader = header

        return header
    }

    /**
     * Fill common block headers for the current block
     */
    protected static void fillBlockFromHeader(Block block, BlockHeader header) {
        if (header == null) {
            return
        }

        log.debug('Fill headers for block: {}', block.type)
        block.with {
            id = header.id
            title = header.title
            attributes = header.attributes

            //blocks.addAll(0, header.actionBlocks)
        }
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

    static enum ParserInfoType {
        ONE,
        ZERO_OR_ONE,
        ZERO_OR_MORE,
        ONE_OR_MORE,
        // to find next parser by looking at the header
        FIND,
    }
    /**
     * Child parser information for the current parser
     */
    @Canonical
    static class ChildParserInfo {
        ParserInfoType type = ParserInfoType.ONE
        String parserId
        boolean expected = true
        boolean findHeader = false
        Closure action

        ChildParserInfo expected() {
            expect = true

            return this
        }

        ChildParserInfo notExpected() {
            expect = false

            return this
        }

        ChildParserInfo findHeader() {
            findHeader = true

            return this
        }

        ChildParserInfo notFindHeader() {
            findHeader = false

            return this
        }

        ChildParserInfo doBeforeParsing(Closure action) {
            this.action = action

            return this
        }
        static ChildParserInfo one(String parserId) {
            return new ChildParserInfo(type: ParserInfoType.ONE,
                                       parserId: parserId)
        }

        static ChildParserInfo zeroOrOne(String parserId) {
            return new ChildParserInfo(type: ParserInfoType.ZERO_OR_ONE,
                                       parserId: parserId)
        }

        static ChildParserInfo zeroOrMore(String parserId) {
            return new ChildParserInfo(type: ParserInfoType.ZERO_OR_MORE,
                                       parserId: parserId)
        }

        static ChildParserInfo oneOrMore(String parserId) {
            return new ChildParserInfo(type: ParserInfoType.ONE_OR_MORE,
                                       parserId: parserId)
        }

        static ChildParserInfo find() {
            return new ChildParserInfo(type: ParserInfoType.FIND,
                                       parserId: null,
                                       expected: false, findHeader: true)
        }
    }
}
