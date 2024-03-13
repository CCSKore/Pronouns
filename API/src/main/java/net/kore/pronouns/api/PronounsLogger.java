package net.kore.pronouns.api;

import net.kyori.adventure.text.Component;

import java.util.logging.Logger;

public class PronounsLogger {
    private static Object LOGGER;
    public static void setLogger(Object l) { LOGGER = l; }

    public static void info(String info) {
        try {
            LOGGER.getClass().getMethod("info", String.class).invoke(LOGGER, info);
        } catch (Throwable ee) {
            try {
                LOGGER.getClass().getMethod("info", Component.class).invoke(LOGGER, Component.text(info));
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
                    LOGGER.getClass().getMethod("warn", Component.class).invoke(LOGGER, Component.text(info));
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
                    LOGGER.getClass().getMethod("error", Component.class).invoke(LOGGER, Component.text(info));
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
