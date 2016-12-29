package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Node

class Html5StrongRenderer extends AbstractHtml5FormattingRenderer {
    Html5StrongRenderer() {
        nodeType = Node.Type.STRONG
        tag = 'strong'
    }
}
