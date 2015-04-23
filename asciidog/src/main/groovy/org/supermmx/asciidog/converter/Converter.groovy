package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * Converter control engine
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class Converter {
    void convert(Document doc) {
        log.info('Converting document and output to console...')

        def writer = new StringWriter()

        convertToHtml(doc, writer)

        println writer.toString()
    }

    void convertToFile(Document doc, String file,
                       String backendId,
                       Map<String, Object> options) {
        // load backend and rendering plugins
        def backend = PluginRegistry.instance.getBackend(backendId)
        if (backend == null) {
            userLog.error("ERROR: Backend ${backendStr} not found")

            return
        }

        def fileObj = new File(file)
        def dir = fileObj.parentFile
        if (!dir.exists()) {
            dir.mkdirs();
        }
        def os = fileObj.newOutputStream()

        def renderer = backend.createRenderer(options)
        renderer.renderDocument(doc, os)

        os.close()
    }

    void convertToHtml(Document doc, Writer writer) {
        
    }
}
