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
    Document parseString(String content) {
        ParserContext context = new ParserContext()

        context.reader = Reader.createFromString(content)

        return parseDocument(context)
    }

    Document parseFile(String filename) {
        ParserContext context = new ParserContext()

        context.reader = Reader.createFromFile(filename)

        return parseDocument(context)
    }

    Document parseDocument(ParserContext context) {
        Document doc = (Document)parse(context)
        doc.attrs = context.attributes

        return doc
    }

    static Block parse(ParserContext context) {
        def parserId = context.parserId

        // set the default parser
        if (parserId == null) {
            parserId = DocumentParser.ID
            context.parserId = parserId
        }

        // create document if the root parse is not document parser
        if (parserId != DocumentParser.ID
            && context.document == null) {
            context.document = new Document()
        }

        def parser = PluginRegistry.instance.getPlugin(parserId)
        if (parser == null) {
            userLog.error("Parser \"${parserId}\" specified in the context not found");
            return null;
        }

        return parser.parse(context);
    }

    static Node parseOld(ParserContext context) {
        Block rootBlock = null
        def parserId = context.parserId

        // set the default parser
        if (parserId == null) {
            parserId = DocumentParser.ID
            context.parserId = parserId
        }

        // create document if the root parse is not document parser
        if (parserId != DocumentParser.ID
            && context.document == null) {
            context.document = new Document()
        }

        def lastParserId = null
        def lastCursor = null

        // continous parsing
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

                def parentParserProps = context.parentParserProps

                context.pop()

                if (parentParserProps != null) {
                    context.properties.putAll((Map)parentParserProps)
                }
            }

            parserId = context.parserId
        }

        return rootBlock
    }

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

        log.debug 'Filling all gaps...'
        fillGap(topInfo, null)

        return resultInlines
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
