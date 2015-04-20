package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Document

/**
 * A renderer renders the document
 */
interface Renderer {
    /**
     * Render the document and output to the stream
     */
    void renderDocument(Document doc, OutputStream os)
}
