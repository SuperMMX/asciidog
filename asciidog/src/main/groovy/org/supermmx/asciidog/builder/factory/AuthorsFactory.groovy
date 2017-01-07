package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Authors
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Node

class AuthorsFactory extends AbstractBlockFactory {
    AuthorsFactory() {
        name = Node.Type.AUTHORS.name
        childClasses = [ Author ]
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        def authors = new Authors()

        return authors
    }
}
