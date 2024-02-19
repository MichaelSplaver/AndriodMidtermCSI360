package com.example.andriodmidterm;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.holder> {

    private ArrayList<Transaction> transactions;
    private boolean displayOwner;

    public TransactionsAdapter(ArrayList<Transaction> transactions, boolean displayOwner) {
        this.transactions = transactions;
        this.displayOwner = displayOwner;
    }

    @NonNull
    @Override
    public TransactionsAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.transaction_item, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsAdapter.holder holder, int position) {
        double amountChange = transactions.get(position).getAmountChange();
        holder.transactionAmountChange.setText(
            NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                .format(amountChange));
        if (amountChange < 0) {
            holder.transactionAmountChange.setTextColor(Color.parseColor("#b52d2d"));
        }
        else {
            holder.transactionAmountChange.setTextColor(Color.parseColor("#4ecf1f"));
        }
        if (displayOwner) {
            String transactionTypeString = transactions.get(position).getTransactionOwner() + " | " +
                transactions.get(position).getTransactionType().toString();
            holder.transactionType.setText(transactionTypeString);
        }
        else {
            holder.transactionType.setText(transactions.get(position).getTransactionType().toString());
        }
        holder.transactionMessage.setText(transactions.get(position).getTransactionMessage());
        holder.transactionNewBalance.setText(
            NumberFormat.getCurrencyInstance(new Locale("en", "US"))
                .format(transactions.get(position).getNewBalance()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class holder extends RecyclerView.ViewHolder {
        TextView transactionMessage;
        TextView transactionType;
        TextView transactionAmountChange;
        TextView transactionNewBalance;

        public holder(@NonNull View itemView) {
            super(itemView);
            transactionMessage = (TextView) itemView.findViewById(R.id.transactionMessage);
            transactionType = (TextView) itemView.findViewById(R.id.transactionType);
            transactionAmountChange = (TextView) itemView.findViewById(R.id.transactionAmountChange);
            transactionNewBalance = (TextView) itemView.findViewById(R.id.transactionNewBalance);
        }
    }
}
