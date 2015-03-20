package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.Node

abstract class Plugin {
    static enum Type {
        PARSER,
        RENDERER
    }

    String id
    Type type
    Node.Type nodeType
}
