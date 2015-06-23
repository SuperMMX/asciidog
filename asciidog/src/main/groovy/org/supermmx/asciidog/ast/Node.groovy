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
    static class Type {
        static final NODE = new Type(name: 'node')

        static final BLOCK = new Type(parent: NODE, name: 'block')
        static final INLINE = new Type(parent: NODE, name: 'inline')

        static final DOCUMENT = new Type(parent: BLOCK, name: 'document')
        static final HEADER = new Type(parent: BLOCK, name: 'header')
        static final REVISION = new Type(parent: BLOCK, name: 'revision')
        static final PREAMBLE = new Type(parent: BLOCK, name: 'preamble')
        static final SECTION = new Type(parent: BLOCK, name: 'section')
        static final LIST = new Type(parent: BLOCK, name: 'list')
        static final LIST_ITEM = new Type(parent: BLOCK, name: 'list_item')
        static final PARAGRAPH = new Type(parent: BLOCK, name: 'paragraph')
        static final TABLE = new Type(parent: BLOCK, name: 'table')
        static final MACRO = new Type(parent: BLOCK, name: 'macro')
        static final COMMENT_LINE = new Type(parent: BLOCK, name: 'comment_line')
        
        static final ATTRIBUTE_REFERENCE = new Type(parent: INLINE, inline: true, name: 'attribute_reference')
        static final CROSS_REFERENCE = new Type(parent: INLINE, inline: true, name: 'xref')
        static final FORMATTING = new Type(parent: INLINE, inline: true, name: 'formatting')
        static final INLINE_MACRO = new Type(parent: INLINE, inline: true, name: 'inline_macro')
        static final REPLACEMENT = new Type(parent: INLINE, inline: true, name: 'replacement')
        static final TEXT = new Type(parent: INLINE, inline: true, name: 'text')

        static final ORDERED_LIST = new Type(parent: LIST, name: 'olist')
        static final UNORDERED_LIST = new Type(parent: LIST, name: 'ulist')

        static final DEFINE_ATTRIBUTE = new Type(parent: NODE, action: true, name: 'define_attribute')
        static final SET_ATTRIBUTE = new Type(parent: INLINE, action: true, name: 'set_attribute')
        static final SET_COUNTER = new Type(parent: INLINE, action: true, name: 'set_counter')

        /**
         * Whether the node type is for inline
         */
        boolean inline = false
        /**
         * Whether the node type is to do some action
         */
        boolean action = false
        /**
         * Parent type
         */
        Type parent
        /**
         * The node type name
         */
        String name

        static boolean isCase(Type caseValue, Type switchValue) {
            return (caseValue.name == switchValue.name)
        }

        boolean isList() {
            return (parent == LIST)
        }
    }

    Type type
    String id
    Map<String, String> attributes = [:]
    Node parent

    Document document

    Node() {
        type = Node.Type.NODE
    }
}
