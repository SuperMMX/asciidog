package org.supermmx.asciidog.backend.html5

class AttributesSpec extends Html5Spec {
    def 'resolves attributes inside attribute value within header'() {
        given:
        def doc = builder.document {
            header('Document Title') {
                attribute 'big', 'big'
                attribute 'bigfoot', '{big}foot'
            }

            preamble {
                para {
                    aref 'bigfoot'
                }
            }
        }

        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                p 'bigfoot'
            }
        }

        when:
        def body = adocHtml(doc) {
            it.body
        }

        then:
        body == expectedBody
    }

    def 'render properly with simple names'() {
        given:
        def doc = builder.document {
            header('Document Title') {
                attribute 'frog', 'Tanglefoot'
                attribute 'my_super-hero', 'Spiderman'
            }

            preamble {
                para {
                    text 'Yo, '
                    aref 'frog'
                    text '!\nBeat '
                    aref 'my_super-hero'
                    text '!'
                }
            }
        }

        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                p 'Yo, Tanglefoot!\nBeat Spiderman!'
            }
        }

        when:
        def body = adocHtml(doc) {
            it.body
        }

        then:
        body == expectedBody
    }

    def 'render properly with single character name'() {
        given:
        def doc = builder.document {
            header('Document Title') {
                attribute 'r', 'Ruby'
            }

            preamble {
                para {
                    text 'R is for '
                    aref 'r'
                    text '!'
                }
            }
        }
        def content = '''= Document Title
:r: Ruby

R is for {r}!
'''
        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                p 'R is for Ruby!'
            }
        }

        when:
        def body = adocHtml(doc) {
            it.body
        }

        then:
        body == expectedBody
    }
}
