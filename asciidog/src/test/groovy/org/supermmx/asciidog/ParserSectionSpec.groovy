package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ParserSectionSpec extends AsciidogSpec {
    def 'static: is section'() {
        expect:
        [ level, title ] == Parser.isSection(line)

        where:
        level | title      | line
        -1    | null       | null
        -1    | null       | ''
        -1    | null       | ' = Title '
        -1    | null       | '======= Title'
        -1    | null       | '==abc'
        0     | 'Level 0'  | '= Level 0'
        1     | 'Level 1'  | '== Level 1'
        2     | 'Level 2'  | '=== Level 2'
        3     | 'Level 3'  | '==== Level 3'
        4     | 'Level 4'  | '===== Level 4'
        5     | 'Level 5'  | '====== Level 5'
    }

    def 'parse: section: not expected level'() {
        given:
        def content = '''

==   Section Title   

'''

        def parser = parser(content)
        parser.parseBlockHeader()

        when:
        def section = parser.parseSection(new Block(), 3)

        then:
        section == null

    }

    def 'parse: section: empty section'() {
        given:

        def content = '''

==   Section Title   

'''
        def expectedSection = builder.section(title: 'Section Title',
                                              id: '_Section Title',
                                              level: 1)
        def parser = parser(content)

        when:

        parser.parseBlockHeader()

        def section = parser.parseSection(new Block(), 1)

        then:

        section == expectedSection
    }

    def 'parse: section: with id'() {
        given:
        def content = '''

[[section-id]]
==   Section Title   

'''
        def expectedSection = builder.section(id: 'section-id',
                                              title: 'Section Title',
                                              level: 1)
        def parser = parser(content)

        when:
        parser.parseBlockHeader()
        def section = parser.parseSection(new Block(), 1)

        then:

        section == expectedSection
    }

    def 'parse: section: simple paragraph block'() {
        given:

        def text1 = '''this is a paragraph
with another line'''
        def text2 = 'New paragraph'
        def content = """

== Section Title

$text1

$text2


"""
        def expectedSection = builder.section('Section Title',
                                              id: '_Section Title',
                                              level: 1) {
            para {
                text text1
            }

            para {
                text text2
            }
        }

        def parser = parser(content)

        when:

        parser.parseBlockHeader()
        def section = parser.parseSection(new Block(), 1)

        then:

        section == expectedSection
    }

    def 'parse: section: with sub section'() {
        given:
        def text1 = '''this is a paragraph
with another line'''
        def text2 = 'paragraph for the subsection'
        def content = """

== Section Title

$text1

=== Subsection Title

$text2

"""
        def expectedSection = builder.section('Section Title',
                                              id: '_Section Title',
                                              level: 1) {
            para {
                text text1
            }

            section('Subsection Title',
                    id: '_Subsection Title') {
                para {
                    text text2
                }
            }
        }

        def parser = parser(content)

        when:
        parser.parseBlockHeader()
        def section = parser.parseSection(new Block(), 1)

        then:
        section == expectedSection
    }

    def 'parse: section: with sibling section'() {
        given:
        def text1 = '''this is a paragraph
with another line'''
        def text2 = 'paragraph for the subsection'
        def content = """

== Section Title

$text1

== Next Section Title

$text2

"""
        def expectedSection = builder.section('Section Title',
                                              id: '_Section Title',
                                              level: 1) {
            para {
                text text1
            }
        }

        def parser = parser(content)

        when:

        parser.parseBlockHeader()
        def section = parser.parseSection(new Block(), 1)

        then:

        section == expectedSection
    }

    def 'parse: section: with upper section'() {
        given:
        def content = '''

=== Section Title

== Upper Section Title

'''
        def expectedSection = builder.section('Section Title',
                                              id: '_Section Title',
                                              level: 2) {
        }

        def parser = parser(content)

        parser.parseBlockHeader()

        when:

        def section = parser.parseSection(new Block(), 2)

        then:

        section == expectedSection
    }

    def 'parse: section: with sub section with id'() {
        given:
        def content = '''

== Section Title

[[subid]]
=== Subsection Title
'''
        def expectedSection = builder.section('Section Title',
                                              id: '_Section Title',
                                              level: 1) {
            section('Subsection Title',
                    id: 'subid')
        }

        def parser = parser(content)

        parser.parseBlockHeader()

        when:

        def section = parser.parseSection(new Block(), 1)

        then:

        section == expectedSection
    }

    def 'parse: section: with sibling section with id'() {
        given:
        def content = '''

== Section Title

[[sec-id]]
== Next Section Title
'''
        def expectedSection = builder.section('Section Title',
                                              id: '_Section Title',
                                              level: 1)

        def parser = parser(content)
        parser.parseBlockHeader()

        when:

        def section = parser.parseSection(new Block(), 1)

        then:

        section == expectedSection
    }

    def 'parse: section: with upper section with id'() {
        given:
        def content = '''

=== Section Title

[[sec-id]]
== Upper Section Title

'''
        def expectedSection = builder.section('Section Title',
                                              id: '_Section Title',
                                              level: 2)

        def parser = parser(content)
        parser.parseBlockHeader()

        when:

        def section = parser.parseSection(new Block(), 2)

        then:

        section == expectedSection
    }
}
