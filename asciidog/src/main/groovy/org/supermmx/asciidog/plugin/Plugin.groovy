package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.Node

abstract class Plugin {
    static enum Type {
        PARSER,
        RENDERER,
        BUILDER
    }

    String id
    Type type
    Node.Type nodeType

    boolean isParserPlugin() {
        type == Type.PARSER
    }

    boolean isRendererPlugin() {
        type == Type.RENDERER
    }
}
