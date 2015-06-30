package org.supermmx.asciidog.builder

import org.supermmx.asciidog.builder.factory.DocumentFactory
import org.supermmx.asciidog.builder.factory.SectionFactory
import org.supermmx.asciidog.builder.factory.ParagraphFactory
import org.supermmx.asciidog.builder.factory.TextFactory
import org.supermmx.asciidog.builder.factory.FormattingFactory

import groovy.util.FactoryBuilderSupport
import groovy.util.logging.Slf4j

@Slf4j
class AsciiDocBuilder extends FactoryBuilderSupport {
    {
        log.info 'Registering builder factories...'

        registerFactory('document', new DocumentFactory())
        registerFactory('section', new SectionFactory())
        registerFactory('paragraph', new ParagraphFactory())

        registerFactory('text', new TextFactory())
        registerFactory('formatting', new FormattingFactory())
    }
}
