package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * An inline container that contains multiple inline nodes.
 */
interface InlineContainer {
    List<Inline> getInlineNodes()
}
