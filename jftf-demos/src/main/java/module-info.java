module jftf.demos {
    requires jftj.lib;
    requires org.junit.jupiter.api;
    exports jftf.demos.BasicTest;
    opens jftf.demos.BasicTest to jftj.lib;
    exports jftf.demos.NegativeTest;
    opens jftf.demos.NegativeTest to jftj.lib;
    exports jftf.demos.PositiveTest;
    opens jftf.demos.PositiveTest to jftj.lib;
    exports jftf.demos.MathTest;
    opens jftf.demos.MathTest to jftj.lib;
    exports jftf.demos.ProductionExample;
    opens jftf.demos.ProductionExample to jftj.lib;
    exports jftf.demos.LiveExample;
    opens jftf.demos.LiveExample to jftj.lib;
}