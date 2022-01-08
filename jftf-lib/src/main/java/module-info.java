module jftj.lib.jtest {
    exports jftf.lib.jtest;
    requires jftf.core.daemon;
    requires info.picocli;
    opens jftf.lib.jtest to info.picocli;
    //exports jftf.lib.headers;
}