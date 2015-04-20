package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Document

/**
 * A backend is responsible for creating the renderer to render
 * the parsed document.
 */
interface Backend {
    /**
     * The backend id
     */
    String getId()

    /**
     * Create the renderer with the specified options
     */
    Renderer createRenderer(Map<String, Object> options)
}
