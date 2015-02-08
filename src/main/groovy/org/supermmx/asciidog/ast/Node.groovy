package org.supermmx.asciidog.ast

class Node {
    static enum Type {
        DOCUMENT,
        SECTION,
        ORDERED_LIST,
        UNORDERED_LIST,
        DELIMITED_BLOCK,
        TABLE,
        PARAGRAPH,
    }

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
