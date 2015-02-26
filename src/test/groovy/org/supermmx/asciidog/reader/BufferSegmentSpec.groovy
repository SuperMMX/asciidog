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
}

