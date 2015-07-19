package org.supermmx.asciidog.backend.html5

class SectionSpec extends Html5Spec {
    def 'section'() {
        given:
        def doc = builder.document {
            header 'Document Title'

            section 'Section Title'
        }

        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                h2(id: '_Section Title', 'Section Title')
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
