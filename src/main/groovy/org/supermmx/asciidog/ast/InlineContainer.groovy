package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * An inline container that contains multiple inline nodes.
 */
@Canonical
@EqualsAndHashCode(callSuper=false)
@ToString(includeSuper=false, includePackage=false, includeNames=true)

trait InlineContainer extends InlineInfo {
    List<Inline> nodes = []

    InlineContainer leftShift(Inline inline) {
        nodes << inline

        return this
    }
}
