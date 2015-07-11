package org.supermmx.asciidog.builder

import spock.lang.*

class AsciiDocBuilderSpec extends Specification {
    def 'test'() {
        given:
        def builder = new AsciiDocBuilder()

        def doc = builder.document {
            section(title: 'Section Title') {
                para {
                    text 'pre '

                    strong {
                        text 'text'
                    }

                    text ' post'
                }

                section(title: 'Sub Section') {
                }
            }
        }

        println "doc = ${doc}"
    }

    def 'list'() {
        given:
        def builder = new AsciiDocBuilder()

        def doc = builder.document {
            section(title: 'Section Title') {
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
            header(title: 'Section Title') {
                attribute('name', 'value')
                attribute('name')
            }
        }

        println "doc = ${doc}"
    }
}
