package org.supermmx.asciidog.reader

import groovy.util.logging.Slf4j

/**
 * The real reader for a single file
 */
@Slf4j
class SingleReader {
    /**
     * The content reader
     */
    private BufferedReader reader

    /**
     * URI of the content, which could be
     * a file, a URL or blank for string.
     */
    String uri
    /**
     * The content of the string the reader is created from a string
     */
    String content
    /**
     * The line number for the line has been read,
     * -1 means the reader is closed
     */
    int lineno

    /**
     * Create the reader from a string
     */
    static SingleReader createFromString(String content) {
        def reader = new SingleReader()
        reader.initFromString(content)

        return reader
    }

    /**
     * Create the reader from a file
     */
    static SingleReader createFromFile(String file) {
        def reader = new SingleReader()
        reader.initFromFile(file)

        return reader
    }

    /**
     * Init the reader from a string
     */
    protected initFromString(String content) {
        this.content = content
        this.uri = ''
        reader = new BufferedReader(new StringReader(content))
        lineno = 0
    }

    /**
     * Init the reader from a file
     */
    protected initFromFile(String file) {
        this.uri = file
        reader = new BufferedReader(new FileReader(file))
        lineno = 0
    }

    /**
     * Close the reader
     */
    void close() {
        reader.close()

        lineno = -1
    }

    /**
     * Whether the reader is closed
     */
    boolean isClosed() {
        return (lineno == -1)
    }

    /**
     * Read the next line
     */
    String readLine() {
        if (reader == null
            || isClosed()) {
            return null
        }

        def line = reader.readLine()
        if (line != null) {
            lineno ++
        }

        return line
    }

}
