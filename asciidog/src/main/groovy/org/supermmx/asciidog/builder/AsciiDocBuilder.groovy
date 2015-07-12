package org.supermmx.asciidog.builder

import org.supermmx.asciidog.builder.factory.*

import groovy.util.FactoryBuilderSupport
import groovy.util.logging.Slf4j

@Slf4j
class AsciiDocBuilder extends FactoryBuilderSupport {
    {
        registerFactory(new DocumentFactory())
        registerFactory(new HeaderFactory())
        registerFactory(new PreambleFactory())
        registerFactory(new AttributeFactory())
        registerFactory(new SectionFactory())
        registerFactory(new OrderedListFactory())
        registerFactory(new UnOrderedListFactory())
        registerFactory(new ListItemFactory())
        registerFactory(new ParagraphFactory())
        registerFactory(new CommentFactory())

        registerFactory(new TextFactory())
        registerFactory(new StrongFormattingFactory())
        registerFactory(new EmphasisFormattingFactory())
        registerFactory(new AttributeReferenceFactory())
        registerFactory(new CrossReferenceFactory())

        // TODO: register plugins factory
    }

    protected void registerFactory(AbstractNodeFactory nodeFactory) {
        registerFactory(nodeFactory.name, nodeFactory)
    }
}
