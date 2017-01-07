package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.backend.Backend

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class PluginRegistrySpec extends AsciidogSpec {

    def cleanupSpec() {
        def testDir = new File(this.class.protectionDomain.codeSource.location.path)
        if (testDir.exists()) {
            testDir.delete()
        }
    }

    def 'custom plugins'() {
        given:
        def testDir = new File(this.class.protectionDomain.codeSource.location.path)
        def servicesFile = new File(testDir, 'asciidog.groovy')
        if (!servicesFile.parentFile.exists()) {
            servicesFile.parentFile.mkdirs()
        } else {
            servicesFile.delete()
        }
        servicesFile << '''
import org.supermmx.asciidog.plugin.*
import org.supermmx.asciidog.backend.*

asciidog {
    // the name of the plugin
    name = 'asciidog-test'

    // the customized plugin loader, optioinal
    suite = TestPluginSuite

    backends = [
        TestBackend,
        AnotherBackend
    ]

    // the plugins
    plugins = [
        AnotherPlugin
    ]

    builders = [
    ]
}
'''

        when:
        def registry = new PluginRegistry("test")

        then:
        registry.getPlugin('test-parser-plugin') instanceof TestPlugin
        registry.getPlugin('another-renderer-plugin') instanceof AnotherPlugin

        cleanup:
        servicesFile.delete()
    }
}

class TestPluginSuite extends PluginSuite {
    TestPluginSuite() {
        plugins << new TestPlugin()
    }
}

class TestPlugin extends ParserPlugin {
    TestPlugin() {
        id = 'test-parser-plugin'
    }
}

class AnotherPlugin extends RendererPlugin {
    AnotherPlugin() {
        id = 'another-renderer-plugin'
    }
}
