package org.supermmx.asciidog.ast

class Document extends Block {
    static enum Type {
        article,
        book,
        inline
    }

    static final String DOCTYPE = 'doctype'
    static final String TOC = 'toc'

    Type type
    Header header

}
