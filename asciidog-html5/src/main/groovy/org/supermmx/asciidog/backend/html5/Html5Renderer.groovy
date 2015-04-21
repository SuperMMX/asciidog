package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.backend.Renderer

class Html5Renderer implements Renderer {
    Html5Renderer(Map<String, Object> options) {
    }

    void renderDocument(Document doc, OutputStream os) {
        def config = new TemplateConfiguration()
        config.with {
            autoNewLine = true
            autoIndent = true
            autoIndentString = '  '
            autoEscape = true
        }

        def engine = new MarkupTemplateEngine(getClass().getClassLoader(), config)
        def template = engine.createTemplateByPath('org/supermmx/asciidog/backend/html5/html5.groovy')
        def model = [:]
        model['doc'] = doc
        def output = template.make(model)

        output.writeTo(new OutputStreamWriter(os))
    }
}

