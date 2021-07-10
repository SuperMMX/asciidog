package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * A data node that may not be rendered
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class DataNode extends Inline {
    String data

    DataNode() {
        type = Node.Type.DATA
        escaped = false
    }

    DataNode(String data) {
        this()

        this.data = data
    }

    @Override
    void asText(StringBuilder buf) {
        buf.append(data)
    }
}
