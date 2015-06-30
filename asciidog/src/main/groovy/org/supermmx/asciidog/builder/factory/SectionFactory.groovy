package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph

class SectionFactory extends AbstractBlockFactory {
    SectionFactory() {
        childClasses = [
            Section,
            Paragraph
        ]
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Section section = new Section()
        
        return section
    }
}
