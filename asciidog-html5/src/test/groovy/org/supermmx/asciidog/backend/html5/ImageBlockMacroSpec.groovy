package org.supermmx.asciidog.backend.html5

class ImageBlockMacroSpec extends Html5Spec {
    def 'image'() {
        given:
        def doc = builder.document(title: 'Document Title') {
            image 'test.jpeg'
        }

        def expectedBody = markupHtml {
            body {
                img(src: 'test.jpeg')
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
