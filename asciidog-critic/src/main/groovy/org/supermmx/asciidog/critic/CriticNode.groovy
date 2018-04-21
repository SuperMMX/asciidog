package org.supermmx.asciidog.critic

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class CriticNode extends Inline implements InlineContainer {
    static final String TAG_ADDITION = '++'
    static final String TAG_DELETION = '--'
    static final String TAG_SUBSTITUTION = '~~'
    static final String TAG_COMMENT_START = '>>'
    static final String TAG_COMMENT_END = '<<'
    static final String TAG_HIGHLIGHT = '=='

    static final Node.Type CRITIC_NODE_TYPE =
        new Node.Type(parent: Node.Type.INLINE, name: 'critic')

    static enum CriticType {
        ADDITION(TAG_ADDITION),
        DELETION(TAG_DELETION),
        COMMENT(TAG_COMMENT_START, TAG_COMMENT_END),
        HIGHLIGHT(TAG_HIGHLIGHT),
        SUBSTITUTION(TAG_SUBSTITUTION)

        String startTag
        String endTag

        CriticType(String startTag, String endTag = null) {
            this.startTag = startTag
            this.endTag = endTag
            if (this.endTag == null) {
                this.endTag = this.startTag
            }
        }
    }

    CriticType criticType

    CriticNode() {
        type = CRITIC_NODE_TYPE
    }
}
