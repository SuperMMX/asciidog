package org.supermmx.asciidog.backend.html5

class SectionSpec extends Html5Spec {
    def 'section'() {
        given:
        def content = '''= Document Title

== Section Title
'''
        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                h2 'Section Title'
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
