package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.Utils
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.AdocList

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class SectionFactory extends AbstractBlockFactory {
    SectionFactory() {
        name = 'section'
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Section section = new Section()

        return section
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        def id = attributes['id']
        def title = attributes['title']
        if (id == null) {
            id = Utils.normalizeId(title)
            attributes['id'] = id
        }

        return true
    }

    @Override
    boolean accept(FactoryBuilderSupport builder, parent, child) {
        return child in Block
    }
}
