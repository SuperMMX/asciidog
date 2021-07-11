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

    @Override
    String toString() {
        StringBuilder buf = new StringBuilder()
        def withSpace = false

        if (first != null) {
            buf.append(first)
            withSpace = true
        }

        if (middle != null) {
            buf.append(withSpace ? ' ': '').append(middle)
            withSpace = true
        }

        if (last != null) {
            buf.append(withSpace ? ' ': '').append(last)
            withSpace = true
        }

        if (email != null) {
            buf.append(withSpace ? ' ': '')
                .append('<')
                .append(email)
                .append('>')
        }

        return buf.toString()
    }
}
