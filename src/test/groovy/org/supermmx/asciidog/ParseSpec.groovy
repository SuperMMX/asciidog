package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Header

import spock.lang.*

class ParserSpec extends Specification {
    def 'isSection'() {
        expect:
        [ level, title ] == Parser.isSection(line)

        where:
        level | title     | line
        -1    | null      | null
        -1    | null      | ' = Title '
        -1    | null      | '======= Title'
        -1    | null      | '==abc'
        0     | 'Level 0' | '= Level 0'
        1     | 'Level 1' | '== Level 1'
        2     | 'Level 2' | '=== Level 2'
        3     | 'Level 3' | '==== Level 3'
        4     | 'Level 4' | '===== Level 4'
        5     | 'Level 5' | '====== Level 5'
    }
}
