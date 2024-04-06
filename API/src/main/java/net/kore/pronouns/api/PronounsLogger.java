package net.kore.pronouns.api;

public class PronounsLogger {
    private static Object LOGGER;

    public static void setLogger(Object l) { LOGGER = l; }

    public static void info(String info) {
        try {
            LOGGER.getClass().getMethod("info", String.class).invoke(LOGGER, info);
        } catch (Throwable ee) {
            try {
                Object c = Class.forName("net.kyori.adventure.text.Component").getMethod("text", String.class).invoke(null, info);
                LOGGER.getClass().getMethod("info", Class.forName("net.kyori.adventure.text.Component")).invoke(LOGGER, c);
            } catch (Throwable e) {
                throw new IllegalStateException("Logger couldn't be executed.", e);
            }
        }
    }

    public static void warn(String info) {
        try {
            LOGGER.getClass().getMethod("warning", String.class).invoke(LOGGER, info);
        } catch (Throwable eee) {
            try {
                LOGGER.getClass().getMethod("warn", String.class).invoke(LOGGER, info);
            } catch (Throwable ee) {
                try {
                    Object c = Class.forName("net.kyori.adventure.text.Component").getMethod("text", String.class).invoke(null, info);
                    LOGGER.getClass().getMethod("warn", Class.forName("net.kyori.adventure.text.Component")).invoke(LOGGER, c);
                } catch (Throwable e) {
                    throw new IllegalStateException("Logger couldn't be executed.", e);
                }
            }
        }
    }

    public static void error(String info) {
        try {
            LOGGER.getClass().getMethod("severe", String.class).invoke(LOGGER, info);
        } catch (Throwable eee) {
            try {
                LOGGER.getClass().getMethod("error", String.class).invoke(LOGGER, info);
            } catch (Throwable ee) {
                try {
                    Object c = Class.forName("net.kyori.adventure.text.Component").getMethod("text", String.class).invoke(null, info);
                    LOGGER.getClass().getMethod("error", Class.forName("net.kyori.adventure.text.Component")).invoke(LOGGER, c);
                } catch (Throwable e) {
                    throw new IllegalStateException("Logger couldn't be executed.", e);
                }
            }
        }
    }

    public static void debug(String info) {
        if (PronounsConfig.get().node("debug-logging").getBoolean(false)) {
            info(info);
        }
    }
}
