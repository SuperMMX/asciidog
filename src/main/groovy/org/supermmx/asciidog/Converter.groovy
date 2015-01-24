package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Document

import groovy.text.markup.MarkupTemplateEngine
import groovy.text.markup.TemplateConfiguration


class Converter {
    void convert(Document doc, String file) {
        convertToHtml(doc)
    }

    void convertToHtml(Document doc) {
        def config = new TemplateConfiguration()
        config.with {
            autoNewLine = true
            autoIndent = true
            autoIndentString = '  '
            autoEscape = true
        }

        def engine = new MarkupTemplateEngine(getClass().getClassLoader(), config)
        def template = engine.createTemplateByPath('org/supermmx/asciidog/html5.groovy')
        def model = [:]
        model['doc'] = doc
        def output = template.make(model)
        def writer = new StringWriter()
        output.writeTo(writer)

        println writer.toString()
    }
}
