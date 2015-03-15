package org.supermmx.asciidog.ast

/**
 * An inline container that contains multiple inline nodes.
 */
trait InlineInfo {
    int start
    int end
    int contentStart
    int contentEnd
}
