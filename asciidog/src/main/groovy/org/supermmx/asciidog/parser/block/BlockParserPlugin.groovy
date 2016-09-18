package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.ast.Blank
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.ParserPlugin
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * Block parser plugin that parses a specific type of block
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
abstract class BlockParserPlugin extends ParserPlugin {
    protected boolean isSkippingBlankLines = true

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

    /**
     * Parse the block based on current context
     */
    Block parse(ParserContext context) {
        // checking
        def header = context.blockHeader
        def parent = context.parent
        def reader = context.reader

        log.trace('Trying parse block type {} with header {}',
                  nodeType, header)

        // skip blank lines if necessary
        if (isSkippingBlankLines) {
            reader.skipBlankLines()
        }

        def isStart = false

        if (header == null) {
            header = new BlockHeader()
            def line = reader.peekLine()

            isStart = checkStart(line, header, true)
        } else {
            isStart = (header.type == nodeType)
        }

        if (!isStart) {
            return null
        }

        log.debug('Parsing block type {}, parent type {}, parent seq {}',
                  nodeType, parent?.type, parent?.seq)

        // create the block
        Block block = createBlock(context, parent, header)
        if (parent != null) {
            block.parent = parent
            block.document = parent.document
        }

        context.blockHeader = null

        context.parents.push(block)
        context.parentParsers.push(this)

        // parsing the child
        def children = parseChildren(context, block)
        block.children.addAll(children)

        context.parents.pop()
        context.parentParsers.pop()

        log.debug('Parsing block {}, parent type {}, parent seq {}...Done',
                  nodeType, parent?.type, parent?.seq)

        return block
    }

    /**
     * Create the block from current context, especially from current block header
     */
    protected Block createBlock(ParserContext context, Block parent, BlockHeader header) {
        return doCreateBlock(context, parent, header)
    }

    protected List<Node> parseChildren(ParserContext context, Block parent) {
        return doParseChildren(context, parent);
    }

    /**
     * Check whether the expected block from the block header is valid
     * for current block
     */
    protected boolean isValidChild(ParserContext context, Block parent, BlockHeader header) {
        return doIsValidChild(context, parent, header);
    }

    /**
     * This block parser should determine whether to end the current child
     * paragraph parsing or not.
     */
    protected boolean toEndParagraph(ParserContext context, String line) {
        return doToEndParagraph(context, line)
    }

    abstract protected boolean doCheckStart(String line, BlockHeader header, boolean expected)

    abstract protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header)

    abstract protected List<Node> doParseChildren(ParserContext context, Block parent)

    protected boolean doIsValidChild(ParserContext context, Block parent, BlockHeader header) {
        return true
    }

    protected boolean doToEndParagraph(ParserContext context, String line) {
        return false
    }

    /**
     * internal class
     */
    protected static class BlockHeader {
        static final String LIST_LEAD = 'listLead'
        static final String LIST_MARKER = 'listMarker'
        static final String LIST_MARKER_LEVEL = 'listMarkerLevel'
        static final String LIST_CONTENT_START = 'listContentStart'

        static final String COMMENT_LINE_COMMENT = 'comment'

        Node.Type type
        BlockParserPlugin parserPlugin

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
     */
    protected BlockHeader nextBlockHeader(ParserContext context) {
        def reader = context.reader

        log.debug('Start parsing block header...')

        reader.skipBlankLines()

        BlockHeader header = new BlockHeader()

        def line = null
        while ((line = reader.peekLine()) != null) {
            if (line.length() == 0) {
                break
            }

            // go through all block parsers to determine what block it is
            for (BlockParserPlugin plugin: PluginRegistry.instance.getBlockParserPlugins()) {
                log.info('=== plugin Id: {}', plugin)
                if (plugin.checkStart(line, header, false)) {
                    break
                }
            }

            if (header.stop) {
                break
            }
        }

        log.debug('{}', header)
        log.debug('End parsing block header')

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
     * Utility to get a list of blocks
     */
    protected List<Block> parseBlocks(ParserContext context, Block parent) {
        log.debug('Parsing blocks for parent type: {}...', parent.type)

        def blocks = []

        def stop = false
        while (true) {
            def header = context.blockHeader
            if (header == null) {
                header = nextBlockHeader(context)
            }

            if (header == null) {
                break
            }

            if (!checkIsValidChild(context, block, header)) {
                break
            }

            def block = header.parserPlugin.parse(context)
            blocks << block
        }

        log.debug('Parsing blocks for parent type: {}...Done', parent.type)

        return blocks
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

}
