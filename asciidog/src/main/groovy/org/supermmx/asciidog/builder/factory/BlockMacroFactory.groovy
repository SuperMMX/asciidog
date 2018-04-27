package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.BlockMacro

class BlockMacroFactory extends AbstractBlockFactory {
    BlockMacroFactory() {
        name = 'blockMacro'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        BlockMacro blockMacro = new BlockMacro()

        return blockMacro
    }
}
