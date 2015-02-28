package org.supermmx.asciidog

import org.supermmx.asciidog.reader.BufferSegment
import org.supermmx.asciidog.reader.Cursor
import org.supermmx.asciidog.reader.SingleReader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
class Reader {
    Cursor cursor

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
        init(SingleReader.createFromFile(file))
    }

    private initFromString(String content) {
        init(SingleReader.createFromString(content))
    }

    private init(SingleReader reader) {
        segment = new BufferSegment(reader)
        cursor = segment.cursor
    }

    String nextLine() {
        def line = null

        while (segment != null
               && (line = segment.nextLine()) == null) {
            segment = segment.nextSegment

            if (segment != null) {
                cursor = segment.cursor
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

        int totalSize = size
        while (segment != null
               && totalSize > 0) {
            def segmentLines = segment.nextLines(totalSize)

            // not enough data in current segment, try next
            if (segmentLines.size() < totalSize) {
                segment = segment.nextSegment
                if (segment != null) {
                    cursor = segment.cursor
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
}
