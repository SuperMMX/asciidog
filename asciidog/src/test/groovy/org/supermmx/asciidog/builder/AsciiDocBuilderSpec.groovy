package org.supermmx.asciidog.builder

import spock.lang.*

class AsciiDocBuilderSpec extends Specification {
    def 'test'() {
        given:
        def builder = new AsciiDocBuilder()

        def doc = builder.document {
            section('Section Title') {
                para {
                    text 'pre '

                    strong {
                        text 'text'
                    }

                    text ' post'
                }

                section('Sub Section') {
                }
            }
        }

        println "doc = ${doc}"
    }

    def 'list'() {
        given:
        def builder = new AsciiDocBuilder()

        def doc = builder.document {
            section('Section Title') {
                ul {
                    item {
                        para {
                            text 'first item'
                        }
                    }

                    item {
                        ol {
                            item {
                                para {
                                    text 'first item lin ordered list'
                                }
                            }
                        }
                        para {
                            text 'second item'
                        }
                    }
                }
            }
        }

        println "doc = ${doc}"
    }

    def 'attr'() {
        given:
        def builder = new AsciiDocBuilder()

        def doc = builder.document {
            header('Document Title') {
                attribute('name', 'value')
                attribute('name')
            }
        }

        println "doc = ${doc}"
    }
}
