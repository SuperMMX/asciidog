package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec

class SectionParserSpec extends AsciidogSpec {
    def 'is section'() {
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

}
