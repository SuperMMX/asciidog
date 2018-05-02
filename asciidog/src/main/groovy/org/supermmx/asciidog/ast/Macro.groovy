package org.supermmx.asciidog.ast

import org.supermmx.asciidog.Subtype

trait Macro implements Subtype {
    String name
    String target

    @Override
    String getSubtype() {
        return name
    }
}
