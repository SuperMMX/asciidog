package org.supermmx.asciidog.parser.action

import org.supermmx.asciidog.Attribute
import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode

class AttributeEntryParserSpec extends AsciidogSpec {
    def 'document: creates an attribute'() {
        given:
        def content = ':frog: Tanglefoot'
        def eDoc = builder.document {
            attribute 'frog', 'Tanglefoot'
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
        doc.attrs['frog'] == 'Tanglefoot'
        doc.attrs.frog == 'Tanglefoot'
        doc.attrs.getAttribute('frog') == new Attribute(name: 'frog', type: Attribute.ValueType.INLINES,
                                                        value: [ new TextNode(text: 'Tanglefoot') ],
                                                        valueString: 'Tanglefoot')
    }
}
