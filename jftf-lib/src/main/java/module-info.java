module jftj.lib {
    exports jftf.lib.core;
    exports jftf.lib.tools.annotations;
    requires jftf.core;
    requires info.picocli;
    requires java.sql;
    opens jftf.lib.core to info.picocli;
}