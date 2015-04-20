package org.supermmx.asciidog.ast;

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Document header.  The children blocks are attributes
 */
@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
class Header extends Block {
    String title
    List<Author> authors = []
    Revision revision

    Author getAuthor() {
        authors[0]
    }
}
