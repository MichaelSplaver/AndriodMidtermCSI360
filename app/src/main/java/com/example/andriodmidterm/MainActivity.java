package com.example.andriodmidterm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Account player1;
    private Account player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = (Account) getIntent().getSerializableExtra("player1");
        player2 = (Account) getIntent().getSerializableExtra("player2");

        if (player1 == null || player2 == null) {
            player1 = new Account(452994,1000.00, "Chase Bank");
            player2 = new Account(236566, 1000.00, "Capital One");
        }

        findViewById(R.id.dicegamebtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("player1", player1);
            intent.putExtra("player2", player2);
            startActivity(intent);
        });

        findViewById(R.id.bankbtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, BankActivity.class);
            intent.putExtra("player1", player1);
            intent.putExtra("player2", player2);
            startActivity(intent);
        });

        //refresh the RecyclerView with latest transactions
        RecyclerView transactionsView = (RecyclerView) findViewById(R.id.recyclerMain);
        transactionsView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Transaction> combinedList = player1.getTransactions();
        combinedList.addAll(player2.getTransactions());

        Collections.sort(combinedList, Comparator.comparing(Transaction::getDate).reversed());

        transactionsView.setAdapter(new TransactionsAdapter(combinedList, true));
    }
}