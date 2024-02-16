package com.example.andriodmidterm;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    public enum TransactionType { DEPOSIT, WITHDRAWAL, TRANSFER}
    private double amountChange;
    private double newBalance;
    private String transactionMessage;
    private TransactionType transactionType;
    private Date date;
    private String transactionOwner;

    public Transaction(double amountChange, double newBalance, String transactionMessage, TransactionType transactionType, String transactionOwner) {
        this.amountChange = amountChange;
        this.newBalance = newBalance;
        this.transactionMessage = transactionMessage;
        this.transactionType = transactionType;
        this.transactionOwner = transactionOwner;
        this.date = new Date();
    }
    public double getAmountChange() {
        return amountChange;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getTransactionOwner() {
        return transactionOwner;
    }

    public Date getDate() {
        return date;
    }
}
