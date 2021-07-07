package org.supermmx.asciidog.ast

import groovy.transform.ToString

/**
 * Local resources referenced in the document
 */
@ToString(includeSuper=false, includePackage=false, includeNames=true, excludes=['node'])
class Resource {
    static enum Type {
        IMAGE,
        STYLESHEET
    }

    static enum Source {
        FILE_SYSTEM,
        CLASSPATH
    }

    Source source = Source.FILE_SYSTEM

    /**
     * The resource type
     */
    Type type

    /**
     * The resource relative to the input file if the source is FILE_SYSTEM,
     * or start from / if the source is CLASSPATH
     */
    String path

    /**
     * The destination path relative to the output base directory,
     * null means the same as path
     */
    String destPath

    /**
     * The corresponding node
     */
    Node node

    String getDestPath() {
        return destPath ?: path
    }
}
