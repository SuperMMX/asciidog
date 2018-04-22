package org.supermmx.asciidog.parser.action

import org.supermmx.asciidog.Attribute
import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.parser.action.AttributeEntryParser

class AttributeEntryParserSpec extends AsciidogSpec {
    def 'static: is attribute, value of single line'() {
        expect:
        [ name, value ] == AttributeEntryParser.isAttribute(line)

        where:
        name    | value       | line
        'attr'  | 'a value'   | ':attr: a value'
        'attr'  | 'a value'   | ':attr:   a value'
        'attr'  | null        | ':attr:'
        'at tr'  | ''          | ':at tr:  '
        '!at tr' | null        | ':!at tr:'
        null    | null        | null
        null    | null        | ''
        null    | null        | 'abcdef'
        null    | null        | '* abc'
        null    | null        | '== abc'
    }

    def 'document: creates an attribute'() {
        given:
        def content = ':frog: Tanglefoot'
        def eDoc = builder.document {
            // simple attribute creation
            attribute 'frog', 'Tanglefoot'
            /*
            // the formal way to create the attribute
            attribute(name: 'frog') {
                text 'Tanglefoot'
            }
             */
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
        doc.attrs['frog'] == 'Tanglefoot'
        doc.attrs.frog == 'Tanglefoot'
        doc.attrs.getAttribute('frog') == new Attribute(name: 'frog', type: Attribute.ValueType.STRING,
                                                        value: 'Tanglefoot')
    }
}
