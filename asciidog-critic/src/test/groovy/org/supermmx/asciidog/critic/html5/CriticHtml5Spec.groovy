package org.supermmx.asciidog.critic.html5

import org.supermmx.asciidog.backend.html5.Html5Spec

class CriticHtml5Spec extends Html5Spec {
    def 'critic addition'() {
        given:
        def doc = builder.document {
            para {
                criticAdd {
                    text 'addition content'
                }
            }
        }

        def expectedBody = markupHtml {
            body {
                p {
                    ins('addition content')
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

    def 'critic deletion'() {
        given:
        def doc = builder.document {
            para {
                criticDelete {
                    text ' deletion content '
                }
            }
        }

        def expectedBody = markupHtml {
            body {
                p {
                    del(' deletion content ')
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

    def 'critic comment'() {
        given:
        def doc = builder.document {
            para {
                criticComment {
                    text 'this is a comment'
                }
            }
        }

        def expectedBody = markupHtml {
            body {
                p {
                    span(class: 'critic comment', 'this is a comment')
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

    def 'critic highlight'() {
        given:
        def doc = builder.document {
            para {
                criticHighlight {
                    text 'highlight'
                }
            }
        }

        def expectedBody = markupHtml {
            body {
                p {
                    mark('highlight')
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

    def 'critic substitution'() {
        given:
        def doc = builder.document {
            para {
                criticSubst {
                    criticDelete {
                        text 'deleted'
                    }
                    criticAdd {
                        text 'added'
                    }
                }
            }
        }

        def expectedBody = markupHtml {
            body {
                p {
                    del 'deleted'
                    ins 'added'
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
