package org.supermmx.asciidog.builder

import spock.lang.*

class AsciiDocBuilderSpec extends Specification {
    def 'test'() {
        given:
        def builder = new AsciiDocBuilder()

        def doc = builder.document {
            section(title: 'Section Title') {
                paragraph {
                    text 'pre '

                    formatting {
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
}
