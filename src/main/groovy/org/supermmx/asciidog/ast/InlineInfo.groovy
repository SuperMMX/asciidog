package org.supermmx.asciidog.ast

/**
 * An inline container that contains multiple inline nodes.
 */
trait InlineInfo {
    boolean constrained

    boolean escaped
    int start
    int end
    int contentStart
    int contentEnd
}
