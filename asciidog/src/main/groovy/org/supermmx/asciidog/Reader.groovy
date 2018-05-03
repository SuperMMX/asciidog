package org.supermmx.asciidog

import org.supermmx.asciidog.reader.BufferSegment
import org.supermmx.asciidog.reader.Cursor
import org.supermmx.asciidog.reader.SingleReader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
class Reader {
    File inputFile

    private BufferSegment segment

    static Reader createFromFile(String filename) {
        Reader reader = new Reader()
        reader.initFromFile(filename)

        return reader
    }

    static Reader createFromString(String content) {
        if (content == null) {
            content = ''
        }

        Reader reader = new Reader()
        reader.initFromString(content)

        return reader
    }

    private initFromFile(String file) {
        inputFile = new File(file)

        init(SingleReader.createFromFile(file))
    }

    private initFromString(String content) {
        init(SingleReader.createFromString(content))
    }

    private init(SingleReader reader) {
        segment = new BufferSegment(reader)
    }

    /**
     * Get the current cursor
     */
    Cursor getCursor() {
        return segment.cursor
    }

    String nextLine() {
        def line = null

        while ((line = segment.nextLine()) == null) {
            // end of the file
            // keep the last segment ?
            def nextSegment = segment.nextSegment
            if (nextSegment != null) {
                segment = segment.nextSegment
            } else {
                break
            }
        }

        return line
    }

    String peekLine() {
        def (line) = peekLines(1)

        return line
    }

    /**
     * Read the next number of lines.
     *
     * @param size the number of lines to read
     *
     * @return the next lines, null appended if there are not enough data
     */
    String[] nextLines(int size) {
        def lines = []

        def nextSegment = segment

        int totalSize = size
        while (nextSegment != null
               && totalSize > 0) {
            def segmentLines = segment.nextLines(totalSize)

            // not enough data in current segment, try next
            if (segmentLines.size() < totalSize) {
                // keep the last segment ?
                nextSegment = segment.nextSegment
                if (nextSegment != null) {
                    segment = segment.nextSegment
                }
            }

            lines.addAll(segmentLines)
            totalSize -= segmentLines.length
        }

        totalSize.times {
            lines.add(null)
        }

        return lines
    }

    /**
     * Peek the next number of lines.
     *
     * @param size the number of lines to peek
     *
     * @return the next lines, null appended if there are not enough data
     */
    String[] peekLines(int size) {
        def lines = []

        def seg = segment
        def totalSize = size

        while (seg != null
               && totalSize > 0) {
            def segmentLines = seg.peekLines(totalSize)

            // no data in current segment, try next
            if (segmentLines.size() < totalSize) {
                seg = seg.nextSegment
            }

            lines.addAll(segmentLines)
            totalSize -= segmentLines.length
        }

        totalSize.times {
            lines.add(null)
        }

        return lines
    }

    /**
     * Skip blank lines
     *
     * @return the number of blank lines skipped
     */
    int skipBlankLines() {
        int skipped = 0
        def line = null

        while ((line = peekLine()) != null) {
            if (line.length() == 0) {
                nextLine()
                skipped ++
            } else {
                break
            }
        }

        return skipped
    }

    /**
     * Skip specified number of characters in the current line
     */
    int skipChars(int count) {
        return segment.skipChars(count)
    }

    /**
     * Skip specified number of characters in the current line
     */
    int skipBlanks() {
        return segment.skipBlanks()
    }
}
