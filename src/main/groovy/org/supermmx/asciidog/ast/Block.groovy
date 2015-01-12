package org.supermmx.asciidog.ast

class Block extends Node {
    String title
    List<String> lines = []
    List<Block> blocks = []

    Block leftShift(Block block) {
        blocks << block

        return this
    }
}
