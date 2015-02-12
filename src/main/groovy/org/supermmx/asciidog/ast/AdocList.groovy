package org.supermmx.asciidog.ast

/**
 * The list contains multiple list items with the same level.
 */
abstract class AdocList extends Block {
    String marker
    int markerLevel
    int level
}
