package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * A paragraph block that only contains inline nodes
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
@Slf4j
class Paragraph extends Block implements InlineContainer  {
    Paragraph() {
        type = Node.Type.PARAGRAPH
    }
}
