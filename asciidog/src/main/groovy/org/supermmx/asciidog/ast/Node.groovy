package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import groovy.util.logging.Slf4j

import java.util.concurrent.atomic.AtomicLong

import org.slf4j.Logger

@Canonical(excludes=['parent', 'document', 'seq'])
@EqualsAndHashCode(excludes=['parent', 'document', 'seq'])
@ToString(excludes=['parent', 'document', 'seq'], includePackage=false, includeNames=true)

@Slf4j
class Node {
    // here the node type should be basic ones
    static class Type {
        static final NODE = new Type(name: 'node', isAbstract: true)

        static final BLOCK = new Type(parent: NODE, name: 'block', isAbstract: true)
        static final INLINE = new Type(parent: NODE, name: 'inline', isAbstract: true)

        static final DOCUMENT = new Type(parent: BLOCK, name: 'document')
        static final HEADER = new Type(parent: BLOCK, name: 'header')
        static final AUTHORS = new Type(parent: BLOCK, name: 'authors')
        static final REVISION = new Type(parent: BLOCK, name: 'revision')
        static final PREAMBLE = new Type(parent: BLOCK, name: 'preamble')
        static final SECTION = new Type(parent: BLOCK, name: 'section')
        static final LIST = new Type(parent: BLOCK, name: 'list')
        static final LIST_ITEM = new Type(parent: BLOCK, name: 'list_item')
        static final PARAGRAPH = new Type(parent: BLOCK, name: 'paragraph')
        static final TABLE = new Type(parent: BLOCK, name: 'table')
        static final MACRO = new Type(parent: BLOCK, name: 'macro')
        static final COMMENT_LINE = new Type(parent: BLOCK, name: 'comment_line')

        static final AUTHOR = new Type(parent: INLINE, name: 'author')
        static final NULL = new Type(parent: INLINE, name: 'null')
        static final ATTRIBUTE_REFERENCE = new Type(parent: INLINE, name: 'attribute_reference')
        static final CROSS_REFERENCE = new Type(parent: INLINE, name: 'xref')
        static final FORMATTING = new Type(parent: INLINE, name: 'formatting')
        static final INLINE_MACRO = new Type(parent: INLINE, name: 'inline_macro')
        static final REPLACEMENT = new Type(parent: INLINE, name: 'replacement')
        static final TEXT = new Type(parent: INLINE, name: 'text')

        static final ORDERED_LIST = new Type(parent: LIST, name: 'olist')
        static final UNORDERED_LIST = new Type(parent: LIST, name: 'ulist')

        static final DEFINE_ATTRIBUTE = new Type(parent: NODE, isAction: true, name: 'define_attribute')
        static final SET_ATTRIBUTE = new Type(parent: INLINE, isAction: true, name: 'set_attribute')
        static final SET_COUNTER = new Type(parent: INLINE, isAction: true, name: 'set_counter')

        /**
         * Whether the node type is abstract
         */
        boolean isAbstract = false
        /**
         * Whether the node type is to do some action
         */
        boolean isAction = false
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

        boolean isInline() {
            boolean res = false

            Type p = parent
            while (p != null) {
                if (p == INLINE) {
                    res = true
                    break
                } else {
                    p = p.parent
                }
            }

            return res
        }

        boolean isList() {
            return (parent == LIST)
        }

        String toString() {
            return name
        }
    }

    static final String ATTRIBUTE_CHUNK_NAME = 'chunk-name'

    private static AtomicLong INDEX = new AtomicLong()

    /**
     * Unique squence number across all nodes
     */
    long seq

    /**
     * Node Type
     */
    Type type
    /**
     * Node ID specified by user or generated
     */
    String id
    /**
     * Node Attributes
     */
    Map<String, String> attributes = [:]
    /**
     * The parent node
     */
    Node parent

    /**
     * The document that contains this node
     */
    Document document

    Node() {
        type = Node.Type.NODE
        seq = INDEX.getAndIncrement()
    }
}
