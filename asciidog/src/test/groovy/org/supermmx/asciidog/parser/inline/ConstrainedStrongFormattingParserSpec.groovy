package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.AsciidogSpec

class ConstrainedStrongFormattingParserSpec extends AsciidogSpec {
    /* === Strong Constrained === */
    def 'regex: single-line constrained strong string'() {
        given:
        def text = '*a few strong words*'

        expect:
        ConstrainedStrongFormattingParser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: escaped single-line constrained strong string'() {
        given:
        def text = '\\*a few strong words*'

        expect:
        ConstrainedStrongFormattingParser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: multi-line constrained strong string'() {
        given:
        def text = '*a few\nstrong words*'

        expect:
        ConstrainedStrongFormattingParser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: constrained strong string containing an asterisk'() {
        given:
        def text = '*bl*ck*-eye'

        expect:
        ConstrainedStrongFormattingParser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: unconstrained strong string should not match constrained strong'() {
        given:
        def text = '**bl*ck**-eye'

        expect:
        ConstrainedStrongFormattingParser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == false
    }

    def 'regex: constrained strong string containing an asterisk and multibyte word chars'() {
        given:
        def text = '*黑*眼圈*'

        expect:
        ConstrainedStrongFormattingParser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: constrained strong string with role'() {
        given:
        def text = '[blue]*a few strong words*'

        expect:
        ConstrainedStrongFormattingParser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: escaped constrained strong string with role'() {
        given:
        def text = '\\[blue]*a few strong words*'

        expect:
        ConstrainedStrongFormattingParser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }
}
