package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.BlockMacro

class ImageBlockMacroFactory extends AbstractBlockFactory {
    ImageBlockMacroFactory() {
        name = 'image'
    }

    def newInstance(FactoryBuilderSupport builder, nodeName, value, Map attributes) {
        BlockMacro blockMacro = new BlockMacro()
        blockMacro.name = name
        if (value != null) {
            blockMacro.target = value
        }

        return blockMacro
    }
}
