package org.supermmx.asciidog.reader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * The buffer segment that contains lines from only one file,
 * and stops at the include directive if there is one.
 */
@Slf4j
class BufferSegment {
    static final def INCLUDE_DIRECTIVE_PATTERN = ~'''(?x)
^
(             # 1, whether is comment
  \\\\?
)
include::
(             # 2, include file
  [^\\[]+
)
\\[
(             # 3, attributes
  .*
)
\\]
$
'''

    public static final int DEFAULT_BUFFER_SIZE = 256

    /**
     * next segment that contains the continuous lines
     */
    BufferSegment nextSegment
    /**
     * The real reader that this buffer segment is associated with
     */
    SingleReader reader
    /**
     * The cursor of the line that is just read
     */
    Cursor cursor

    // whether the lines has include directive,
    // true if a include directive is processed,
    // and the buffer will not be updated.
    private boolean hasInclude = false
    // include directive metadata
    private String uri
    private String attrs
    private boolean isComment

    private List<String> lines = []

    int bufferSize = DEFAULT_BUFFER_SIZE

    BufferSegment(SingleReader reader) {
        this.reader = reader

        cursor = new Cursor(uri: reader.uri,
                            lineno: reader.lineno)
    }

    String peekLine() {
        def line = null
        def retLines = peekLines(1)

        if (retLines.size() > 0) {
            line = retLines[0]
        }

        return line
    }

    String nextLine() {
        def line = peekLine()

        if (lines.size() > 0) {
            lines.remove(0)
            cursor.lineno ++
        }

        return line
    }

    String[] peekLines(int size) {
        // only read more data when there is no include directive,
        // and there is not enough data
        if (!hasInclude && lines.size() < size) {
            readMoreLines()
        }

        if (lines.size() <= size) {
            if (hasInclude) {
                processIncludeDirective()
            }

            if (lines.size() == 0) {
                return []
            } else {
                return lines[0..-1]
            }
        }

        return lines[0..(size - 1)]
    }

    String[] nextLines(int size) {
        def retLines = peekLines(size)

        (retLines.size()).times {
            lines.remove(0)

            cursor.lineno ++
        }

        return retLines
    }

    /**
     * Read more lines to fill the buffer
     */
    protected void readMoreLines() {
        // will not read more if include directive is processed 
        if (hasInclude) {
            return
        }

        if (reader.isClosed()) {
            return
        }

        // fill the buffer
        for (int i = 0; i < bufferSize - lines.size(); i ++) {
            def line = readNextLine()

            if (line == null) {
                break
            }
            lines.add(line)
        }
    }

    /**
     * Read the next line from the reader
     */
    protected String readNextLine() {
        if (reader.isClosed()) {
            return null
        }

        String line = reader.readLine()

        if (line == null) {
            // end of the file

            reader.close()

            return line
        }

        // check wether the next line is include
        (uri, attrs, isComment) = isInclude(line)

        if (uri != null) {
            if (isComment) {
                // comment
                if (attrs == null) {
                    attrs = ''
                }
                line = "include::$uri[$attrs]"
            } else {
                hasInclude = true

                line = null
            }
        }

        return line
    }

    /**
     * Process the include directive
     */
    protected void processIncludeDirective() {
        // TODO: check the file existence
        // TODO: process attributes, like lines or tags

        // process include directive

        // create the new segment for included uri
        def includeReader = SingleReader.createFromFile(uri)
        def includeSegment = new BufferSegment(includeReader)

        // create the segment for the same file
        // after the include directive
        def continuousSegment = new BufferSegment(reader)

        // set up the links correctly
        continuousSegment.nextSegment = nextSegment
        nextSegment = includeSegment
        includeSegment.nextSegment = continuousSegment
    }

    /**
     * Check whether the line represents a include directive or
     * a include directive comment
     *
     * @return uri
     *         attributes
     *         isComment
     */
    protected static List isInclude(String line) {
        if (line == null) {
            return [ null, null, true ]
        }

        def m = INCLUDE_DIRECTIVE_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ null, null, true ]
        }

        def isComment = (m[0][1] != '')
        def file = m[0][2]
        def attributes = m[0][3]

        return [ file, attributes, isComment ]
    }
}
