package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.AsciidogSpec

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode

class CriticAdditionParserSpec extends AsciidogSpec {
    def 'simple addition'() {
        given:
        def content = '{++addition content++}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.ADDITION,
                               inlineNodes: [
                                   new TextNode('addition content')
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }
}

