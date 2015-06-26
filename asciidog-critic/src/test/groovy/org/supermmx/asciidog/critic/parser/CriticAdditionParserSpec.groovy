package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.AsciidogSpec

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node

class CriticAdditionParserSpec extends AsciidogSpec {
    def 'simple addition'() {
        given:
        def content = '{++addition content++}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.info = inlineInfo(constrained: false, escaped: false,
                                      start: 0, end: length,
                                      contentStart:0, contentEnd: length)
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               info: inlineInfo(constrained: false, escaped: false,
                                                start: 0, end: length,
                                                contentStart: 3, contentEnd: length - 3),
                               inlineNodes: [
                                   TextNode(type: Node.Type.TEXT, text: 'addition content') {
                                       current.info = inlineInfo(constrained: false, escaped: false,
                                                                 start: 3, end: length - 3,
                                                                 contentStart: 3, contentEnd: length - 3)
                                   }
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }
}

