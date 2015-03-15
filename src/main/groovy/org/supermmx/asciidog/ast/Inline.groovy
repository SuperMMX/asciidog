package org.supermmx.asciidog.ast

import java.util.regex.Matcher

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)

abstract class Inline extends Node implements InlineInfo {
    static enum InlineBaseType {
        STRONG,
        DOUBLE_QUOTE,
        SINGLE_QUOTE,
        MONOSPACED,
        EMPHASIS,
        SUPERSCRIPT,
        SUBSCRIPT
    }

    static enum InlineType{
        STRONG_UNCONSTRAINED(InlineBaseType.STRONG, false),
        STRONG_CONSTRAINED(InlineBaseType.STRONG, true),
        DOUBLE_QUOTE(InlineBaseType.DOUBLE_QUOTE, true),
        SINGLE_QUOTE(InlineBaseType.SINGLE_QUOTE, true),
        MONOSPACED_UNCONSTRAINED(InlineBaseType.MONOSPACED, false),
        MONOSPACED_CONSTRAINED(InlineBaseType.MONOSPACED, true),
        EMPHASIS_UNCONSTRAINED(InlineBaseType.EMPHASIS, false),
        EMPHASIS_CONSTRAINED(InlineBaseType.EMPHASIS, true),
        SUPERSCRIPT(InlineBaseType.SUPERSCRIPT, false),
        SUBSCRIPT(InlineBaseType.SUBSCRIPT, false)

        InlineBaseType baseType
        boolean constrained
        Closure fillDataClosure

        InlineType(InlineBaseType baseType, boolean contrained) {
            this.baseType = baseType
            this.constrained = constrained
        }

        def parse(Matcher m, List groups) {
            def info = new Expando()
            info.type = this
            info.start = m.start()
            info.end = m.end()

            info.escape = (groups[1] != '')

            info.contentStart = m.start(2)
            info.contentEnd = m.end(2)
            info.content = groups[2]

            return info
        }
    }
}
