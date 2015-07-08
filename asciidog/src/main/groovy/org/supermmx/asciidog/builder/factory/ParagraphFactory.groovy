package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Paragraph

class ParagraphFactory extends InlineContainerFactory {
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Paragraph paragraph = new Paragraph()
        
        return paragraph
    }
}
