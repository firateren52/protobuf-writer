package com.eren.zopa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.MessageFormat;

import static com.eren.zopa.model.AppConfig.Money.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Quote {
    private final BigDecimal amount;
    private final BigDecimal rate;
    private final BigDecimal monthlyRepayment;
    private final BigDecimal totalRepayment;

    @Override
    public String toString() {
        MessageFormat messageFormat = new MessageFormat("Requested amount: {0}{1}Rate: {2}{3}Monthly repayment: {4}{5}Total repayment: {6}");
        Object[] params = new Object[]{printMoney(amount.setScale(ZERO_SCALE)), System.lineSeparator(), printRate(rate.setScale(RATE_SCALE, DEFAULT_ROUNDING)), System.lineSeparator(),
                printMoney(monthlyRepayment.setScale(MONEY_SCALE, DEFAULT_ROUNDING)), System.lineSeparator(), printMoney(totalRepayment.setScale(MONEY_SCALE, DEFAULT_ROUNDING))};
        return messageFormat.format(params);
    }

    private String printMoney(BigDecimal money) {
        //TODO(firat.eren) add £ to constants
        return POUND_SIGN + money;
    }

    private String printRate(BigDecimal rate) {
        return rate + PERCENT_SIGN;
    }
}
