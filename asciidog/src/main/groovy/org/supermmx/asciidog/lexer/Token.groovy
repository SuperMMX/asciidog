package org.supermmx.asciidog.lexer

import org.supermmx.asciidog.reader.Cursor

/**
 * A token from the file or stream, consecutive same characters are combined
 * in the same token, and WHITE_SPACE and TEXT are always combined
 */
class Token {
    static enum Type {
        // White spaces: ' ' or \t
        WHITE_SPACE(' \t'),
        // End of Line
        EOL,
        // End of File
        EOF,
        // ASCII punctuation
        PUNCT('!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~'),
        // all other
        TEXT

        /**
         * Types that need to match against characters classes
         */
        static List<Type> MATCHING_TYPES = [ WHITE_SPACE, PUNCT ]

        /**
         * The character classes to match for the type
         */
        String characterClasses

        Type() {
        }

        Type(String characterClasses) {
            this.characterClasses = characterClasses
        }

        /**
         * Whether the character matches the character classes
         */
        boolean matches(char ch) {
            if (characterClasses == null) {
                return false
            }

            return characterClasses.indexOf((int)ch) >= 0
        }
    }

    /**
     * The token type
     */
    Type type
    /**
     * The token value
     */
    String value

    /**
     * The URI of the input
     */
    String uri
    /**
     * The row where the token is located
     */
    int row
    /**
     * The starting col of the token
     */
    int col

    String toString() {
        def valueStr = value ? "\"${value}\"" : null
        return "Token: [ ($row, $col): $type, ${valueStr} ]"
    }
}
