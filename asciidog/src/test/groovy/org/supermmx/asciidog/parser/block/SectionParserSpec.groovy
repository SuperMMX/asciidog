package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser

class SectionParserSpec extends AsciidogSpec {
    def sectionParser = new SectionParser()

    def 'static: is section'() {
        expect:
        [ level, title ] == SectionParser.isSection(line)

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

    def 'standalone: simple section'() {
        given:
        def content = '== Section Title'
        def expectedSection = builder.section(level: 1, title: 'Section Title')

        def context = parserContext(content)
        context.parserId = SectionParser.ID
        context.expectedSectionLevel = 1

        when:
        def section = Parser.parse(context)

        then:
        section == expectedSection
    }

    def 'standalone: section with wrong level'() {
        given:
        def content = '== Section Title'

        def context = parserContext(content)
        context.parserId = SectionParser.ID
        context.expectedSectionLevel = level

        when:
        def section = Parser.parse(context)

        then:
        section == null

        where:
        level | value
        0     | null
        2     | null
    }

    def 'standalone: section with sub sections'() {
        given:
        def content = '''== Section Title

=== Sub Section

=== Another Sub Section
'''
        def expectedSection = builder.section(level: 1, title: 'Section Title') {
            section(level: 2, title: 'Sub Section')
            section(level: 2, title: 'Another Sub Section')
        }

        def context = parserContext(content)
        context.parserId = SectionParser.ID
        context.expectedSectionLevel = 1

        when:
        def section = Parser.parse(context)

        then:
        section == expectedSection
    }
}
