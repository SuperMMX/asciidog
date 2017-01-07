package org.supermmx.asciidog.plugin

/**
 * The abstract class for parser plugin
 */
abstract class ParserPlugin extends Plugin {
    ParserPlugin() {
        type = Type.PARSER
    }

    /**
     * Parse general attributes, not the document attributes.
     *
     * @param text the attributes text with []
     */
    protected static Map<String, String> parseAttributes(String text) {
        def line = text

        def attrs = [:] as LinkedHashMap<String, String>

        // size of the attribute line
        def size = line.length()

        def key = null
        def value = null

        def index = 0

        // main loop
        while (index < size) {
            def buf = []

            def quote = null

            def ch = line[index]

            // skip blanks
            while (ch == ' ') {
                index ++
                ch = line[index]
            }

            // starting quote
            if (ch == "'" || ch == '"') {
                quote = ch
                index ++

                ch = ''
            }

            // find the key or value in quotes or not
            while (index < size) {
                ch = line[index]

                if (quote == null) {
                    if (',='.indexOf(ch) >= 0) {
                        // not in quotes, and is a delimiter
                        break
                    }
                }

                // ending quote
                if ((ch == "'" || ch == '"')
                    && ch == quote) {
                    index ++
                    ch = ''
                    break
                }

                buf << ch
                ch = ''

                index ++
            }

            // join the characters
            def str = buf.join('')

            // trim the value if not in quote
            if (quote == null) {
                str = str.trim()
            } else {
                // skip all blanks after the quote
                while (index < size) {
                    ch = line[index]
                    if (ch == ' ') {
                        index ++
                    } else {
                        break
                    }
                }

                quote = null
            }

            if (index >= size) {
                ch = ','
            } else {
                ch = line[index]
            }

            if (ch == ',') {
                // end of the value
                // an attribute defintion is over

                if (key == null) {
                    key = str
                } else {
                    value = str
                }

                // add the attribute
                attrs[(key)] = value

                // reset
                key = null
                value = null
            } else if (ch == '=') {
                // end of the key
                key = str
            } else {
                // invalid
            }

            if (index < size) {
                index ++
            }
        }

        return attrs
    }

}
