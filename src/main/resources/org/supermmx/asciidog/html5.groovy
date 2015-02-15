import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section

def blocks
def section
def paragraph
def ulist
def olist
def list_items

paragraph = { Paragraph para ->
    p(para.lines.join('\n'))
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
        }
    }
}

section = { Section sec ->
    h2(sec.title)

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

// main content
yieldUnescaped '<!DOCTYPE html>'

newLine()

html {
    head {
        title(doc.header?.title)
    }

    newLine()

    body {
        h1(doc.header?.title)

        newLine()

        blocks(doc.blocks)
    }
}

