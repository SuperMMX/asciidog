package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true, excludes=['references', 'header', 'preamble'])
@TupleConstructor
class Document extends Block {
    static enum DocType {
        article,
        book,
        inline
    }

    static final String DOCTYPE = 'doctype'
    static final String TOC = 'toc'

    /**
     * Base name for the output
     */
    static final String OUTPUT_BASE = 'output-base'

    /**
     * Whether the output should be chunked, default false
     */
    static final String OUTPUT_CHUNKED = 'output-chunked'
    /**
     * The default chunking level
     */
    static final String OUTPUT_CHUNKING_LEVEL = 'output-chunking-level'
    /**
     * Whehter the output is to stream, default false
     */
    static final String OUTPUT_STREAM = 'output-stream'

    /**
     * Default chunking section level, chapter level by default
     */
    static final int DEFAULT_CHUNKING_LEVEL = 1

    DocType docType

    // references in this document
    Map<String, Node> references = [:]

    Document() {
        type = Node.Type.DOCUMENT

        // excluded fields in toString
        excludes = ['header', 'preamble', 'references']
    }

    Header getHeader() {
        // only the first one
        if (children.size() == 0) {
            return null
        }

        Header header = null
        Block block = children[0]
        if (block in Header) {
            header = block
        }

        return header
    }

    Preamble getPreamble() {
        Preamble preamble = null

        int index = (getHeader() == null) ? 0 : 1
        if (index < children.size()) {
            Block block = children[index]
            if (block in Preamble) {
                preamble = block
            }
        }

        return preamble
    }
}
