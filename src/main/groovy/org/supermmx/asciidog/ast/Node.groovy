package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical(excludes=['parent', 'document'])
@EqualsAndHashCode(excludes=['parent', 'document'])
@ToString(excludes=['parent', 'document'], includePackage=false, includeNames=true)

class Node {
    static enum Type {
        COMMENT_LINE,
        DELIMITED_BLOCK,
        DOCUMENT,
        INLINE_ATTRIBUTE_REFERENCE,
        INLINE_FORMATTED_TEXT,
        INLINE_MACRO,
        INLINE_REPLACEMENT,
        INLINE_TEXT,
        LIST_ITEM,
        ORDERED_LIST,
        PARAGRAPH,
        SECTION,
        TABLE,
        UNORDERED_LIST

        boolean isInline() {
            return name().startsWith('INLINE_')
        }

        boolean isList() {
            return name().endsWith('_LIST')
        }
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
