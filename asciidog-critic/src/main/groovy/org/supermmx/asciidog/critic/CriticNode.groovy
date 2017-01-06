package org.supermmx.asciidog.critic

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class CriticNode extends Inline implements InlineContainer {
    static final Node.Type CRITIC_NODE_TYPE =
        new Node.Type(parent: Node.Type.INLINE, name: 'critic')

    static enum CriticType {
        ADDITION,
        DELETION,
        COMMENT,
        HIGHLIGHT,
        SUBSTITUTION
    }

    CriticType criticType

    CriticNode() {
        type = CRITIC_NODE_TYPE
    }
}
