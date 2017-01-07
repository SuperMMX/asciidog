package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.backend.NullNodeRenderer
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.CrossReferenceNode
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.converter.DocumentContext

import groovy.util.logging.Slf4j

import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import java.lang.reflect.Proxy

@Slf4j
class Html5Backend extends AbstractBackend {
    Html5Backend() {
        id = 'html5'
        ext = '.html'

        //registerRenderer(Node.Type.DOCUMENT, new Html5DocumentRenderer())
        registerRenderer(Node.Type.HEADER, new Html5HeaderRenderer())
        registerRenderer(Node.Type.PREAMBLE, new NullNodeRenderer())
        registerRenderer(Node.Type.SECTION, new Html5SectionRenderer())
        registerRenderer(Node.Type.PARAGRAPH, new Html5ParagraphRenderer())
        registerRenderer(Node.Type.LIST, new Html5ListRenderer())
        registerRenderer(Node.Type.LIST_ITEM, new Html5ListItemRenderer())

        registerRenderer(Node.Type.TEXT, new Html5InlineTextRenderer())
        registerRenderer(Node.Type.STRONG, new Html5StrongRenderer())
        registerRenderer(Node.Type.EMPHASIS, new Html5EmphasisRenderer())
        registerRenderer(Node.Type.CROSS_REFERENCE, new Html5InlineXrefRenderer())

        chunkRenderer = new Html5ChunkRenderer()
    }
}
