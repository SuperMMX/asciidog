package org.supermmx.asciidog.ast;

class Document extends Block {
    static enum Type {
        article,
        book,
        inline
    }

    Type type
    Header header
}
