package org.supermmx.asciidog

import spock.lang.*

class ReaderSpec extends Specification {
    def 'peek line'() {
        given:
        def reader = Reader.createFromString('''line1
''');

        when:
        def line = reader.peekLine()

        then:
        line == 'line1'

        when:
        line = reader.nextLine()

        then:
        line == 'line1'
    }
    
    def 'next line'() {
        given:
        def reader = Reader.createFromString('''line1
''');

        when:
        def line = reader.nextLine()

        then:
        line == 'line1'

        when:
        line = reader.nextLine()

        then:
        line == null
    }

    def 'peek lines'() {
        given:
        def reader = Reader.createFromString('''line1
line2
line3
''');

        when:
        def lines = reader.peekLines(2)

        then:
        lines == [ 'line1', 'line2' ]

        when:
        def line = reader.nextLine()

        then:
        line == 'line1'
    }
    
    def 'peek lines without enough data'() {
        given:
        def reader = Reader.createFromString('''line1
line2
''');

        when:
        def lines = reader.peekLines(3)

        then:
        lines == [ 'line1', 'line2', null ]

        when:
        def line = reader.nextLine()

        then:
        line == 'line1'
    }

    def 'next lines'() {
        given:
        Reader reader = Reader.createFromString('''line1
line2
line3
''');

        when:
        def lines = reader.nextLines(2)

        then:
        lines == [ 'line1', 'line2' ]

        when:
        lines = reader.nextLines(2)

        then:
        lines ==  [ 'line3', null ]

        when:
        lines = reader.nextLines(2)

        then:
        lines ==  [ null, null ]
    }

    def 'skip blank lines'() {
        given:
        Reader reader = Reader.createFromString('''line1
line2


line3
 line4
line5  abc




''');

        expect:
        reader.skipBlankLines() == 0
        reader.nextLines(2) == [ 'line1', 'line2' ]
        reader.skipBlankLines() == 2
        reader.nextLines(3) == [ 'line3', ' line4', 'line5  abc']
        reader.skipBlankLines() == 4
        reader.nextLine() == null
    }

    def 'line number'() {
        given:

        def reader = Reader.createFromString('''line1
line2
line3
line4
line5
''');

        // start
        expect:

        reader.cursor.lineno == 1

        // peek one line
        when:

        def line = reader.peekLine()

        then:

        reader.cursor.lineno == 1

        // read one line
        when:

        line = reader.nextLine()

        then:

        reader.cursor.lineno == 2

        // peek next three lines
        when:

        reader.peekLines(3)

        then:

        reader.cursor.lineno == 2

        // read next three lines
        when:

        reader.nextLines(3)

        then:

        reader.cursor.lineno == 5

        // read next three lines
        // no more lines
        when:

        reader.nextLines(3)

        then:

        reader.cursor.lineno == -1
    }
}
