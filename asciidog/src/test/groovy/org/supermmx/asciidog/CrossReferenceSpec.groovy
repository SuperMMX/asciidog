package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph

class CrossReferenceSpec extends AsciidogSpec {
    def 'xref using angled bracket syntax'() {
        given:
        def content = '<<tigers>>'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.info = inlineInfo(constrained: false, escaped: false,
                                      start: 0, end: length,
                                      contentStart:0, contentEnd: length)
            current.inlineNodes = [
                crossReferenceNode(type: Node.Type.INLINE_CROSS_REFERENCE,
                                   xrefId: 'tigers') {
                    current.info = inlineInfo(constrained: false, escaped: false,
                                              start: 0, end: length,
                                              contentStart: 2, contentEnd: length - 2)
                }
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }
}
