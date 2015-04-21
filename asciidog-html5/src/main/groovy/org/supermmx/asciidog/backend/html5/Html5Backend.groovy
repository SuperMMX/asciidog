package org.supermmx.asciidog.backend.html5

class Html5Backend extends AbstractBackend {
    Html5Backend() {
        id = 'html5'
    }

    Renderer createRenderer(Map<String, Object> options) {
        return new Html5Renderer(options)
    }
}
