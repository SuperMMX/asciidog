package org.supermmx.asciidog.critic.renderer

import org.supermmx.asciidog.critic.CriticNode
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext
import org.supermmx.asciidog.backend.AbstractNodeRenderer

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
class CriticHtml5Renderer extends AbstractNodeRenderer {
    CriticHtml5Renderer() {
        nodeType = CriticNode.CRITIC_NODE_TYPE
        backendId = 'html5'
    }

    void doPre(DocumentContext context, Node criticNode) {
        def tag = null

        switch (criticNode.criticType) {
        case CriticNode.CriticType.ADDITION:
            tag = 'ins'
            break
        case CriticNode.CriticType.DELETION:
            tag = 'del'
            break
        case CriticNode.CriticType.COMMENT:
            tag = 'span'
            break
        case CriticNode.CriticType.HIGHLIGHT:
            tag = 'mark'
            break
        case CriticNode.CriticType.SUBSTITUTION:
            // child nodes of delete and add
            break
        }

        if (tag != null && tag != '') {
            context.push()

            context.tag = tag
            context.writer.writeStartElement(tag)

            if (criticNode.criticType == CriticNode.CriticType.COMMENT) {
                context.writer.writeAttribute('class', 'critic comment')
            }
        }

    }

    void doPost(DocumentContext context, Node criticNode) {
        def tag = context.tag

        if (tag != null && tag != '') {
            context.writer.writeEndElement()

            context.pop()
        }
    }
}
