module jftf.demos {
    requires jftf.lib;
    requires org.junit.jupiter.api;
    exports jftf.demos.BasicTest;
    opens jftf.demos.BasicTest to jftf.lib;
    exports jftf.demos.NegativeTest;
    opens jftf.demos.NegativeTest to jftf.lib;
    exports jftf.demos.PositiveTest;
    opens jftf.demos.PositiveTest to jftf.lib;
    exports jftf.demos.MathTest;
    opens jftf.demos.MathTest to jftf.lib;
    exports jftf.demos.ProductionExample;
    opens jftf.demos.ProductionExample to jftf.lib;
    exports jftf.demos.LiveExample;
    opens jftf.demos.LiveExample to jftf.lib;
}