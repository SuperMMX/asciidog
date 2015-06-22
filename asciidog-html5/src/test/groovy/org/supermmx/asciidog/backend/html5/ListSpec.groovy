package org.supermmx.asciidog.backend.html5

class ListSpec extends Html5Spec {
    def 'ul: Simple lists'() {
        def content = '''= Document Title

== Lists

- Foo
- Boo
- Blech
'''
        def expectedBody = markupHtml {
            body {
                h1 'Document Title'
                h2 (id: '_Lists', 'Lists')
                ul {
                    li {
                        p 'Foo'
                    }
                    li {
                        p 'Boo'
                    }
                    li {
                        p 'Blech'
                    }
                }
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
