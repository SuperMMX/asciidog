package org.supermmx.asciidog.ast

class Resource {
    static enum Type {
        IMAGE
    }

    /**
     * The resource type
     */
    Type type

    /**
     * The resource relative to the input file
     */
    String path

    /**
     * The corresponding node
     */
    Node node
}
