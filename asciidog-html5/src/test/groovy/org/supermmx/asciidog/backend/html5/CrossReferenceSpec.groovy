package org.supermmx.asciidog.backend.html5

class CrossReferenceSpec extends Html5Spec {
    def 'xref using angled bracket syntax for section'() {
        given:
        def content = '''= Document Title

<<_Section>>

== Section
'''
        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                p {
                    a(href: '#_Section', 'Section')
                }
                h2(id: '_Section', 'Section')
            }
        }

        when:
        def body = adocHtml(content) {
            return it.body
        }

        then:
        body == expectedBody
    }
}
