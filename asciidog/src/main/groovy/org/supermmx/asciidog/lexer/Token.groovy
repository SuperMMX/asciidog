package org.supermmx.asciidog.lexer

import org.supermmx.asciidog.reader.Cursor

import groovy.transform.Canonical

import java.util.regex.Pattern

/**
 * A token from the file or stream, consecutive same characters are combined
 * in the same token, and WHITE_SPACE and TEXT are always combined
 */
@Canonical
class Token {
    static enum Type {
        // White spaces: ' ' or \t
        WHITE_SPACES(' \t'),
        DIGITS('0123456789'),
        // End of Line
        EOL(false),
        // Begin of File
        BOF(false),
        // End of File
        EOF(false),
        // ASCII punctuation
        PUNCTS('!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~', false),
        // Unicode punctuation
        OTHER_PUNCTS(~/(?U)\p{IsPunctuation}/, false),
        // all other
        TEXT

        /**
         * Types that need to match against characters classes
         */
        static List<Type> MATCHING_TYPES = [ WHITE_SPACES, DIGITS, PUNCTS, OTHER_PUNCTS ]

        Pattern regex
        /**
         * The character classes to match for the type
         */
        String characterClasses
        /**
         * Whether to combine different characters
         */
        boolean combining = true

        Type() {
        }

        Type(boolean combining) {
            this.combining = combining
        }

        Type(String characterClasses) {
            this(characterClasses, true)
        }

        Type(String characterClasses, boolean combining) {
            this.characterClasses = characterClasses
            this.combining = combining
        }

        Type(Pattern regex, boolean combining) {
            this.regex = regex
            this.combining = combining
        }

        /**
         * Whether the character matches the character classes
         */
        boolean matches(char ch) {
            if (characterClasses != null) {
                return characterClasses.indexOf((int)ch) >= 0
            }

            if (regex != null) {
                return regex.matcher(ch.toString()).matches()
            }

            return false
        }
    }

    /**
     * The token index in the token stream
     */
    int index
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
        def valueStr = value == '\n' ? '\\n' : value
        valueStr = value ? "\"${valueStr}\"" : null
        return "Token: [ $index: ($row, $col): $type, ${valueStr} ]"
    }
}
