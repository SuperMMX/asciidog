package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.Renderer

class PluginRegistrySpec extends AsciidogSpec {

    def cleanupSpec() {
        def testDir = new File(this.class.protectionDomain.codeSource.location.path)
        if (testDir.exists()) {
            testDir.delete()
        }
    }

    def 'backends'() {
        given:
        def testDir = new File(this.class.protectionDomain.codeSource.location.path)
        def servicesFile = new File(testDir, 'META-INF/services/org.supermmx.asciidog.backend.Backend')
        if (!servicesFile.parentFile.exists()) {
            servicesFile.parentFile.mkdirs()
        } else {
            servicesFile.delete()
        }
        servicesFile << 'org.supermmx.asciidog.backend.TestBackend\n'
        servicesFile << 'org.supermmx.asciidog.backend.AnotherBackend\n'

        when:
        def registry = new PluginRegistry("test")

        then:
        registry.backends['test-backend'] instanceof org.supermmx.asciidog.backend.TestBackend
        registry.backends['another-backend'] instanceof org.supermmx.asciidog.backend.AnotherBackend
    }

    def 'custom plugins'() {
        given:
        def testDir = new File(this.class.protectionDomain.codeSource.location.path)
        def servicesFile = new File(testDir, 'META-INF/services/org.supermmx.asciidog.plugin.Plugin')
        if (!servicesFile.parentFile.exists()) {
            servicesFile.parentFile.mkdirs()
        } else {
            servicesFile.delete()
        }
        servicesFile << 'org.supermmx.asciidog.plugin.TestPlugin\n'
        servicesFile << 'org.supermmx.asciidog.plugin.AnotherPlugin\n'

        when:
        def registry = new PluginRegistry("test")

        then:
        registry.getPlugin('test-parser-plugin') instanceof TestPlugin
        registry.getPlugin('another-renderer-plugin') instanceof AnotherPlugin
    }
}

class TestPlugin extends ParserPlugin {
    TestPlugin() {
        id = 'test-parser-plugin'
    }
}

class AnotherPlugin extends ParserPlugin {
    AnotherPlugin() {
        id = 'another-renderer-plugin'
    }
}
