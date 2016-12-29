package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Node

class Html5EmphasisRenderer extends AbstractHtml5FormattingRenderer {
    Html5EmphasisRenderer() {
        nodeType = Node.Type.EMPHASIS
        tag = 'em'
    }
}
