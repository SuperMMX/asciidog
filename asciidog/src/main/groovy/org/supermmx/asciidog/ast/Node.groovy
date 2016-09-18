package org.supermmx.asciidog.ast

import groovy.json.StreamingJsonBuilder
import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import groovy.util.logging.Slf4j

import java.lang.reflect.Modifier
import java.util.concurrent.atomic.AtomicLong

import org.slf4j.Logger

@EqualsAndHashCode(excludes=['parent', 'document', 'seq', 'excludes'])
@TupleConstructor
@Slf4j
class Node {
    // here the node type should be basic ones
    static class Type {
        static final NODE = new Type(name: 'node', isAbstract: true)

        static final BLOCK = new Type(parent: NODE, name: 'block', isAbstract: true)
        static final INLINE = new Type(parent: NODE, name: 'inline', isAbstract: true)
        static final ACTION = new Type(parent: BLOCK, name: 'action', isAbstract: true, isAction: true)
        static final LIST = new Type(parent: BLOCK, name: 'list', isAbstract: true)

        static final AUTHORS = new Type(parent: BLOCK, name: 'authors')
        static final BLANK = new Type(parent: BLOCK, name: 'blank')
        static final COMMENT_LINE = new Type(parent: BLOCK, name: 'comment_line')
        static final DOCUMENT = new Type(parent: BLOCK, name: 'document')
        static final HEADER = new Type(parent: BLOCK, name: 'header')
        static final LIST_ITEM = new Type(parent: BLOCK, name: 'list_item')
        static final MACRO = new Type(parent: BLOCK, name: 'macro')
        static final PARAGRAPH = new Type(parent: BLOCK, name: 'paragraph')
        static final PREAMBLE = new Type(parent: BLOCK, name: 'preamble')
        static final REVISION = new Type(parent: BLOCK, name: 'revision')
        static final SECTION = new Type(parent: BLOCK, name: 'section')
        static final TABLE = new Type(parent: BLOCK, name: 'table')

        static final ATTRIBUTE_REFERENCE = new Type(parent: INLINE, name: 'attribute_reference')
        static final AUTHOR = new Type(parent: INLINE, name: 'author')
        static final CROSS_REFERENCE = new Type(parent: INLINE, name: 'xref')
        static final FORMATTING = new Type(parent: INLINE, name: 'formatting')
        static final INLINE_MACRO = new Type(parent: INLINE, name: 'inline_macro')
        static final NULL = new Type(parent: INLINE, name: 'null')
        static final REPLACEMENT = new Type(parent: INLINE, name: 'replacement')
        static final TEXT = new Type(parent: INLINE, name: 'text')

        static final ORDERED_LIST = new Type(parent: LIST, name: 'olist')
        static final UNORDERED_LIST = new Type(parent: LIST, name: 'ulist')

        static final DEFINE_ATTRIBUTE = new Type(parent: ACTION, isAction: true, name: 'define_attribute')
        static final SET_ATTRIBUTE = new Type(parent: INLINE, isAction: true, name: 'set_attribute')
        static final SET_COUNTER = new Type(parent: INLINE, isAction: true, name: 'set_counter')

        /**
         * Whether the node with this type is abstract
         */
        boolean isAbstract = false
        /**
         * Whether the node with this type is to do some action
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
     * The child nodes
     */
    List<Node> children = []

    /**
     * The document that contains this node
     */
    Document document

    Node() {
        type = Node.Type.NODE
        seq = INDEX.getAndIncrement()
    }

    Node leftShift(Node node) {
        children << node

        return this
    }

    /**
     * Excluded toString() fields
     */
    protected String[] excludes = []

    @Override
    String toString() {
        def props = this.metaClass.properties
        .sort { it.name }
        .findAll {
            !['class', 'active', 'document', 'parent', 'seq', 'excludes', 'type'].contains(it.name) &&
            !Modifier.isStatic(it.modifiers)
        }.collectEntries {
            [(it.name): it]
        }

        def commonProps = props.subMap([ 'id', 'title', 'attributes'])
        def childrenProp = props.remove('children')

        props = props - commonProps

        StringBuilder sb = new StringBuilder()

        // common properties in order
        commonProps.each { key, prop ->
            def value = prop.getProperty(this)
            sb << "${key} "

            writeObject(sb, value)

            sb << "\n"
        }

        // other properties sorted
        props.findAll {
            !excludes.contains(it.key)
        }.each { key, prop ->
            def value = prop.getProperty(this)
            sb << "${key} "

            writeObject(sb, value)

            sb << "\n"
        }

        // children
        sb << "children "
        writeObject(sb, childrenProp.getProperty(this))
        sb << "\n"

        def temp = sb.toString()

        sb = new StringBuilder()

        def clsName = this.getClass().getSimpleName()
        sb << clsName << ' {\n'

        indent(sb, temp)

        sb << '}'

        return sb.toString()
    }

    /**
     * Write an object to the buffer
     */
    protected void writeObject(StringBuilder sb, Object value) {
        def cls = value.getClass()
        if (CharSequence.class.isAssignableFrom(cls)) {
            sb << "'${value}'"
        } else if (Map.class.isAssignableFrom(cls)) {
            sb << "[\n"
            def mapStr = value.collect { mapKey, mapValue ->
                "${mapKey}: ${mapValue}"
            }.join(',\n')

            indent(sb, mapStr)

            sb << "]"
        } else if (Iterable.class.isAssignableFrom(cls)
                   || Iterator.class.isAssignableFrom(cls)) {
            sb << "[\n"
            def listStr = value.collect { listValue ->
                "${listValue}"
            }.join(',\n')

            indent(sb, listStr)

            sb << "]"
        } else {
            sb << "${value}"
        }
    }

    /**
     * Indent the content and append to the buffer
     */
    protected void indent(StringBuilder sb, String content) {
        content.eachLine { line ->
            sb << '  ' << line << '\n'
        }
    }
}
