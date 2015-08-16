package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Document

import spock.lang.*

class DocumentContextSpec extends Specification {
    def context

    def setup() {
        context = new DocumentContext()
    }

    def 'built-in properties'() {
        expect:
        context.attrContainer != null
        context.document == null
        context.backend == null
    }

    def 'dynamic properties'() {
        when:
        context.newProperty = 'new value'

        then:
        context.newProperty == 'new value'
    }

    def 'remove dynmic property'() {
        given:
        context.newProperty = 'new value'

        when:
        context.remove('newProperty')

        then:
        context.newProperty == null
    }

    def 'push and pop dynamic properties'() {
        given:
        context.document = new Document()
        context.newProperty = 'value'

        when:
        context.push()
        context.newProperty = 'new value'
        context.integer = 10

        then:
        context.document != null
        context.newProperty == 'new value'
        context.integer == 10

        when:
        context.pop()

        then:
        context.document != null
        context.newProperty == 'value'
        context.integer == null
    }
}
