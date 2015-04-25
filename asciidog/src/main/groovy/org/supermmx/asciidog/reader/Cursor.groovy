package org.supermmx.asciidog.reader

/**
 * The cursor that tracks the URI's line number
 */
class Cursor {
    String uri
    int lineno

    Cursor() {
        lineno = 0
    }

    String toString() {
        return "${uri}:${lineno}"
    }
}
