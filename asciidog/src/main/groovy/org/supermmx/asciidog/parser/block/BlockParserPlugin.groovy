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

    /**
     * Get the next child parser
     */
    String getNextChildParser(ParserContext context) {
        return doGetNextChildParser(context, context.block)
    }

    abstract protected String doGetNextChildParser(ParserContext context, Block block)

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

}
