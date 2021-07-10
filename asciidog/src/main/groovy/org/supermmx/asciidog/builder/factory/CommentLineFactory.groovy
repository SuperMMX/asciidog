package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.CommentLine
import org.supermmx.asciidog.ast.DataNode

class CommentLineFactory extends AbstractBlockFactory {
    CommentLineFactory() {
        name = 'commentLine'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        CommentLine commentLine = new CommentLine()

        DataNode dataNode = new DataNode(value)

        commentLine << dataNode

        return commentLine
    }

    @Override
    boolean accept(FactoryBuilderSupport builder, parent, child) {
        return false
    }
}
