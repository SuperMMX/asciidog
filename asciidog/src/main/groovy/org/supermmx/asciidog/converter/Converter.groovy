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

    /**
     * Convert the input file and save to the specified output directory
     *
     * @param doc the input document
     * @param dir the output directory
     * @param backendId the backend id
     * @param options the options for the backend
     */
    void convertToFile(Document doc, String dir,
                       String backendId,
                       Map<String, Object> options) {
        // load backend and rendering plugins
        def backend = PluginRegistry.instance.getBackend(backendId)
        if (backend == null) {
            userLog.error("ERROR: Backend ${backendId} not found")

            return
        }

        def dirObj = new File(dir)
        if (!dirObj.exists()) {
            dirObj.mkdirs();
        }

        def context = new DocumentContext(document: doc,
                                          backend: backend)
        context.attrContainer.attributes.putAll(doc.attrs.attributes)

        // set different for different backends
        context.outputDir = new File(dirObj, backend.id)
        if (!context.outputDir.exists()) {
            context.outputDir.mkdirs();
        }

        // convert all options to document attributes
        context.attrContainer.removeSystemAttributes()
        options.each { k, v ->
            context.attrContainer.setSystemAttribute(k, v)
        }

        def walker = new DocumentWalker()
        walker.traverse(doc, backend, context)
    }

    void convertToHtml(Document doc, Writer writer) {
    }
}
