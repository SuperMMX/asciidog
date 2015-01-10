package org.supermmx.asciidog

import spock.lang.*

class ReaderSpec extends Specification {
    def 'peek line'() {
        given:
        def reader = Reader.createFromString('''line1
''');
        reader.bufferSize = 1

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
        reader.bufferSize = 1

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
        reader.bufferSize = 2

        when:
        def lines = reader.peekLines(2)

        then:
        lines == [ 'line1', 'line2' ]

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
        reader.bufferSize = 2

        when:
        def lines = reader.nextLines(2)

        then:
        lines == [ 'line1', 'line2' ]

        when:
        lines = reader.nextLines(2)

        then:
        lines ==  [ 'line3' ]

        when:
        lines = reader.nextLines(2)

        then:
        lines ==  []
    }
}
