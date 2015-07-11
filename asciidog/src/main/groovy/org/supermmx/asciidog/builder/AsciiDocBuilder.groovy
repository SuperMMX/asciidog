package org.supermmx.asciidog.builder

import org.supermmx.asciidog.builder.factory.DocumentFactory
import org.supermmx.asciidog.builder.factory.SectionFactory
import org.supermmx.asciidog.builder.factory.HeaderFactory
import org.supermmx.asciidog.builder.factory.AttributeFactory
import org.supermmx.asciidog.builder.factory.ListFactory
import org.supermmx.asciidog.builder.factory.ListItemFactory
import org.supermmx.asciidog.builder.factory.ParagraphFactory
import org.supermmx.asciidog.builder.factory.TextFactory
import org.supermmx.asciidog.builder.factory.FormattingFactory
import org.supermmx.asciidog.builder.factory.AttributeReferenceFactory

import groovy.util.FactoryBuilderSupport
import groovy.util.logging.Slf4j

@Slf4j
class AsciiDocBuilder extends FactoryBuilderSupport {
    {
        log.info 'Registering builder factories...'

        registerFactory('document', new DocumentFactory())
        registerFactory('header', new HeaderFactory())
        registerFactory('attribute', new AttributeFactory())
        registerFactory('section', new SectionFactory())
        registerFactory('ol', new ListFactory())
        registerFactory('ul', new ListFactory())
        registerFactory('listItem', new ListItemFactory())
        registerFactory('para', new ParagraphFactory())

        registerFactory('text', new TextFactory())
        registerFactory('formatting', new FormattingFactory())
        registerFactory('aref', new AttributeReferenceFactory())
    }
}
