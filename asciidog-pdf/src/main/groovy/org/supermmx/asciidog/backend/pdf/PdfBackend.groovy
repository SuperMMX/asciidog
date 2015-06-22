package org.supermmx.asciidog.backend.pdf

import com.craigburke.document.core.Heading
import com.craigburke.document.core.Text
import com.craigburke.document.core.Document as DBDocument
import com.craigburke.document.builder.PdfDocumentBuilder

import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.CrossReferenceNode
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.converter.DocumentContext

/**
 * PDF Backend
 */
class PdfBackend extends AbstractBackend {
    PdfBackend() {
        id = 'pdf'
        ext = '.pdf'
    }
}
