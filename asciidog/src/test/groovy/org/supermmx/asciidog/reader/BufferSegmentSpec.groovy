package org.supermmx.asciidog.reader

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.Specification

class BufferSegmentSpec extends Specification {
    def 'is include'() {
        expect:
        [ uri, attrs, comment ] == BufferSegment.isInclude(line)

        where:
        line   | uri    | attrs     | comment
        null   | null   | null      | true
        ''     | null   | null      | true
        '\\include::file.adoc[]' | 'file.adoc' | '' | true
        'include::file.adoc[lines=20..30]'   | 'file.adoc' | 'lines=20..30' | false
    }

    def 'read next line'() {
        given:

        def content = '''line1
line2
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        when:

        def line = segment.readNextLine()

        then:

        line == 'line1'

        when:

        line = segment.readNextLine()

        then:

        line == 'line2'

        when:

        line = segment.readNextLine()

        then:

        line == null
    }

    def 'read next line with include directive'() {
        given:

        def includeContent = '''include-1
include-2
'''
        GroovySpy(SingleReader, global: true)
        SingleReader.createFromFile("include.adoc") >> SingleReader.createFromString(includeContent)

        def content = '''line1
include::include.adoc[]
line3
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        when:

        def line = segment.readNextLine()

        then:

        line == 'line1'
        segment.nextSegment == null

        when:

        line = segment.readNextLine()

        then:

        line == null
    }

    def 'peek lines'() {
        given:

        def content = '''line1
line2
line3
line4
line5
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        expect:

        segment.peekLines(1) == ['line1']
        segment.peekLines(2) == ['line1', 'line2']
        segment.peekLines(3) == ['line1', 'line2', 'line3']
    }

    def 'peek lines with include'() {
        given:

        def includeContent = '''include-1
include-2
'''
        GroovyMock(FileReader, global: true)
        new FileReader(_ as String) >> new StringReader(includeContent)

        def content = '''line1
line2
include::include.adoc[]
line4
line5
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        expect:

        segment.peekLines(2) == ['line1', 'line2']
        segment.peekLines(3) == ['line1', 'line2']

        when:

        def includeSegment = segment.nextSegment
        def continuousSegment = includeSegment.nextSegment

        then:

        includeSegment.cursor.uri == 'include.adoc'
        continuousSegment.cursor.uri == segment.cursor.uri

        expect:

        includeSegment.readNextLine() == 'include-1'
        continuousSegment.readNextLine() == 'line4'

        // make sure the include directive is only processed once
        segment.peekLines(5) == ['line1', 'line2']
        segment.nextSegment == includeSegment
    }

    def 'peek line'() {
        given:

        def content = '''line1
line2
line3
line4
line5
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        expect:

        segment.peekLine() == 'line1'
        segment.peekLine() == 'line1'
    }

    def 'peek line with include'() {
        given:

        def includeContent = '''include-1
include-2
'''
        GroovyMock(FileReader, global: true)
        new FileReader(_ as String) >> new StringReader(includeContent)

        def content = '''include::include.adoc[]
line4
line5
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        expect:

        segment.peekLine() == null

        when:

        def includeSegment = segment.nextSegment
        def continuousSegment = includeSegment.nextSegment

        then:

        includeSegment.cursor.uri == 'include.adoc'
        continuousSegment.cursor.uri == segment.cursor.uri

        expect:

        includeSegment.readNextLine() == 'include-1'
        continuousSegment.readNextLine() == 'line4'
    }

    def 'next line'() {
        given:

        def content = '''line1
line2
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        expect:

        segment.nextLine() == 'line1'
        segment.cursor.lineno == 1

        segment.nextLine() == 'line2'
        segment.cursor.lineno == 2

        segment.nextLine() == null
        segment.cursor.lineno == 2
    }

    def 'next line with include'() {
        given:

        def includeContent = '''include-1
include-2
'''
        GroovyMock(FileReader, global: true)
        new FileReader(_ as String) >> new StringReader(includeContent)

        def content = '''line1
include::include.adoc[]
line3
line4
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        expect:

        segment.nextLine() == 'line1'
        segment.cursor.lineno == 1

        segment.nextLine() == null
        segment.cursor.lineno == 1

        when:

        def includeSegment = segment.nextSegment
        def continuousSegment = includeSegment.nextSegment

        then:

        includeSegment.cursor.uri == 'include.adoc'
        continuousSegment.cursor.uri == segment.cursor.uri

        expect:

        includeSegment.nextLine() == 'include-1'
        includeSegment.cursor.lineno == 1

        continuousSegment.nextLine() == 'line3'
        continuousSegment.cursor.lineno == 3
    }

    def 'next lines'() {
        given:

        def content = '''line1
line2
line3
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        expect:

        segment.nextLines(2) == ['line1', 'line2']
        segment.cursor.lineno == 2

        segment.nextLines(2) == ['line3']
        segment.cursor.lineno == 3
    }

    def 'next lines with include'() {
        given:

        def includeContent = '''include-1
include-2
'''
        GroovyMock(FileReader, global: true)
        new FileReader(_ as String) >> new StringReader(includeContent)

        def content = '''line1
line2
line3
include::include.adoc[]
line5
line6
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        expect:

        segment.nextLines(2) == ['line1', 'line2']
        segment.cursor.lineno == 2

        segment.nextLines(2) == ['line3']
        segment.cursor.lineno == 3

        when:

        def includeSegment = segment.nextSegment
        def continuousSegment = includeSegment.nextSegment

        then:

        includeSegment.cursor.uri == 'include.adoc'
        continuousSegment.cursor.uri == segment.cursor.uri

        expect:

        includeSegment.nextLine() == 'include-1'
        includeSegment.cursor.lineno == 1

        continuousSegment.nextLine() == 'line5'
        continuousSegment.cursor.lineno == 5
    }

    def 'skip characters'() {
        given:

        def content = '''line1
line2
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        when:
        segment.skipChars(2)

        then:
        segment.cursor.column == 2
        segment.peekLine() == 'ne1'
        segment.nextLine() == 'ne1'
    }

    def 'skip characters more than length'() {
        given:

        def content = '''line1
line2
'''
        def reader = SingleReader.createFromString(content)
        def segment = new BufferSegment(reader)

        when:
        segment.skipChars(10)

        then:
        segment.cursor.column == -1
        segment.peekLine() == ''
        segment.nextLine() == ''
    }
}

