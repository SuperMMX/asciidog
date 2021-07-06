package org.supermmx.asciidog.ast

import org.supermmx.asciidog.AttributeContainer

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true, excludes=['references', 'attrs'])
@TupleConstructor
class Document extends Block {
    static enum DocType {
        article,
        book,
        inline
    }

    static final String DOCTYPE = 'doctype'
    static final String TOC = 'toc'
    static final String PLUS = 'plus'

    /**
     * Writing mode for output, same as CSS writing mode
     */
    static final String OUTPUT_WRITING_MODE = 'output-writing-mode';
    /**
     * The supported writing mode
     */
    static enum WritingMode {
        htb,
        vrl,
        vlr
    }

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

    // only avaialble when the document is completed parsed
    AttributeContainer attrs

    //DocType docType = DocType.article

    // references in this document
    Map<String, Node> references = [:]

    /**
     * Local resources referenced in the document
     */
    List<Resource> resources = []

    Document() {
        type = Node.Type.DOCUMENT

        // excluded fields in toString
        excludes = ['header', 'preamble', 'references', 'attrs']
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
