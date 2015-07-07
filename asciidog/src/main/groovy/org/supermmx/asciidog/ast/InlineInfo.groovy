package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Information about the inline node
 */
@Canonical(excludes=['inlineNode'])
@EqualsAndHashCode
@ToString(excludes=['inlineNode'], includePackage=false, includeNames=true)
class InlineInfo {
    boolean constrained

    boolean escaped
    int start
    int end
    int contentStart
    int contentEnd

    /**
     * Whether to fill the gap between nodes with TextNode
     */
    boolean fillGap = true

    Node inlineNode

    List<InlineInfo> children = []

    InlineInfo leftShift(InlineInfo info) {
        children << info

        return this
    }
}
