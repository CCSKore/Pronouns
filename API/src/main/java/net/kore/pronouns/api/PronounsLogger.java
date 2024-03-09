package net.kore.pronouns.api;

import java.util.logging.Logger;

public class PronounsLogger {
    private static boolean isL4J = false;
    private static Logger javaLogger = null;
    private static org.apache.logging.log4j.Logger l4JLogger = null;

    public static void setLogger(Logger logger) {
        javaLogger = logger;
    }

    public static void setLogger(org.apache.logging.log4j.Logger logger) {
        l4JLogger = logger;
        isL4J = true;
    }

    public static void info(String info) {
        if (isL4J) {
            l4JLogger.info(info);
        } else if (javaLogger != null) {
            javaLogger.info(info);
        } else {
            throw new IllegalStateException("Logger not ready.");
        }
    }

    public static void warn(String info) {
        if (isL4J) {
            l4JLogger.warn(info);
        } else if (javaLogger != null) {
            javaLogger.warning(info);
        } else {
            throw new IllegalStateException("Logger not ready.");
        }
    }

    public static void error(String info) {
        if (isL4J) {
            l4JLogger.error(info);
        } else if (javaLogger != null) {
            javaLogger.severe(info);
        } else {
            throw new IllegalStateException("Logger not ready.");
        }
    }

    public static void debug(String info) {
        if (PronounsConfig.get().node("debug-logging").getBoolean(false)) {
            if (isL4J) {
                l4JLogger.info(info);
            } else if (javaLogger != null) {
                javaLogger.info(info);
            } else {
                throw new IllegalStateException("Logger not ready.");
            }
        }
    }
}
