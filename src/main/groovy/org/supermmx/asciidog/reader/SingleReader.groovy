package org.supermmx.asciidog.reader

/**
 * The real reader for a single file
 */
class SingleReader {
    /**
     * The content reader
     */
    private BufferedReader reader

    String uri
    String content
    int lineno

    static SingleReader createFromString(String content) {
        def reader = new SingleReader()
        reader.initFromString(content)

        return reader
    }

    static SingleReader createFromFile(String file) {
        def reader = new SingleReader()
        reader.initFromFile(file)

        return reader
    }


    protected initFromString(String content) {
        this.content = content
        this.uri = ''
        reader = new BufferedReader(new StringReader(content))
        lineno = 0
    }

    protected initFromFile(String file) {
        this.file = file
        reader = new BufferedReader(new FileReader(file))
        lineno = 0
    }

    void close() {
        reader.close()

        lineno = -1
    }

    String readLine() {
        if (reader == null) {
            return null
        }

        def line = reader.readLine()
        if (line != null) {
            lineno ++
        }

        return line
    }

}
