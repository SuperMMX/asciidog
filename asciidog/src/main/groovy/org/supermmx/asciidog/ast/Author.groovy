package org.supermmx.asciidog.ast;

import org.supermmx.asciidog.Parser

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Author extends Inline {
    Author() {
        type = Node.Type.AUTHOR
    }

    String first
    String middle
    String last
    String initials
    String email
}
