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

    boolean contains(InlineInfo info) {
        return contains(contentStart, contentEnd, info.start, info.end)
    }

    boolean contains(int start, int end) {
        return contains(contentStart, contentEnd, start, end)
    }

    boolean belongsTo(InlineInfo info) {
        return belongsTo(start, end, info.contentStart, info.contentEnd)
    }

    boolean belongsTo(int start, int end) {
        return belongsTo(this.start, this.end, start, end)
    }

    boolean overlaps(InlineInfo info) {
        return overlaps(start, end, info.start, info.end)
    }

    boolean overlaps(int start, int end) {
        return overlaps(this.start, this.end, start, end)
    }

    static boolean contains(int s1, int e1, int s2, int e2) {
        return (s2 >= s1 && e2 <= e1)
    }

    static boolean belongsTo(int s1, int e1, int s2, int e2) {
        return (s1 >= s2 && e1 <= e2)
    }

    static boolean overlaps(int s1, int e1, int s2, int e2) {
        return (s1 >= s2 && s1 <= e2) && (e1 >= e2) ||
        (s1 <= s2) && (e1 >= s2 & e1 <= e2)
    }
}
