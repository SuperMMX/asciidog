package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.converter.Converter
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j(category='AsciiDog')
class AsciiDog {
    static void main(String[] args) {
        if (args.length < 2) {
            log.error('Usage: AsciiDog <Input File> <Output File>')
            return
        }

        String file = args[0]
        String output = args[1]
        log.info("AsciiDoc Input File: {}", file)
        log.info("Converted Output File: {}", output)

        Parser parser = new Parser()
        Document doc = parser.parseFile(file)

        Converter converter = new Converter()

        PluginRegistry.instance.backends.each { id, backend ->
            converter.convertToFile(doc, output + backend.ext, id, [:])
        }
    }
}
