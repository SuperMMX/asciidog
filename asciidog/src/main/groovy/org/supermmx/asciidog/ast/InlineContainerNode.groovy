package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 */
@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
class InlineContainerNode extends Inline implements InlineContainer {
    List<Inline> inlineNodes = []

    InlineContainer leftShift(Inline inline) {
        inlineNodes << inline

        return this
    }
}
