package org.supermmx.asciidog.backend.pdf

import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.backend.Renderer

/**
 * PDF Backend
 */
class PdfBackend extends AbstractBackend {
    PdfBackend() {
        id = 'pdf'
    }

    Renderer createRenderer(Map<String, Object> options) {
        return new PdfRenderer(options)
    }
}
