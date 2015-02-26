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

    Cursor cursor

    // whether the lines has include directive,
    // true if a include directive is processed,
    // and the buffer will not be updated.
    private boolean hasInclude = false

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
        } else {
            cursor.lineno = -1
        }

        return line
    }

    String[] peekLines(int size) {
        if (!hasInclude && lines.size() < size) {
            readMoreLines()
        }

        if (lines.size() <= size) {
            return lines
        }

        return lines[0..(size - 1)]
    }

    protected void readMoreLines() {
        // will not read more if include directive is processed 
        if (hasInclude) {
            return
        }

        // fill the buffer
        (bufferSize - lines.size()).times {
            def line = readNextLine()

            if (line == null) {
                return
            }
            lines.add(line)
        }
    }

    protected String readNextLine() {
        String line = reader.readLine()

        if (line == null) {
            // end of the file

            reader.close()

            return line
        }

        // check wether the next line is include
        def (file, attributes, isComment) = isInclude(line)

        if (file != null) {
            if (isComment) {
                // comment
                if (attributes == null) {
                    attributes = ''
                }
                line = "include::${file}[${attributes}]"
            } else {
                // TODO: process when the include line is actually peeked

                // TODO: check the file existence
                
                // process include directive
                def includeReader = SingleReader.createFromFile(file)
                BufferSegment includeSegment = new BufferSegment(includeReader)
                

                BufferSegment continuousSegment = new BufferSegment(reader)
                continuousSegment.nextSegment = nextSegment
                nextSegment = includeSegment
                includeSegment.nextSegment = continuousSegment

                line = null
            }
        }

        return line
    }

    /**
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
