package org.supermmx.asciidog.ast;

class Header extends Block {
    String title
    List<Author> authors
    Revision revision

    Author getAuthor() {
        authors[0]
    }
}
