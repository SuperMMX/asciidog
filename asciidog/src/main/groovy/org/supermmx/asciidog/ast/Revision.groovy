package org.supermmx.asciidog.ast;

class Revision extends Block {
    String revnumber
    String revdate
    String revremark

    Revision() {
        type = Node.Type.REVISION
    }
}
