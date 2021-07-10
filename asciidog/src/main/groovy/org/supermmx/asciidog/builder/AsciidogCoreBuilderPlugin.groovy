package org.supermmx.asciidog.builder

import org.supermmx.asciidog.builder.factory.*
import org.supermmx.asciidog.plugin.BuilderPlugin

/**
 * The core builder plugin
 */
class AsciidogCoreBuilderPlugin extends BuilderPlugin {
    AsciidogCoreBuilderPlugin() {
        id = 'core_builder'

        factories << new AttributeFactory()
        factories << new AuthorFactory()
        factories << new AuthorsFactory()
        factories << new BlockMacroFactory()
        factories << new CommentFactory()
        factories << new CommentLineFactory()
        factories << new DocumentFactory()
        factories << new HeaderFactory()
        factories << new ImageBlockMacroFactory()
        factories << new ListItemFactory()
        factories << new OrderedListFactory()
        factories << new ParagraphFactory()
        factories << new PreambleFactory()
        factories << new SectionFactory()
        factories << new UnOrderedListFactory()

        // styled block
        factories << new OpenBlockFactory()
        factories << new QuoteFactory()
        factories << new VerseFactory()

        // inline
        factories << new AttributeReferenceFactory()
        factories << new CrossReferenceFactory()
        factories << new EmphasisFormattingFactory()
        factories << new LinkFactory()
        factories << new MarkFormattingFactory()
        factories << new StrongFormattingFactory()
        factories << new TextFactory()
    }
}
