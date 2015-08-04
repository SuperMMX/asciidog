package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph

class CrossReferenceSpec extends AsciidogSpec {
    def 'xref using angled bracket syntax'() {
        given:
        def content = '<<tigers>>'
        def length = content.length()
        def expectedPara = builder.para {
            xref 'tigers'
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }
}
