package org.supermmx.asciidog.ast

class Node {
    String id
    Map<String, AttributeEntry> attributes = [:]
    Node parent

    Document document

    void setAttribute(AttributeEntry attr) {
        attributes[attr.name] = attr
    }

    void unsetAttribute(AttributeEntry attr) {
        attributes.remove(attr.name)
    }
}
