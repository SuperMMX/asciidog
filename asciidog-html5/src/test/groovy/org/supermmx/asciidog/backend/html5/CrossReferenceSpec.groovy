package org.supermmx.asciidog.backend.html5

class CrossReferenceSpec extends Html5Spec {
    def 'xref using angled bracket syntax for section'() {
        given:
        def doc = builder.document(title: 'Document Title') {
            preamble {
                para {
                    xref 'Section'
                }
            }

            section(title: 'Section')
        }

        def expectedBody = markupHtml {
            body {
                div(class: '') {
                    div(class: 'paragraph') {
                        p {
                            a(href: '#Section', 'Section')
                        }
                    }
                }
                h2(id: 'Section', 'Section')
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
