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
            node.id = Utils.normalizeId("_${node.title}")
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
                    ch = '_'
                }
            } else {
                if (!inRangeList(ch as char, Parser.ID_CHARS)) {
                    ch = '_'
                }
            }

            sb.append(ch)
        }

        return sb.toString()
    }

    private static boolean inRangeList(char ch, List<Range> rangeList) {
        boolean res = false
        for (Range range : rangeList) {
            if (range.contains(ch)) {
                res = true
                break
            }
        }

        return res
    }
}
