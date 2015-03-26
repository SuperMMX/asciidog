package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Information about the inline node
 */
@Canonical
@EqualsAndHashCode
@ToString(includePackage=false, includeNames=true)
class InlineInfo {
    boolean constrained

    boolean escaped
    int start
    int end
    int contentStart
    int contentEnd
}
