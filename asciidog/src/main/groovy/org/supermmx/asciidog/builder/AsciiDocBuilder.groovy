package org.supermmx.asciidog.builder

import org.supermmx.asciidog.builder.factory.*

import groovy.util.FactoryBuilderSupport
import groovy.util.logging.Slf4j

@Slf4j
class AsciiDocBuilder extends FactoryBuilderSupport {
    {
        log.info 'Registering builder factories...'

        registerFactory(new DocumentFactory())
        registerFactory(new HeaderFactory())
        registerFactory(new AttributeFactory())
        registerFactory(new SectionFactory())
        registerFactory(new OrderedListFactory())
        registerFactory(new UnOrderedListFactory())
        registerFactory(new ListItemFactory())
        registerFactory(new ParagraphFactory())

        registerFactory(new TextFactory())
        registerFactory(new StrongFormattingFactory())
        registerFactory(new EmphasisFormattingFactory())
        registerFactory(new AttributeReferenceFactory())
    }

    protected void registerFactory(AbstractNodeFactory nodeFactory) {
        registerFactory(nodeFactory.name, nodeFactory)
    }
}
