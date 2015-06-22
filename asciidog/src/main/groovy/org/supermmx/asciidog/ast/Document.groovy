package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)

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
    Preamble preamble

    // references in this document
    Map<String, Node> references = [:]

    Document() {
        type = Node.Type.DOCUMENT
    }
}
