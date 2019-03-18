package com.eren.zopa.model;

import java.math.RoundingMode;

public class AppConfig {
    public interface Money {
        public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;
        public static final int MONEY_SCALE = 2;
        public static final int RATE_SCALE = 1;
        public static final int ZERO_SCALE = 0;
        public static final String POUND_SIGN = "£";
        public static final String PERCENT_SIGN = "£";
    }

    public interface Loan {
        public static final int LOAN_PERIOD_MONTHS = 36;
    }


}
