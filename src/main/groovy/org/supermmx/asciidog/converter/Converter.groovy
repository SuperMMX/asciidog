package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Document

import groovy.text.markup.MarkupTemplateEngine
import groovy.text.markup.TemplateConfiguration

/**
 * Converter control engine
 */
class Converter {
    // Attribute names

    static String DOCTYPE = 'doctype'
    static String TOC = 'toc'

    void convert(Document doc) {
        def writer = new StringWriter()

        convertToHtml(doc, writer)

        println writer.toString()
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

    /**
     * Get the value type of the known attribute with the specified name
     *
     * @param name the attribute name
     *
     * @return the attribute type, ValueType.STRING if the attribute
     *         is not a system attribute
     */
    static AttributeEntry.ValueType getAttributeType(String name) {
        def type = AttributeEntry.ValueType.STRING

        AttributeEntry attr = DEFAULT_ATTRIBUTES[name]
        if (attr != null) {
            type = attr.type
        }

        return type
    }

    static AttributeEntry getDefaultAttribute(String name) {
        return DEFAULT_ATTRIBUTES[name]
    }

    // name, type, default value
    private static def DEFAULT_ATTR_DEFS = [
        [ DOCTYPE, AttributeEntry.ValueType.STRING, Document.Type.article.toString() ],
        [ TOC, AttributeEntry.ValueType.STRING, 'auto' ],
    ]

    private static def DEFAULT_ATTRIBUTES = [:]

    static {
        DEFAULT_ATTR_DEFS.each {
            def name = it[0]
            def type = it[1]
            def value = it[2]
            DEFAULT_ATTRIBUTES[name] = new AttributeEntry([ name: name, type: type, value: value ])
        }
    }
}
