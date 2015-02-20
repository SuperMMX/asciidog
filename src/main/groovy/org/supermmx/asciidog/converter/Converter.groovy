package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Document

import groovy.text.markup.MarkupTemplateEngine
import groovy.text.markup.TemplateConfiguration
import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * Converter control engine
 */
@Slf4j
class Converter {
    // Attribute names

    static String DOCTYPE = 'doctype'
    static String TOC = 'toc'

    void convert(Document doc) {
        log.info('Converting document and output to console...')

        def writer = new StringWriter()

        convertToHtml(doc, writer)

        println writer.toString()
    }

    void convertToHtmlFile(Document doc, String file) {
        log.info('Converting document to HTML5 file {}...', file)
        Writer writer = new BufferedWriter(new FileWriter(file))

        convertToHtml(doc, writer)

        writer.close()
    }

    void convertToHtml(Document doc, Writer writer) {
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
        output.writeTo(writer)
    }
}
