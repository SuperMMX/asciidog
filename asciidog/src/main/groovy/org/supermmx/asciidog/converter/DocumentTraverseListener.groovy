package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.Inline

interface DocumentTraverseListener {
    void enterDocument(DocumentContext context, Document document)

    void exitDocument(DocumentContext context, Document document)

    void enterDocumentHeader(DocumentContext context, Header header)
    void exitDocumentHeader(DocumentContext context, Header header)

    void enterPreamble(DocumentContext context, Preamble preamble)
    void exitPreamble(DocumentContext context, Preamble preamble)

    void enterSection(DocumentContext context, Section section)
    void exitSection(DocumentContext context, Section section)

    void enterList(DocumentContext context, AdocList list)
    void exitList(DocumentContext context, AdocList list)

    void enterParagraph(DocumentContext context, Paragraph para)
    void exitParagraph(DocumentContext context, Paragraph para)

    void enterInline(DocumentContext context, Inline inline)
    void exitInline(DocumentContext context, Inline inline)

    /*
    void enter(DocumentContext context, )
    void exit(DocumentContext context, )
    */
    
}
