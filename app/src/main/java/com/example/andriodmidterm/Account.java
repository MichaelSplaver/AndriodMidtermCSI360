package com.example.andriodmidterm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a player's account (basically the entire player for the purposes of this game)
 * Stores transactions an ArrayList
 */
public class Account implements Serializable {
    private int accountNumber;
    private double balance;
    private String bankName;
    private ArrayList<Transaction> transactions;

    public Account(int accountNumber, double balance, String bankName) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.bankName = bankName;
        this.transactions = new ArrayList<>();
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getBankName() {
        return bankName;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
    public void updateBalance(double amount) {
        balance+=amount;
    }
}
