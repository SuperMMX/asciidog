package org.supermmx.asciidog.ast;

import org.supermmx.asciidog.ast.Authors
import org.supermmx.asciidog.Parser

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Authors extends Block implements InlineContainer {
    Authors() {
        type = Node.Type.AUTHORS
    }
}
