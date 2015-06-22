package org.supermmx.asciidog.converter

import org.supermmx.asciidog.AttributeContainer
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.backend.Backend

class DocumentContext {
    AttributeContainer attrContainer = new AttributeContainer()

    Document document
    Backend backend
    OutputStream outputStream

    Map<String, Object> properties = [:]

    def propertyMissing(String name, value) {
        properties[(name)] = value
    }

    def propertyMissing(String name) {
        return properties[(name)]
    }
}
