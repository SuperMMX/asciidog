package org.supermmx.asciidog.ast

class Document extends Block {
    static enum DocType {
        article,
        book,
        inline
    }

    static final String DOCTYPE = 'doctype'
    static final String TOC = 'toc'

    DocType docType
    Header header

    Document() {
        type = Node.Type.DOCUMENT
    }
}
