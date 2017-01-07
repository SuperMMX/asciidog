package org.supermmx.asciidog.reader

import groovy.transform.AutoClone

/**
 * The cursor that tracks the URI's line number
 */
@AutoClone
class Cursor {
    String uri
    int lineno
    int column

    Cursor() {
        lineno = 0
    }

    String toString() {
        return "${uri}:(${lineno}, ${column})"
    }
}
