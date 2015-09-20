package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.Attribute
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.builder.AsciiDocBuilder
import org.supermmx.asciidog.converter.DocumentWalker
import org.supermmx.asciidog.converter.DocumentContext

import groovy.xml.*

import spock.lang.*

class Html5Spec extends Specification {
    @Shared
    def builder = new AsciiDocBuilder()

    @Shared
    def backend = new Html5Backend()

    @Shared
    def walker = new DocumentWalker()

    /**
     * Create xml slurper from HTML string
     */
    def html(String content) {
        def html = new XmlSlurper(false, false, true).parseText(content)

        return html
    }

    /**
     * Create xml slurper from AsciiDoc, and return the html string
     * of the element returned from the closure
     */
    def adocHtml(Document doc, Closure closure) {
        def baos = new ByteArrayOutputStream()
        def context = new DocumentContext(document: doc, backend: backend)
        context.outputStream = baos
        context.attrContainer.setSystemAttribute(Document.OUTPUT_STREAM,
                                                 'true')

        walker.traverse(doc, backend, context)

        baos.close()

        def htmlText = baos.toString('UTF-8')

        def html = html(htmlText)

        return XmlUtil.serialize(closure(html))
    }

    /**
     * Create HTML string from markup
     */
    def markupHtml(Closure closure) {
        return XmlUtil.serialize(new StreamingMarkupBuilder().bind(closure))
    }
}
