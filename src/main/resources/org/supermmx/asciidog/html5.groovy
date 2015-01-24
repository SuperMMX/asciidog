import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section

def blocks
def section
def paragraph

paragraph = { Paragraph para ->
    p(para.lines.join('\n'))
    newLine()
}

blocks = { blks ->
    blks.eachWithIndex { block, index ->
        if (block instanceof Section) {
            section(block)
        } else if (block instanceof Paragraph){
            paragraph(block)
        }
    }
}

section = { Section sec ->
    h2(sec.title)

    newLine()

    blocks(sec.blocks)
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

