package org.supermmx.asciidog.backend.html5

class SectionSpec extends Html5Spec {
    def 'section'() {
        given:
        def doc = builder.document(title: 'Document Title') {
            section(title: 'Section Title')
        }

        def expectedBody = markupHtml {
            body {
                h2(id: 'Section_Title', 'Section Title')
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
