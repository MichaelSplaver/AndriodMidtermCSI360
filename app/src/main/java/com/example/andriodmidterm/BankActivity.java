package com.example.andriodmidterm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class BankActivity extends AppCompatActivity {

    private Account player1;
    private Account player2;

    private String selectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        player1 = (Account) getIntent().getSerializableExtra("player1");
        player2 = (Account) getIntent().getSerializableExtra("player2");

        selectedAccount = "player1";
        populateBankInfo(player1);
        //=====Listeners Setup======

        //Radio Group selected changes
        RadioGroup accountSelector = findViewById(R.id.accountselector);
        accountSelector.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton selectedButton = findViewById(i);
            String buttonText = (String) selectedButton.getText();
            if (buttonText.contains("1")) {
                selectedAccount = "player1";
                populateBankInfo(player1);
            }
            else {
                selectedAccount = "player2";
                populateBankInfo(player2);
            }
        });

        //Deposit Button
        findViewById(R.id.bankdepositbtn).setOnClickListener(view -> {

            EditText inputAmount = findViewById(R.id.bankInput);

            if (!inputAmount.getText().toString().equals("")) {

                double transactionAmount = Double.parseDouble(inputAmount.getText().toString());

                if (selectedAccount == "player1") {
                    player1.updateBalance(transactionAmount);
                    player1.addTransaction(new Transaction(
                            transactionAmount, player1.getBalance(), "Bank Deposit", Transaction.TransactionType.DEPOSIT, "Player 1"));
                    populateBankInfo(player1);
                } else {
                    player2.updateBalance(transactionAmount);
                    player2.addTransaction(new Transaction(
                            transactionAmount, player2.getBalance(), "Bank Deposit", Transaction.TransactionType.DEPOSIT, "Player 2"));
                    populateBankInfo(player2);
                }
            }
        });

        //Withdraw Button
        findViewById(R.id.bankwithdrawbtn).setOnClickListener(view -> {

            EditText inputAmount = findViewById(R.id.bankInput);

            if (!inputAmount.getText().toString().equals("")) {

                double transactionAmount = Double.parseDouble(inputAmount.getText().toString());

                if (selectedAccount == "player1") {
                    player1.updateBalance(-transactionAmount);
                    player1.addTransaction(new Transaction(
                            -transactionAmount, player1.getBalance(), "Bank Withdrawal", Transaction.TransactionType.WITHDRAWAL, "Player 1"));
                    populateBankInfo(player1);
                } else {
                    player2.updateBalance(-transactionAmount);
                    player2.addTransaction(new Transaction(
                            -transactionAmount, player2.getBalance(), "Bank Withdrawal", Transaction.TransactionType.WITHDRAWAL, "Player 2"));
                    populateBankInfo(player2);
                }
            }
        });

        //Menu Button
        findViewById(R.id.homebuttonBank).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("player1", player1);
            intent.putExtra("player2", player2);
            startActivity(intent);
        });
    }

    private void populateBankInfo(Account account)
    {
        TextView bankName = findViewById(R.id.bankNameText);
        bankName.setText(account.getBankName());

        TextView bankAccountNumber = findViewById(R.id.bankAccountNumberText);
        bankAccountNumber.setText(Integer.toString(account.getAccountNumber()));

        TextView balance = findViewById(R.id.balance);
        balance.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(account.getBalance()));

        TextView selectAccount = findViewById(R.id.accountOpenText);
        String accountName;
        if (selectedAccount == "player1") { accountName = "Player 1"; }
        else { accountName = "Player 2"; }
        selectAccount.setText("Open Account: " + accountName);

        //populate transactions RecyclerView
        RecyclerView transactionsView = findViewById(R.id.recyclerBank);
        transactionsView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Transaction> recyclerViewList = account.getTransactions();

        Collections.sort(recyclerViewList, Comparator.comparing(Transaction::getDate).reversed());

        transactionsView.setAdapter(new TransactionsAdapter(recyclerViewList, false));
    }
}