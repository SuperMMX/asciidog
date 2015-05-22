package org.supermmx.asciidog.backend.html5

class AttributesSpec extends Html5Spec {
    def 'resolves attributes inside attribute value within header'() {
        given:
        def content = '''= Document Title
:big: big
:bigfoot: {big}foot

{bigfoot}
'''
        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                p 'bigfoot'
            }
        }

        when:
        def body = adocHtml(content) {
            it.body
        }

        then:
        body == expectedBody
    }

    def 'render properly with simple names'() {
        given:
        def content = '''= Document Title
:frog: Tanglefoot
:my_super-hero: Spiderman

Yo, {frog}!
Beat {my_super-hero}!
'''
        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                p 'Yo, Tanglefoot!\nBeat Spiderman!'
            }
        }

        when:
        def body = adocHtml(content) {
            it.body
        }

        then:
        body == expectedBody
    }
}
