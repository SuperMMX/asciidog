package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.AsciidogSpec

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.AttributeReferenceNode
import org.supermmx.asciidog.ast.CrossReferenceNode

class CriticParserSpec extends AsciidogSpec {
    def 'simple addition'() {
        given:
        def content = '{++addition content++}'

        def expectedPara = builder.para {
            criticAdd {
                text 'addition content'
            }
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
        def expectedPara = builder.para {
            criticDelete {
                text ' deletion content '
            }
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
        def expectedPara = builder.para {
            criticComment {
                text 'this is a comment'
            }
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
        def expectedPara = builder.para {
            criticHighlight {
                text 'highlight'
            }
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
        def expectedPara = builder.para {
            criticSubst {
                criticDelete {
                    text 'deleted'
                }
                criticAdd {
                    text 'added'
                }
            }
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
        def expectedPara = builder.para {
            criticHighlight {
                text 'highlight'
            }
            criticComment {
                text 'this is the comment'
            }
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
        def expectedPara = builder.para {
            aref 'reference'
            criticAdd {
                text ' added'
            }
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
        def expectedPara = builder.para {
            criticDelete {
                aref 'reference'
                text ' deleted'
            }
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
        def expectedPara = builder.para {
            xref 'xref'
            criticComment {
                text 'comment'
            }
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
        def expectedPara = builder.para {
            criticComment {
                text 'see '
                xref 'xref'
            }
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
        def expectedPara = builder.para {
            text '<<ref'
            criticComment {
                text 'comment'
            }
            text 'ref>>'
        }

        when:
        def para = parser(content).parseParagraph(new Block())

        then:
        para == expectedPara
    }
}

