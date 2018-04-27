package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Block extends Node {
    Block() {
        type = Node.Type.BLOCK
    }

    String title

    /**
     * Conditionally walk all this block recursively
     */
    void walk(Closure condition, Closure action,
              Closure pre = null, Closure post = null) {
        if (pre) {
            pre(this)
        }

        children.each { block ->
            if (condition(block)) {
                action(block)
            }

            if (block in Block) {
                block.walk(condition, action, pre, post)
            }
        }

        if (post) {
            post(this)
        }
    }
}
