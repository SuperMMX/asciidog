package org.supermmx.asciidog

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
class Reader {
    public static final int DEFAULT_BUFFER_SIZE = 1000

    static class Cursor {
        int lineno

        Cursor() {
            lineno = 1
        }

        String toString() {
            return "line ${lineno}"
        }
    }

    private BufferedReader reader
    // lines buffer
    private List<String> lines
    // buffer size
    private int bufferSize = DEFAULT_BUFFER_SIZE

    // the cursor pointing at next line
    Cursor cursor

    static Reader createFromFile(String filename) {
        Reader reader = new Reader(new BufferedReader(new FileReader(filename)))

        return reader
    }

    static Reader createFromString(String content) {
        if (content == null) {
            content = ''
        }

        Reader reader = new Reader(new BufferedReader(new StringReader(content)))

        return reader
    }

    private Reader(BufferedReader reader) {
        this.reader = reader
        lines = [] as List<String>
        cursor = new Cursor()
    }

    void close() {
        reader.close()
    }

    String nextLine() {
        def line = peekLine()

        if (line != null) {
            cursor.lineno ++
            lines.remove(0)
        }

        return line
    }

    String peekLine() {
        if (lines.size() == 0) {
            readMoreLines()
        }

        if (lines.size() == 0) {
            return null
        } else {
            return lines[0]
        }
    }

    /**
     * Read the next number of lines.
     *
     * @param size the number of lines to read
     *
     * @return the next lines, null appended if there are not enough data
     */
    String[] nextLines(int size) {
        String[] nextLines = peekLines(size)

        boolean noData = false
        if (lines.size() < nextLines.size()) {
            noData = true
        }

        Math.min(nextLines.size(), lines.size()).times {
            lines.remove(0)
            cursor.lineno ++
        }

        if (noData) {
            cursor.lineno = -1
        }

        return nextLines
    }

    /**
     * Peek the next number of lines.
     *
     * @param size the number of lines to peek
     *
     * @return the next lines, null appended if there are not enough data
     */
    String[] peekLines(int size) {
        if (lines.size() < size) {
            readMoreLines()
        }

        if (lines.size() >= size) {
            return lines[0..(size - 1)]
        } else {
            return lines + (1..(size - lines.size())).collect { null }
        }
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

    private void readMoreLines() {
        log.debug('Reading more lines from file, lines to read: {}',
                  bufferSize - lines.size())

        (bufferSize - lines.size()).times {
            String nextLine = reader.readLine()
            if (nextLine == null) {
                return
            }
            lines.add(nextLine)
        }
    }
}
