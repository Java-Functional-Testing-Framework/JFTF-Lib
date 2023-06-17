module jftf.core {
    exports jftf.core;
    exports jftf.core.daemon;
    exports jftf.core.logging;
    exports jftf.core.ioctl;
    exports jftf.core.api;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
    requires java.logging;
    requires org.apache.commons.configuration2;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
}