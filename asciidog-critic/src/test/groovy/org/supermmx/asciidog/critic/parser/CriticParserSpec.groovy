package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.AsciidogSpec

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.NullNode
import org.supermmx.asciidog.ast.AttributeReferenceNode
import org.supermmx.asciidog.ast.CrossReferenceNode

class CriticParserSpec extends AsciidogSpec {
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

    def 'simple deletion'() {
        given:
        def content = '{-- deletion content --}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.DELETION,
                               inlineNodes: [
                                   new TextNode(' deletion content ')
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }

    def 'simple comment'() {
        given:
        def content = '{>>this is a comment<<}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.COMMENT,
                               inlineNodes: [
                                   new TextNode('this is a comment')
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }

    def 'simple highlight'() {
        given:
        def content = '{==highlight==}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.HIGHLIGHT,
                               inlineNodes: [
                                   new TextNode('highlight')
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }

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

    def 'highlight with comment'() {
        given:
        def content = '{==highlight==}{>>this is the comment<<}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.HIGHLIGHT,
                               inlineNodes: [
                                   new TextNode('highlight')
                               ]),
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.COMMENT,
                               inlineNodes: [
                                   new TextNode('this is the comment')
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }

    def 'critic with attribute reference outside'() {
        given:
        def content = '{reference}{++ added++}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new AttributeReferenceNode(name: 'reference'),
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.ADDITION,
                               inlineNodes: [
                                   new TextNode(' added')
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }

    def 'critic with attribute reference inside'() {
        given:
        def content = '{--{reference} deleted--}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.DELETION,
                               inlineNodes: [
                                   new AttributeReferenceNode(name: 'reference'),
                                   new TextNode(' deleted')
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }

    def 'comment with xref outside'() {
        given:
        def content = '<<xref>>{>>comment<<}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CrossReferenceNode(type: Node.Type.CROSS_REFERENCE,
                                       xrefId: 'xref'),
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.COMMENT,
                               inlineNodes: [
                                   new TextNode('comment')
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }

    def 'comment with xref inside'() {
        given:
        def content = '{>>see <<xref>><<}'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.COMMENT,
                               inlineNodes: [
                                   new TextNode('see '),
                                   new CrossReferenceNode(type: Node.Type.CROSS_REFERENCE,
                                                          xrefId: 'xref'),
                               ])
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }

    def 'comment with xref tag in between'() {
        given:
        def content = '<<ref{>>comment<<}ref>>'
        def length = content.length()
        def expectedPara = builder.paragraph() {
            current.inlineNodes = [
                new TextNode('<<ref'),
                new CriticNode(type: CriticNode.CRITIC_NODE_TYPE,
                               criticType: CriticNode.CriticType.COMMENT,
                               inlineNodes: [
                                   new TextNode('comment'),
                               ]),
                new TextNode('ref>>'),
            ]
        }


        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }
}

