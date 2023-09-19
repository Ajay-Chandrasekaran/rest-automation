package com.spiro.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class PaymentHistoryList {
    private List<Payment> history;

    public PaymentHistoryList() {
        this.history = new ArrayList<>();
    }
}
