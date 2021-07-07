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


        static final ABSTRACT_BLOCK = new Type(parent: NODE, name: 'abstract_block', isAbstract: true)
        static final STRUCTURE_BLOCK = new Type(parent: ABSTRACT_BLOCK, name: 'structure_block', isAbstract: true)
        static final Type CHUNK = new Type(parent: STRUCTURE_BLOCK, name: 'CHUNK')
        static final BLOCK = new Type(parent: ABSTRACT_BLOCK, name: 'block', isAbstract: true)
        static final ACTION = new Type(parent: BLOCK, name: 'action', isAbstract: true, isAction: true)
        static final INLINE = new Type(parent: NODE, name: 'inline', isAbstract: true)

        static final DOCUMENT = new Type(parent: STRUCTURE_BLOCK, name: 'document')
        static final HEADER = new Type(parent: STRUCTURE_BLOCK, name: 'header')
        static final PREAMBLE = new Type(parent: STRUCTURE_BLOCK, name: 'preamble')
        static final REVISION = new Type(parent: STRUCTURE_BLOCK, name: 'revision')
        static final SECTION = new Type(parent: STRUCTURE_BLOCK, name: 'section')

        static final AUTHORS = new Type(parent: BLOCK, name: 'authors')
        static final BLANK = new Type(parent: BLOCK, name: 'blank')
        static final BLOCK_MACRO = new Type(parent: BLOCK, name: 'block_macro')
        static final COMMENT_LINE = new Type(parent: BLOCK, name: 'comment_line')
        static final LIST = new Type(parent: BLOCK, name: 'list', isAbstract: true)
        static final LIST_ITEM = new Type(parent: BLOCK, name: 'list_item')
        static final PARAGRAPH = new Type(parent: BLOCK, name: 'paragraph')
        static final TABLE = new Type(parent: BLOCK, name: 'table')

        static final FORMATTING = new Type(parent: INLINE, name: 'formatting', isAbstract: true)
        static final STRONG = new Type(parent: FORMATTING, name: 'strong')
        static final EMPHASIS = new Type(parent: FORMATTING, name: 'emphasis')
        static final MARK = new Type(parent: FORMATTING, name: 'mark')

        static final ATTRIBUTE_REFERENCE = new Type(parent: INLINE, name: 'attribute_reference')
        static final AUTHOR = new Type(parent: INLINE, name: 'author')
        static final CROSS_REFERENCE = new Type(parent: INLINE, name: 'xref')
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

        static boolean isCase(Type switchType, Type caseType) {
            def type = switchType
            while (type != null) {
                if (type == caseType) {
                    break
                }

                type = type.parent
            }
            return (type != null)
        }

        boolean isType(Type type) {
            return isCase(this, type)
        }

        boolean isBlock() {
            return isCase(this, BLOCK)
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

    List<Node> find(Node.Type type) {
        return find { node ->
            node.type.isType(type)
        }
    }

    List<Node> find(Closure closure) {
        def result = []

        find(result, closure)

        return result
    }

    protected void find(List<Node> result, Closure closure) {
        if (closure.call(this)) {
            result << this
        }

        children.each { child ->
            child.find(result, closure)
        }
    }

    /**
     * Excluded toString() fields
     */
    protected List<String> excludes = []

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
            def mapStr = value.collect { mapKey, mapValue ->
                "${mapKey}: ${mapValue}"
            }.join(',\n')

            sb << '['
            if (mapStr) {
                sb << '\n'
                indent(sb, mapStr)
            }
            sb << ']'
        } else if (Iterable.class.isAssignableFrom(cls)
                   || Iterator.class.isAssignableFrom(cls)) {

            def listStr = value.collect { listValue ->
                "${listValue}"
            }.join(',\n')

            sb << '['
            if (listStr) {
                sb << '\n'
                indent(sb, listStr)
            }
            sb << ']'
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
