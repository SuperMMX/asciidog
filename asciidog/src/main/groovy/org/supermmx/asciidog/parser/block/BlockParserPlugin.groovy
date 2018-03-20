package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.ast.Blank
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher
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
     * Whether to skip blank lines before the block
     */
    protected boolean isSkippingBlankLines = true

    /**
     * Parse the block based on current context
     */
    Block parse(ParserContext context) {
        // parse current block
        log.info("Start parsing...")

        def block = parseBlock(context)
        if (block == null) {
            return null
        }

        context.block = block;

        log.debug("Parsing children...")
        def parent = context.parent

        block.parent = parent
        block.document = context.document

        // parse children
        def lastParserId = null
        def lastCursor = null

        def childParserId = getNextChildParser(context)
        while (childParserId != null) {
            log.info("Child parser is ${childParserId}");

            lastParserId = childParserId
            lastCursor = context.reader.cursor.clone()

            def childBlock = null

            def childParser = PluginRegistry.instance.getPlugin(childParserId)
            if (childParser != null) {
                // save current context

                context.push()

                context.parent = block
                context.properties.putAll(context.childParserProps)
                context.childParserProps.clear()

                childBlock = childParser.parse(context);

                def parentParserProps = context.parentParserProps

                context.pop()

                if (parentParserProps != null) {
                    context.properties.putAll((Map)parentParserProps)
                }

                if (childBlock != null) {
                    block << childBlock
                }
            }

            // next child
            childParserId = getNextChildParser(context)

            // infinite loop detected when the parser is the same and the cursor doesn't move
            if (childParserId == lastParserId
                && context.reader.cursor == lastCursor) {
                log.error('Infinite loop detected, current parser: {}, cursor: {}',
                          childParserId, context.reader.cursor)
                childParserId = null

                // stop the parsing process
                context.stop = true;
            }

            if (context.stop) {
                break;
            }
        }

        return block;
    }

    /**
     * Parse the block based on current context
     */
    protected Block parseBlock(ParserContext context) {
        // checking
        def header = context.blockHeader
        def lexer = context.lexer

        def parent = context.parent
        log.trace('Parser: {}, Trying parse block type {} with header {}',
                  id, nodeType, header)

        if (header == null) {
            header = new BlockHeader()
        }

        // skip blank lines if necessary
        if (header?.type == null && isSkippingBlankLines) {
            lexer.skipBlanks()
        }

        // FIXME: this one is not really needed
        def isStart = checkStart(context, header, context.expected ?: false)
        log.debug('Parser: {}, check start = {}', id, isStart)

        if (!isStart) {
            return null
        }

        // create the block
        Block block = createBlock(context, parent, header)
        if (block == null) {
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

    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        return false
    }

    boolean checkStart(ParserContext context, BlockHeader header, boolean expected) {
        context.lexer.mark()

        def result = doCheckStart(context, header, expected)

        context.lexer.reset()

        return result
    }

    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        return false
    }

    /**
     * Create the block from current context, especially from current block header,
     * without parsing the child nodes
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
            context.childParsers = getChildParserInfos(context).collect { it }
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

        // saved lines in case the block header is not needed
        def lines = []
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

        def lexer = context.lexer

        log.debug('Parser: {}, Start parsing block header...', id)

        lexer.skipBlanks()

        header = new BlockHeader()

        def line = null
        def matcher = TokenMatcher.type(Token.Type.EOL)
        while (lexer.peek().type != Token.Type.EOF) {

            // TODO: parse with tokens
            lexer.mark()
            line = lexer.combineTo(matcher)

            if (line.length() == 0) {
                lexer.reset()
                break
            }

            log.debug('Parser: {}, line = {}', id, line)

            // check id
            def (anchorId, anchorRef) = isBlockAnchor(line)
            if (anchorId != null) {
                header.id = anchorId
                header.lines << line

                lexer.clearMark()

                continue
            }

            // check title
            def title = isBlockTitle(line)
            if (title != null) {
                header.title = title
                header.lines << line

                lexer.clearMark()

                continue
            }

            // check attributes
            def attrs = isBlockAttributes(line)
            if (attrs != null) {
                header.attributes.putAll(attrs)
                header.lines << line

                lexer.clearMark()

                continue
            }

            lexer.reset()

            SectionParser sectionParser = PluginRegistry.instance.getPlugin(SectionParser.ID)
            if (sectionParser.checkStart(context, header, false)) {
                header.parserId = sectionParser.id

                break
            }

            // go through all block parsers to determine what block it is
            for (BlockParserPlugin plugin: PluginRegistry.instance.getBlockParsers()) {
                log.debug('Parser: {}, Try parser wth id: {}', id, plugin.id)
                if (plugin.checkStart(context, header, false)) {
                    log.debug('Parser: {}, Parser {} matches', id, plugin.id)
                    header.parserId = plugin.id

                    break
                }
            }

            // paragraph as the final result if non matches above
            if (header.parserId == null) {
                header.type = Node.Type.PARAGRAPH
                header.parserId = ParagraphParser.ID
            }

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
