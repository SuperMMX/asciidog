import ch.qos.logback.classic.Level

setupAppenders()

setupLoggers()

def setupAppenders() {
    appender("STDOUT", ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
    }
}

def setupLoggers() {
    def pattern = ~'(.+)\\.level'

    def rootLevel = INFO

    // logger "AsciiDog" is for user messages

    // looking for *.level in system properties
    System.properties.each { k, v ->
        def m = pattern.matcher(k)
        if (!m.matches()) {
            return
        }

        def loggerName = m[0][1]
        def level = Level.valueOf(v)
        if (loggerName == 'root') {
            rootLevel = level
        } else {
            logger(loggerName, level)
        }
    }
    root(rootLevel, ['STDOUT'])
}
