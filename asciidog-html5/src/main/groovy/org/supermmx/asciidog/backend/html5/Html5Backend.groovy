package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.backend.AbstractTemplateBackend
import org.supermmx.asciidog.backend.TemplateManager
import org.supermmx.asciidog.converter.DocumentContext

import groovy.util.logging.Slf4j

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
