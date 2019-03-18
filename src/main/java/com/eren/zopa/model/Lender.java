package com.eren.zopa.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
@ToString
public class Lender {
    private String name;
    private BigDecimal rate;
    private BigDecimal amount;
}
