import org.supermmx.asciidog.AttributeContainer
import org.supermmx.asciidog.Attribute
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.FormattingNode

def attrs = new AttributeContainer()

def header_attributes
def attribute_reference
def cross_reference
def blocks
def section
def paragraph
def ulist
def olist
def list_items
def inline_node
def inline_container
def format_text

header_attributes = { header ->
    header.blocks.each { attr ->
        attrs.setAttribute(attr.name, attr.value)
    }
}

attribute_reference = { attrRef ->
    def name = attrRef.name
    def attr = attrs.getAttribute(name)
    switch (attr.type) {
    case Attribute.ValueType.INLINES:
        attr.value.each { inline ->
            inline_node(inline)
        }
        break
    default:
        yield attr.value
        break
    }
}

cross_reference = { xref ->
    a(href: "#${xref.xrefId}", "${doc.references[(xref.xrefId)].title}")
}

paragraph = { Paragraph para ->
    p { inline_container(para) }
    newLine()
}

blocks = { blks ->
    blks.eachWithIndex { block, index ->
        switch (block.type) {
        case Node.Type.SECTION:
            section(block)
            break
        case Node.Type.ORDERED_LIST:
            olist(block)
            break
        case Node.Type.UNORDERED_LIST:
            ulist(block)
            break
        case Node.Type.PARAGRAPH:
            paragraph(block)
            break
        }
    }
}

section = { Section sec ->
    switch (sec.level) {
    case 0:
        h1(id: sec.id, sec.title)
        break
    case 1:
        h2(id: sec.id, sec.title)
        break
    case 2:
        h3(id: sec.id, sec.title)
        break
    case 3:
        h4(id: sec.id, sec.title)
        break
    case 4:
        h5(id: sec.id, sec.title)
        break
    }

    newLine()

    blocks(sec.blocks)
}

olist = { list ->
    ol {
        list_items(list.blocks)
    }

    newLine()
}

ulist = { list ->
    ul {
        list_items(list.blocks)
    }

    newLine()
}

list_items = { items ->
    items.each { item ->
        li {
            blocks(item.blocks)
        }

        newLine()
    }

}

inline_container = { inlineContainer ->
    inlineContainer.inlineNodes.each { inline ->
        inline_node(inline)
    }
}

inline_node = { inline ->
    switch (inline.type) {
    case Node.Type.INLINE_TEXT:
        yield inline.text
        break
    case Node.Type.INLINE_FORMATTED_TEXT:
        format_text(inline)
        break
    case Node.Type.INLINE_ATTRIBUTE_REFERENCE:
        attribute_reference(inline)
        break
    case Node.Type.INLINE_CROSS_REFERENCE:
        cross_reference(inline)
        break
    }
}

format_text = { tfNode ->
    switch (tfNode.formattingType) {
    case FormattingNode.Type.STRONG:
        strong { inline_container(tfNode) }
        break
    case FormattingNode.Type.EMPHASIS:
        em { inline_container(tfNode) }
        break
    }
}

// main content
yieldUnescaped '<!DOCTYPE html>'

newLine()

html {
    head {
        meta(charset: "UTF-8")

        newLine()

        title(doc.header?.title)
    }

    newLine()

    body {
        h1(doc.header?.title)

        header_attributes(doc.header)

        newLine()

        blocks(doc.blocks)
    }
}

