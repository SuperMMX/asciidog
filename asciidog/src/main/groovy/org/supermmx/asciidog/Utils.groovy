package org.supermmx.asciidog

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Node

class Utils {
    /**
     * Gerneate id for the node
     */
    public static void generateId(Node node) {
        switch (node.type) {
        case Node.Type.SECTION:
            node.id = Utils.normalizeId(node.title)
            break
        default:
            break
        }
    }

    public static String normalizeId(String id) {
        StringBuilder sb = new StringBuilder()

        def first = true
        id.each { ch ->
            if (first) {
                if (!inRangeList(ch as char, Parser.ID_START_CHARS)) {
                    // prefix with '_'
                    sb.append('_')
                }
                first = false
            }

            if (!inRangeList(ch as char, Parser.ID_CHARS)) {
                ch = '_'
            }

            sb.append(ch)
        }

        return sb.toString()
    }

    private static boolean inRangeList(char ch, List<IntRange> rangeList) {
        boolean res = false
        for (Range range : rangeList) {
            res = range.contains((int)ch)
            if (res) {
                break
            }
        }

        return res
    }
}
