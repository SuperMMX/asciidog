package org.supermmx.asciidog.backend.html5

import javax.xml.stream.XMLStreamWriter

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Manually pretty print xml
 */
class PrettyPrintHandler implements InvocationHandler {
    private final XMLStreamWriter target;
    private int depth = 0;
    private final Map<Integer, Boolean> hasChildElement = new HashMap<Integer, Boolean>();
    private static final String INDENT_CHAR = "  ";
    private static final String LINEFEED_CHAR = "\n";

    private static def INLINE_ELEMENTS = [
        'a', 'abbr', 'address', 'area', 'audio',
        'b', 'bdi', 'bdo', 'bm', 'br',
        'cite', 'code', 'command',
        'datalist', 'del', 'details', 'dfn',
        'em', 'embed',
        'font',
        'i', 'iframe', 'img', 'input', 'ins',
        'label', 'legend', 'link',
        'kbd',
        'mark', 'meter',
        'nav',
        'object', 'optgroup', 'option',
        'param',
        'q',
        'rp', 'rt', 'ruby',
        's', 'samp', 'script', 'select', 'small', 'source', 'span', 'strong', 'sub', 'summary', 'sup',
        'tbody', 'td', 'time', 'track',
        'u',
        'var', 'video',
        'wbr'
    ]

    PrettyPrintHandler(XMLStreamWriter target) {
        this.target = target;
    }
 
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // FIXME: Not starting a new line for inline elements

        String m = method.getName();

        // Needs to be BEFORE the actual event, so that for instance the
        // sequence writeStartElem, writeAttr, writeStartElem, writeEndElem, writeEndElem
        // is correctly handled
        if ("writeStartElement".equals(m)) {
            // update state of parent node
            if (depth > 0) {
                hasChildElement.put(depth - 1, true);
            }

            // reset state of current node
            hasChildElement.put(depth, false);

            if (!INLINE_ELEMENTS.contains(args[0])) {
                // indent for current depth
                target.writeCharacters(LINEFEED_CHAR);

                target.writeCharacters(repeat(depth, INDENT_CHAR));
            }
            depth++;
        }
        else if ("writeEndElement".equals(m)) {
            depth--;
            if (hasChildElement.get(depth) == true) {
                target.writeCharacters(LINEFEED_CHAR);
                target.writeCharacters(repeat(depth, INDENT_CHAR));
            }
        }
        else if ("writeEmptyElement".equals(m)) {
            if (!INLINE_ELEMENTS.contains(args[0])) {
                // update state of parent node
                if (depth > 0) {
                    hasChildElement.put(depth - 1, true);
                }
                // indent for current depth
                target.writeCharacters(LINEFEED_CHAR);
                target.writeCharacters(repeat(depth, INDENT_CHAR));
            }
        }

        method.invoke(target, args);

        return null;
    }
 
    private String repeat(int d, String s) {
        return s * d
    }
}
