module jftf.core.daemon {
    exports jftf.core.daemon;
    exports jftf.core.logging;
    exports jftf.core.ioctl;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
}