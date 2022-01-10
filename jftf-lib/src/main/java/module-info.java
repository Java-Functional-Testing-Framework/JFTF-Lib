module jftj.lib {
    exports jftf.lib.core;
    exports jftf.lib.tools.annotations;
    exports jftf.lib.core.runner;
    exports jftf.lib.core.computer;
    exports jftf.lib.core.meta;
    requires jftf.core;
    requires info.picocli;
    requires java.sql;
    requires org.reflections;
    requires com.google.common;
    opens jftf.lib.core to info.picocli;
}