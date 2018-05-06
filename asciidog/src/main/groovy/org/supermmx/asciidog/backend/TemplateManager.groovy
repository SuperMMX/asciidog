package org.supermmx.asciidog.backend

import org.supermmx.asciidog.Subtype
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.text.GStringTemplateEngine

import groovy.util.logging.Slf4j

@Slf4j
@Singleton
class TemplateManager {
    private Map<String, List<String>> templatePaths = [:]

    private GStringTemplateEngine templateEngine = new GStringTemplateEngine()
    private Map<String, Template> templateMap = [:]

    void registerTemplateDirectory(String backendId, String path, boolean isClasspath) {
        def paths = templatePaths[backendId]
        if (paths == null) {
            paths = []
            templatePaths[backendId] = paths
        }

        if (isClasspath && !path.startsWith('/')) {
            path = '/' + path
        }

        paths.add(0, path)
    }

    Template getTemplate(DocumentContext context, Node node, String suffix, String ext) {
        def backend = context.backend

        def type = node.type
        def subtype = ''

        if (node in Subtype) {
            subtype = node.subtype
        }

        if (suffix == null) {
            suffix = ''
        }

        def key = "${backend.id}.${type}.${subtype}.${suffix}"
        log.trace '==== Get template with key = {}', key

        def template = templateMap[key]
        if (template != null || templateMap.containsKey(key)) {
            return template
        }

        def list = [ type, subtype, suffix ]
        def templateName = list.collect { it.toString() }.findAll { it.length() > 0 }.join("_")
        templateName = "${templateName}${backend.templateExt}"

        // find the template file from registered directory or classpath
        // TODO: user defined template directories
        def templateContent = null
        def pathList = templatePaths[backend.id]
        for (def path: pathList) {
            def url = this.class.getResource("${path}${templateName}")
            if (url != null) {
                log.trace '==== Template {} found at {}', templateName, url

                templateContent = url.text
                break
            }
        }

        if (templateContent == null) {
            log.trace '==== Template {} not found', templateName
            // make sure null value is added
            templateMap.put(key, null)
            return null
        }

        template = templateEngine.createTemplate(templateContent)

        templateMap[key] = template

        return template
    }
}
