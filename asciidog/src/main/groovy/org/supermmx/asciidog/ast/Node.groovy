package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical(excludes=['parent', 'document'])
@EqualsAndHashCode(excludes=['parent', 'document'])
@ToString(excludes=['parent', 'document'], includePackage=false, includeNames=true)

class Node {
    // FIXME: how to handle nodes from plugin?
    // here the node type should be basic ones
    static enum Type {
        COMMENT_LINE,
        DELIMITED_BLOCK,
        DOCUMENT,
        DOCUMENT_ATTRIBUTE,
        DOCUMENT_HEADER,
        INLINE_ATTRIBUTE_REFERENCE,
        INLINE_CROSS_REFERENCE,
        INLINE_FORMATTED_TEXT,
        INLINE_MACRO,
        INLINE_REPLACEMENT,
        INLINE_TEXT,
        LIST,
        LIST_ITEM,
        ORDERED_LIST,
        PARAGRAPH,
        SECTION,
        SET_ATTRIBUTE,
        SET_COUNTER,
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
    Map<String, String> attributes = [:]
    Node parent

    Document document
}
