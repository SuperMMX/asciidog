package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical(excludes=['parent', 'document'])
@EqualsAndHashCode(excludes=['parent', 'document'])
@ToString(excludes=['parent', 'document'], includePackage=false, includeNames=true)

class Node {
    static enum Type {
        COMMENT,
        DELIMITED_BLOCK,
        DOCUMENT,
        LIST_ITEM,
        ORDERED_LIST,
        PARAGRAPH,
        SECTION,
        TABLE,
        UNORDERED_LIST,
    }

    Type type
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
