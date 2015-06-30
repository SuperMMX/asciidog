package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer

class ParagraphFactory extends InlineContainerFactory {
    ParagraphFactory() {
        childClasses = [
            Inline,
            InlineContainer
        ]
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Paragraph paragraph = new Paragraph()
        
        return paragraph
    }
}
