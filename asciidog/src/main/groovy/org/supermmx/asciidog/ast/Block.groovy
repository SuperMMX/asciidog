package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)

class Block extends Node {
    Block() {
        type = Node.Type.BLOCK
    }

    String title
    List<String> lines = []
    List<Block> blocks = []

    Block leftShift(Block block) {
        blocks << block

        return this
    }

    /**
     * Conditionally walk all this block recursively
     */
    void walk(Closure condition, Closure action,
              Closure pre = null, Closure post = null) {
        if (pre) {
            pre(this)
        }

        blocks.each { block ->
            if (condition(block)) {
                action(block)
            }

            block.walk(condition, action, pre, post)
        }

        if (post) {
            post(this)
        }
    }
}
