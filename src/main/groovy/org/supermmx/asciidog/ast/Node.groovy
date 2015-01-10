package org.supermmx.asciidog.ast

class Node {
    String id
    Map<String, String> attributes = [:]
    Node parent

    Document document
}
