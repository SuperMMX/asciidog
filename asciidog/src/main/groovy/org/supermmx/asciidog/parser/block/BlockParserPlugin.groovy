package org.supermmx.asciidog.parser.block

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
    /**
     * Check weather the line is the start of the block,
     * if yes, some necessary information is saved in the header.
     */
    abstract boolean isStart(String line, BlockHeader header)

    abstract Block parse(ParserContext context)

    /**
     * This block parser should determine whether to end the current child
     * paragraph parsing or not.
     */
    protected boolean toEndParagraph(ParserContext context, String line) {
        return false
    }

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
     * Parse a block header that is used to parse content further.
     * The id, attribute, title are read, but not the block start line.
     * These will determine what type the next block is.
     */
    protected static BlockHeader parseBlockHeader(ParserContext context) {
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
                if (plugin.isStart(line, header)) {
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

            // check the action blocks
            context.parent.blocks.addAll(header.actionBlocks)

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
            blocks.addAll(0, header.actionBlocks)
        }
    }

}
