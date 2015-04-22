package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.Renderer

class PluginRegistrySpec extends AsciidogSpec {
    def 'backend plugins'() {
        given:
        def testDir = new File(this.class.protectionDomain.codeSource.location.path)
        def servicesFile = new File(testDir, 'META-INF/services/org.supermmx.asciidog.backend.Backend')
        if (!servicesFile.parentFile.exists()) {
            servicesFile.parentFile.mkdirs()
        } else {
            servicesFile.delete()
        }
        servicesFile << 'org.supermmx.asciidog.plugin.TestBackend\n'
        servicesFile << 'org.supermmx.asciidog.plugin.AnotherBackend\n'

        expect:
        PluginRegistry.instance.backends['test-backend'].getClass().name == 'org.supermmx.asciidog.plugin.TestBackend'
        PluginRegistry.instance.backends['another-backend'].getClass().name == 'org.supermmx.asciidog.plugin.AnotherBackend'
    }
}

class TestBackend extends AbstractBackend {
    TestBackend() {
        id = 'test-backend'
    }

    Renderer createRenderer(Map<String, Object> options) {
        return null
    }
}

class AnotherBackend extends AbstractBackend {
    AnotherBackend() {
        id = 'another-backend'
    }

    Renderer createRenderer(Map<String, Object> options) {
        return null
    }
}
