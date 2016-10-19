package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * An inline node with formatted text
 */
@EqualsAndHashCode(callSuper=true, excludes=[ 'constrained' ])
@TupleConstructor
abstract class FormattingNode extends Inline implements InlineContainer {
    boolean constrained = false

    FormattingNode() {
        excludes = ['constrained']
    }
}
