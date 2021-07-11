package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Node

class ImageBlockMacroSpec extends Html5Spec {
    def 'simple image block macro'() {
        given:
        def doc = builder.document(title: 'Document Title') {
            image 'test.jpeg'
        }

        def expectedBody = markupHtml {
            body {
                div(class: '') {
                    img(src: 'test.jpeg')
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

    def 'image block macro in attribute imagesdir'() {
        given:
        def doc = builder.document(title: 'Document Title') {
            attribute 'imagesdir', 'images/path'
            image 'test.jpeg'
        }

        def expectedBody = markupHtml {
            body {
                div(class: '') {
                    img(src: 'images/path/test.jpeg')
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
