package org.supermmx.asciidog.backend.html5

class CrossReferenceSpec extends Html5Spec {
    def 'xref using angled bracket syntax for section'() {
        given:
        def doc = builder.document() {
            header 'Document Title'

            preamble {
                para {
                    xref '_Section'
                }
            }

            section('Section')
        }

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
        def body = adocHtml(doc) {
            return it.body
        }

        then:
        body == expectedBody
    }
}
