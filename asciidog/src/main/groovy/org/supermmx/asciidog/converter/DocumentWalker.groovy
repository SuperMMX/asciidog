package org.supermmx.asciidog.converter

import org.supermmx.asciidog.Attribute
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.backend.Backend

import groovy.util.logging.Slf4j

@Slf4j
class DocumentWalker {
    void traverse(Document document, Backend backend, OutputStream os) {
        DocumentContext context = new DocumentContext(document: document,
                                                      backend: backend,
                                                      outputStream: os)
        traverseBlock(context, document)
    }

    protected void traverseBlock(DocumentContext context, Block block) {
        def backend = context.backend

        if (block.type.isAction) {
            // action nodes
            switch (block.type) {
            case Node.Type.DEFINE_ATTRIBUTE:
                def attr = (AttributeEntry)block
                context.attrContainer.setAttribute(attr.name, attr.value)
                break
            }

            return
        }

        // get the renderer
        def renderer = backend.getRenderer(block.type)

        if (renderer == null) {
            log.warn "Renderer not found in backend \"${backend.id}\" for node type \"${block.type}\""
            return
        }

        // pre
        renderer.pre(context, block)

        // tranverse the child blocks or inlines

        if (block in InlineContainer) {
            // inline container
            traverseInlineContainer(context, block)
        } else {
            // normal blocks
            block.blocks.each { childBlock ->
                traverseBlock(context, childBlock)
            }
        }

        // post
        renderer.post(context, block)
    }

    protected void traverseInlineContainer(DocumentContext context, InlineContainer container) {
        container.inlineNodes.each { inline ->
            traverseInline(context, inline)
        }
    }

    void traverseInline(DocumentContext context, Inline inline) {
        def backend = context.backend

        // expand the attribute reference
        if (inline.type == Node.Type.ATTRIBUTE_REFERENCE) {
            def name = inline.name
            def attr = context.attrContainer.getAttribute(name)
            if (attr.type == Attribute.ValueType.INLINES) {
                attr.value.each { attrInline ->
                    traverseInline(context, attrInline)
                }
            } else {
                // render the text directly?
            }

            return
        }

        // all inlines should be handled by the renderer plugin

        def renderer = backend.getRenderer(inline.type)

        if (renderer == null) {
            log.warn "Node Renderer not found in backend \"${backend.id}\" for node type \"${inline.type}\""
            return
        }

        renderer.pre(context, inline)

        if (inline in InlineContainer) {
            traverseInlineContainer(context, inline)
        } else {
            renderer = backend.getInlineRenderer(inline.type)
            renderer.render(context, inline)
        }

        renderer.post(context, inline)
    }
}
