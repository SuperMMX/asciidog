package org.supermmx.asciidog.builder

import org.supermmx.asciidog.builder.factory.*
import org.supermmx.asciidog.plugin.BuilderPlugin

/**
 * The core builder plugin
 */
class AsciidogCoreBuilderPlugin extends BuilderPlugin {
    AsciidogCoreBuilderPlugin() {
        id = 'core_builder'

        factories << new DocumentFactory()
        factories << new HeaderFactory()
        factories << new AuthorsFactory()
        factories << new AuthorFactory()
        factories << new PreambleFactory()
        factories << new AttributeFactory()
        factories << new SectionFactory()
        factories << new OrderedListFactory()
        factories << new UnOrderedListFactory()
        factories << new ListItemFactory()
        factories << new ParagraphFactory()
        factories << new CommentFactory()

        factories << new TextFactory()
        factories << new StrongFormattingFactory()
        factories << new EmphasisFormattingFactory()
        factories << new AttributeReferenceFactory()
        factories << new CrossReferenceFactory()
    }
}
