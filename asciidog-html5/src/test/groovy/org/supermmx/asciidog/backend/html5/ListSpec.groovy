package org.supermmx.asciidog.backend.html5

class ListSpec extends Html5Spec {
    def 'ul: Simple lists'() {
        def doc = builder.document(title: 'Document Title') {
            section(title: 'Lists') {
                ul {
                    item {
                        para {
                            text 'Foo'
                        }
                    }

                    item {
                        para {
                            text 'Boo'
                        }
                    }

                    item {
                        para {
                            text 'Blech'
                        }
                    }
                }
            }
        }

        def expectedBody = markupHtml {
            body {
                h2 (id: 'Lists', 'Lists')
                div(class: '') {
                    ul {
                        li {
                            div(class: 'paragraph') {
                                p 'Foo'
                            }
                        }
                        li {
                            div(class: 'paragraph') {
                                p 'Boo'
                            }
                        }
                        li {
                            div(class: 'paragraph') {
                                p 'Blech'
                            }
                        }
                    }
                }
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
