package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.CommentLine

class CommentFactory extends AbstractBlockFactory {
    CommentFactory() {
        name = 'comment'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        // TODO: comment block
        // TODO: text node ?
        CommentLine comment = new CommentLine(lines: [ value ])

        return comment
    }
}
