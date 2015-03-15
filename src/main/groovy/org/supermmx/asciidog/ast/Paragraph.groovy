package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical()
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)

class Paragraph extends Block implements InlineContainer  {
    Paragraph() {
        type = Node.Type.PARAGRAPH
    }
}
