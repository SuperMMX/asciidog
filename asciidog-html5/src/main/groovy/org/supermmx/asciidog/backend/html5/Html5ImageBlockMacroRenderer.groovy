package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.BlockMacro
import org.supermmx.asciidog.backend.AbstractBlockMacroRenderer
import org.supermmx.asciidog.converter.DocumentContext

import java.nio.file.Files
import java.nio.file.Paths

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * The renderer for image block macro
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class Html5ImageBlockMacroRenderer extends AbstractBlockMacroRenderer {
    Html5ImageBlockMacroRenderer() {
        name = 'image'
    }

    @Override
    void doRender(DocumentContext context, Node node) {
        BlockMacro macroNode = (BlockMacro)node

        context.writer.writeStartElement('img')

        def path = macroNode.target

        // consider attribute "imagesdir"
        def imagesdir = context.attrContainer['imagesdir']
        if (imagesdir != null) {
            path = "${imagesdir}/${macroNode.target}"
        }

        // TODO: consider absolute path and URI
        context.writer.writeAttribute('src', path)

        context.writer.writeEndElement()

        // copy the image to the destination, can't do this after rendering
        // because the AST itself may not have the correct imagesdir value
        def inputDir = context.document.attrs.inputFile?.parentFile
        if (inputDir != null) {
            def imageFile = new File(context.outputDir, path)
            if (!imageFile.parentFile.exists()) {
                imageFile.parentFile.mkdirs()
            }

            Files.copy(inputDir.toPath().resolve(path), imageFile.toPath())
        }
    }
}
