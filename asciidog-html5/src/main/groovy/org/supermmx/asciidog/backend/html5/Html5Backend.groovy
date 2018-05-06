package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.backend.AbstractTemplateBackend
import org.supermmx.asciidog.backend.NullNodeRenderer
import org.supermmx.asciidog.backend.TemplateManager
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
class Html5Backend extends AbstractTemplateBackend {
    /**
     * The option for the directory to store all HTML files
     */
    static final String HTML_DIR = 'html-dir'
    /**
     * The option for the directory to store stylesheets
     */
    static final String CSS_DIR = 'css-dir'

    static final String HTML5_BACKEND_ID = 'html5'
    static final String HTML5_EXT = '.html'

    @Override
    protected void initialize() {
        id = HTML5_BACKEND_ID
        ext = HTML5_EXT

        templateExt = ext
    }

    /**
     * Get the chunk path for html files from the attributes
     */
    @Override
    String getChunkPath(DocumentContext context) {
        def htmlDir = context.attrContainer[HTML_DIR]

        return htmlDir
    }
}
