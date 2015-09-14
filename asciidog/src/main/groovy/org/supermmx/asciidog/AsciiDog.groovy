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
            log.error('Usage: AsciiDog <Input File> <Output Directory> <Base Name>')
            return
        }

        String file = args[0]
        String output = args[1]
        String base = null
        if (args.length >= 3) {
            base = args[2]
        } else {
            base = file.split("\\.")[0]
        }

        log.info("AsciiDoc Input File: {}", file)
        log.info("Converted Output Directory: {}", output)
        log.info("Converted Output Base Name: {}", base)

        Parser parser = new Parser()
        Document doc = parser.parseFile(file)

        Converter converter = new Converter()

        PluginRegistry.instance.backends.each { id, backend ->
            converter.convertToFile(doc, output, id,
                                    ['base': base])
        }
    }
}
