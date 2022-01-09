module jftf.demos.main {
    requires jftj.lib;
    requires org.junit.jupiter.api;
    exports jftf.demos;
    opens jftf.demos to jftj.lib;
}