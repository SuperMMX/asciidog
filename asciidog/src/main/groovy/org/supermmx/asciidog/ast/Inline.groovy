package org.supermmx.asciidog.ast

import java.util.regex.Matcher

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * Base inline with inline information
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
abstract class Inline extends Node implements InlineContainer {
    boolean escaped

    void asText(StringBuilder buf) {
        children.each { inline.asText(bu) }
    }
}
