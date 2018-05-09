package org.supermmx.asciidog.backend

import org.supermmx.asciidog.Subtype
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.text.GStringTemplateEngine

import groovy.util.logging.Slf4j

/**
 * The template manager manages the templates
 */
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

        // TODO: search parent backend

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

    /**
     * Trim the leading and trailing new lines for the rendering result
     * of all inline nodes and training new lines for pre inline containers
     *
     * @param content the content of the rendering result
     * @param node the node rendered
     * @param suffix the suffix of the template
     *
     * @return the trimmed result
     */
    static String trim(String content, Node node, String suffix) {
        int length = content.length()

        int startIndex = 0
        // remove the leading newlines for inline nodes
        if (node in Inline) {
            while (startIndex < length
                   && content[startIndex] == ('\n' as char)) {
                startIndex ++
            }
        }

        int endIndex = length
        // remove tailing newlines for inlines and inline contains with pre
        if ((node in Inline)
            || (node in InlineContainer && suffix == 'pre')) {
            while (endIndex > startIndex
                   && content[endIndex - 1] == ('\n' as char)) {
                endIndex --
            }
        }

        content = content.substring(startIndex, endIndex)

        return content
    }
}
