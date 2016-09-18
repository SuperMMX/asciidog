package org.supermmx.asciidog.ast;

import org.supermmx.asciidog.Parser

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Authors extends Block {
    Authors() {
        type = Node.Type.AUTHORS
    }
}
