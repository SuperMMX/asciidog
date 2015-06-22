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

        documentRenderer = new Html5DocumentRenderer()
        headerRenderer = new Html5HeaderRenderer()
        preambleRenderer = new NullNodeRenderer()
        sectionRenderer = new Html5SectionRenderer()
        paragraphRenderer = new Html5ParagraphRenderer()
        listRenderer = new Html5ListRenderer()
        listItemRenderer = new Html5ListItemRenderer()

        inlineTextRenderer = new Html5InlineTextRenderer()
        inlineFormattingRenderer = new Html5InlineFormattingRenderer()
        inlineXrefRenderer = new Html5InlineXrefRenderer()
    }
}
