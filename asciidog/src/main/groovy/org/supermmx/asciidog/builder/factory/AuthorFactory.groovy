package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.block.AuthorParser

class AuthorFactory extends AbstractInlineFactory {
    AuthorFactory() {
        name = Node.Type.AUTHOR.name
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        def author = AuthorParser.createAuthor(value)

        return author
    }
}
