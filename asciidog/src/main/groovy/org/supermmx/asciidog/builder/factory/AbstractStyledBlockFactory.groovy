package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.StyledBlock
import org.supermmx.asciidog.ast.OpenBlock

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
abstract class AbstractStyledBlockFactory extends AbstractBlockFactory {
    /*
    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        if (node in OpenBlock) {
            return true
        }

        def hasDelimiter = attributes['hasDelimiter']
        if (hasDelimiter == null) {
            hasDelimiter = true
            attributes['hasDelimiter'] = true
        }

        def isOpenBlock = attributes['isOpenBlock']
        if (isOpenBlock == null) {
            attributes['isOpenBlock'] = !hasDelimiter
        }

        return true
    }
     */
}
