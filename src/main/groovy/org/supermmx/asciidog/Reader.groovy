package org.supermmx.asciidog

class Reader {
    public static final int DEFAULT_BUFFER_SIZE = 1000

    private BufferedReader reader
    // lines buffer
    private List<String> lines

    // buffer size
    int bufferSize = DEFAULT_BUFFER_SIZE

    // current line
    String line
    
    static Reader createFromFile(String filename) {
        null;
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
        lines = []
    }

    void close() {
        reader.close()
    }

    String nextLine() {
        line = peekLine()

        if (line != null) {
            lines.remove(0)
        }
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

    String[] nextLines(int size) {
        println "orig lines = $lines"
        String[] nextLines = peekLines(size)
        println "lines red = $nextLines"
        nextLines.size().times {
            lines.remove(0)
        }
        println "new lines = $lines"

        return nextLines
    }

    String[] peekLines(int size) {
        if (lines.size() < size) {
            readMoreLines()
        }

        if (lines.size() == 0) {
            return []
        } else {
            return lines[0..Math.min(size - 1, lines.size() - 1)]
        }
    }

    private void readMoreLines() {
        (bufferSize - lines.size()).times {
            String nextLine = reader.readLine()
            if (nextLine == null) {
                return
            }
            lines.add(nextLine)
        }
    }
}
