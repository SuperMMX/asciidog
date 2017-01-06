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

        def eDoc = builder.document{
            para {
                criticAdd {
                    text 'addition content'
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'simple deletion'() {
        given:
        def content = '{-- deletion content --}'
        def eDoc = builder.document {
            para {
                criticDelete {
                    text ' deletion content '
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'simple comment'() {
        given:
        def content = '{>>this is a comment<<}'
        def eDoc = builder.document {
            para {
                criticComment {
                    text 'this is a comment'
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'simple highlight'() {
        given:
        def content = '{==highlight==}'
        def eDoc = builder.document {
            para {
                criticHighlight {
                    text 'highlight'
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'simple substitution'() {
        given:
        def content = '{~~deleted~>added~~}'
        def eDoc = builder.document {
            para {
                criticSubst {
                    criticDelete {
                        text 'deleted'
                    }
                    criticAdd {
                        text 'added'
                    }
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'highlight with comment'() {
        given:
        def content = '{==highlight==}{>>this is the comment<<}'
        def eDoc = builder.document {
            para {
                criticHighlight {
                    text 'highlight'
                }
                criticComment {
                    text 'this is the comment'
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'critic with attribute reference outside'() {
        given:
        def content = '{reference}{++ added++}'
        def eDoc = builder.document {
            para {
                aref 'reference'
                criticAdd {
                    text ' added'
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'critic with attribute reference inside'() {
        given:
        def content = '{--{reference} deleted--}'
        def eDoc = builder.document {
            para {
                criticDelete {
                    aref 'reference'
                    text ' deleted'
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'comment with xref outside'() {
        given:
        def content = '<<xref>>{>>comment<<}'
        def eDoc = builder.document {
            para {
                xref 'xref'
                criticComment {
                    text 'comment'
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'comment with xref inside'() {
        given:
        def content = '{>>see <<xref>><<}'
        def eDoc = builder.document {
            para {
                criticComment {
                    text 'see '
                    xref 'xref'
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'comment with xref tag in between'() {
        given:
        def content = '<<ref{>>comment<<}ref>>'
        def eDoc = builder.document {
            para {
                xref 'ref{'
                text 'comment'
                xref '}ref'
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }
}

