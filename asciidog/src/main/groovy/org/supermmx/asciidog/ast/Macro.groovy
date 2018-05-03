package org.supermmx.asciidog.ast

import org.supermmx.asciidog.Subtype

/**
 * Macro trait for both Block Macro and Inline Macro
 */
trait Macro implements Subtype {
    String name
    String target

    @Override
    String getSubtype() {
        return name
    }
}
