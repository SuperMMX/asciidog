package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * The list contains multiple list items with the same level.
 */
@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)

abstract class AdocList extends Block {
    /**
     * the leading spaces
     */
    String lead
    String marker
    int markerLevel
    int level
}
