import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.FormattingNode

def blocks
def section
def paragraph
def ulist
def olist
def list_items
def inline_node
def inline_container
def format_text

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
        h1(sec.title)
        break
    case 1:
        h2(sec.title)
        break
    case 2:
        h3(sec.title)
        break
    case 3:
        h4(sec.title)
        break
    case 4:
        h5(sec.title)
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

        newLine()

        blocks(doc.blocks)
    }
}

