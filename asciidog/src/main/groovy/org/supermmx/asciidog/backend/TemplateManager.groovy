package org.supermmx.asciidog.backend

import org.supermmx.asciidog.Subtype
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext
import org.supermmx.asciidog.plugin.PluginRegistry

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
    /**
     * Template paths for backends
     */
    private Map<String, List<String>> templatePaths = [:]

    /**
     * The template engine
     */
    private GStringTemplateEngine templateEngine = new GStringTemplateEngine()
    /**
     * The template cache
     */
    private Map<String, Template> templateMap = [:]

    /**
     * Register the template path for a backend
     *
     * @param backendId the backend id
     * @param path the path that contains the templates
     * @param isClasspath whether the path is in the classpath or is a directory
     */
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

    /**
     * Recursively get the template of current backend for the specified node
     *
     * @param context current document context
     * @param node the node to get the template for
     * @param suffix pre, post or blank
     *
     * @return the template
     */
    Template getTemplate(DocumentContext context, Node node, String suffix) {
        def template = getTemplate(context, context.backend, node, suffix, context.backend.templateExt)

        return template
    }

    /**
     * Get the template of the specified backend for the specified node
     */
    protected Template getTemplate(DocumentContext context, AbstractTemplateBackend backend,
                                   Node node, String suffix, String ext) {
        def type = node.type
        def subtype = ''

        if (node in Subtype) {
            subtype = node.subtype
        }

        if (suffix == null) {
            suffix = ''
        }

        def key = "${backend.id}.${type}.${subtype}.${suffix}".toString()

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
                log.trace '==== Backend: {}, template {} found at {}', backend.id, templateName, url

                templateContent = url.text
                break
            }
        }

        // search the parent backend
        if (templateContent == null) {
            log.trace '==== Backend: {}, template {} not found', backend.id, templateName

            if (backend.parentId != null) {
                log.trace '==== Backend: {}, try to find template {} from parent {}', backend.id, templateName, backend.parentId
                def parentBackend = PluginRegistry.instance.getBackend(backend.parentId)
                template = getTemplate(context, parentBackend, node, suffix, parentBackend.templateExt)
            }

            log.trace '==== Backend: {}, template {} found from parent {}: {}, key = {}', backend.id, templateName, backend.parentId, template, key
            templateMap.put(key, template)

            return template
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
