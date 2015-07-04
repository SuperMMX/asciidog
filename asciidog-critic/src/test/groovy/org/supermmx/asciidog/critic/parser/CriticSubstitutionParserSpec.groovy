package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.AsciidogSpec

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.NullNode

class CriticSubstitutionParserSpec extends AsciidogSpec {
    def 'simple substitution'() {
        given:
        def content = '{~~deleted~>added~~}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.SUBSTITUTION,
                               inlineNodes: [
                                   new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                                                  criticType: CriticNode.CriticType.DELETION,
                                                  inlineNodes: [
                                                      new TextNode('deleted')
                                                  ]),
                                   new NullNode(),
                                   new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                                                  criticType: CriticNode.CriticType.ADDITION,
                                                  inlineNodes: [
                                                      new TextNode('added')
                                                  ])
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }
}

