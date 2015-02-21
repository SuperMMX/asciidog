package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ParserSectionSpec extends Specification {
    @Shared
    def builder = new ObjectGraphBuilder()

    def setupSpec() {
        builder.classNameResolver = "org.supermmx.asciidog.ast"
        builder.identifierResolver = "uid"
    }

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

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

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
                                              level: 1)

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

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

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        parser.parseBlockHeader()
        def section = parser.parseSection(new Block(), 1)

        then:

        section == expectedSection
    }

    def 'parse: section: simple paragraph block'() {
        given:

        def content = '''

== Section Title

this is a paragraph
with another line

New paragraph


'''
        def expectedSection = builder.section(title: 'Section Title',
                                              level: 1) {
            current.blocks = [
                paragraph(lines: ['this is a paragraph', 'with another line']),
                paragraph(lines: ['New paragraph'])
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:

        parser.parseBlockHeader()
        def section = parser.parseSection(new Block(), 1)

        then:

        section == expectedSection
    }

    def 'parse: section: with sub section'() {
        given:
        def content = '''

== Section Title

this is a paragraph
with another line

=== Subsection Title

paragraph for the subsection

'''
        def expectedSection = builder.section(title: 'Section Title',
                                              level: 1) {
            current.blocks = [
                paragraph(lines: ['this is a paragraph', 'with another line']),
                section(title: 'Subsection Title') {
                    current.blocks = [
                        paragraph(lines: ['paragraph for the subsection'])
                    ]
                }
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        parser.parseBlockHeader()
        def section = parser.parseSection(new Block(), 1)

        then:
        section == expectedSection
    }

    def 'parse: section: with sibling section'() {
        given:
        def content = '''

== Section Title

this is a paragraph
with another line

== Next Section Title

paragraph for the subsection

'''
        def expectedSection = builder.section(title: 'Section Title',
                                              level: 1) {
            current.blocks = [
                paragraph(lines: ['this is a paragraph', 'with another line'])
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

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
        def expectedSection = builder.section(title: 'Section Title',
                                              level: 2) {
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

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
        def expectedSection = builder.section(title: 'Section Title',
                                              level: 1) {
            current.blocks = [
                section(title: 'Subsection Title',
                        id: 'subid')
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

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
        def expectedSection = builder.section(title: 'Section Title',
                                              level: 1)

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

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
        def expectedSection = builder.section(title: 'Section Title',
                                              level: 2)

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        parser.parseBlockHeader()

        when:

        def section = parser.parseSection(new Block(), 2)

        then:

        section == expectedSection
    }
}
