package org.supermmx.asciidog.ast

/**
 * Local resources referenced in the document
 */
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
