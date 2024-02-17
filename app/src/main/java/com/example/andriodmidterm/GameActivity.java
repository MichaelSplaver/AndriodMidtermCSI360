package com.example.andriodmidterm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.lang3.SerializationUtils;

public class GameActivity extends AppCompatActivity {

    GameManager gameManager;
    private Account player1;
    private Account player2;

    private Account player1Snapshot;
    private Account player2Snapshot;

    private GameManager.GameState currentGameState;

    private Button whiteRerollButton;
    private Button blackRerollButton;

    private int sumWhite;
    private int sumBlack;

    private int rerollCountWhite;
    private int rerollCountBlack;

    private int totalPointsWhite;
    private int totalPointsBlack;

    private double totalBetWhite;
    private double totalBetBlack;

    private boolean firstRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameManager = new GameManager(this);

        //Manipulatable Accounts
        player1 = (Account) getIntent().getSerializableExtra("player1");
        player2 = (Account) getIntent().getSerializableExtra("player2");

        //Account Snapshots for restoring in event of Mid-Game leave
        player1Snapshot = SerializationUtils.clone(player1);
        player2Snapshot = SerializationUtils.clone(player2);

        currentGameState = GameManager.GameState.INIT;

        whiteRerollButton = findViewById(R.id.reroll1);
        blackRerollButton = findViewById(R.id.reroll2);

        EditText betAmountWhite = findViewById(R.id.whiteBetAmount);
        EditText betAmountBlack = findViewById(R.id.blackBetAmount);

        totalBetWhite = 0;
        totalBetBlack = 0;

        TextView totalPointsWhiteText = findViewById(R.id.pointswhite);
        TextView totalPointsBlackText = findViewById(R.id.pointsblack);

        totalPointsWhite = 0;
        totalPointsBlack = 0;

        TextView rerollleftWhiteText = findViewById(R.id.rerollleft1);
        TextView rerollleftBlackText = findViewById(R.id.rerollleft2);

        firstRound = true;

        updateDisplay();

        whiteRerollButton.setOnClickListener(view -> {
            if (rerollCountWhite > 0) {
                rerollCountWhite-= 1;
                if (rerollCountWhite == 0) {
                    whiteRerollButton.setEnabled(false);
                }
                sumWhite = gameManager.rollDice(GameManager.Team.WHITE);
            }
            String rerollleftWhiteString = "Rerolls Left: " + rerollCountWhite;
            rerollleftWhiteText.setText(rerollleftWhiteString);
        });

        blackRerollButton.setOnClickListener(view -> {
            if (rerollCountBlack > 0) {
                rerollCountBlack-= 1;
                if (rerollCountBlack == 0) {
                    blackRerollButton.setEnabled(false);
                }
                sumBlack = gameManager.rollDice(GameManager.Team.BLACK);
            }

            String rerollleftBlackString = "Rerolls Left: " + rerollCountBlack;
            rerollleftBlackText.setText(rerollleftBlackString);
        });

        //Play Button
        Button playButton = findViewById(R.id.continueGameBtn);
        playButton.setOnClickListener(view -> {
            if (currentGameState==GameManager.GameState.FINISHED) {
                //...

            }
            else if (currentGameState==GameManager.GameState.INIT || currentGameState==GameManager.GameState.NOT_STARTED) {

                currentGameState = GameManager.GameState.STARTED;

                double betValueWhite;
                double betValueBlack;
                String transacationMessage;

                if (!firstRound) {
                    if (betAmountWhite.getText().toString() == ""){
                        betAmountWhite.setText("0");
                    }
                    if (betAmountBlack.getText().toString() == ""){
                        betAmountBlack.setText("0");
                    }

                    betValueWhite = Double.valueOf(betAmountWhite.getText().toString());
                    betValueBlack = Double.valueOf(betAmountBlack.getText().toString());
                    transacationMessage = "Dice Game Bet";
                } else {
                    betValueWhite = 50;
                    betValueBlack = 50;
                    transacationMessage = "Dice Game Buy-In";
                }
                player1.updateBalance(-betValueWhite);
                player1.addTransaction(new Transaction(
                        -betValueWhite, player1.getBalance(), transacationMessage, Transaction.TransactionType.TRANSFER, "Player 1"));
                totalBetWhite+=betValueWhite;

                player2.updateBalance(-betValueBlack);
                player2.addTransaction(new Transaction(
                        -betValueBlack, player2.getBalance(), transacationMessage, Transaction.TransactionType.TRANSFER, "Player 2"));
                totalBetBlack+=betValueBlack;


                sumWhite = gameManager.rollDice(GameManager.Team.WHITE);
                sumBlack = gameManager.rollDice(GameManager.Team.BLACK);

                updateDisplay();

                if (totalPointsBlack+sumBlack>=100 || totalPointsWhite+sumWhite>100)
                {
                    totalPointsWhite+=sumWhite;
                    totalPointsBlack+=sumBlack;
                    totalPointsWhiteText.setText("Points: " + totalPointsWhite);
                    totalPointsBlackText.setText("Points: " + totalPointsBlack);
                    currentGameState = GameManager.GameState.FINISHED;
                }
                else {
                    rerollCountWhite = 2;
                    rerollCountBlack = 2;
                    String rerollleftWhiteString = "Rerolls Left: " + rerollCountWhite;
                    rerollleftWhiteText.setText(rerollleftWhiteString);
                    String rerollleftBlackString = "Rerolls Left: " + rerollCountBlack;
                    rerollleftBlackText.setText(rerollleftBlackString);


                    whiteRerollButton.setEnabled(true);
                    blackRerollButton.setEnabled(true);

                    if (firstRound) {
                        betAmountWhite.setEnabled(false);
                        betAmountBlack.setEnabled(false);
                        betAmountWhite.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(50.0));
                        betAmountBlack.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(50.0));
                    }

                    firstRound = false;
                    playButton.setText("Next Round");
                    playButton.setEnabled(true);
                    currentGameState = GameManager.GameState.AWAITING;
                }

            }
            else if (currentGameState==GameManager.GameState.AWAITING) {
                //Starting Next Round
                whiteRerollButton.setEnabled(false);
                blackRerollButton.setEnabled(false);

                betAmountWhite.setEnabled(true);
                betAmountBlack.setEnabled(true);
                betAmountWhite.setText("");
                betAmountBlack.setText("");

                totalPointsWhite+=sumWhite;
                totalPointsBlack+=sumBlack;
                totalPointsWhiteText.setText("Points: " + totalPointsWhite);
                totalPointsBlackText.setText("Points: " + totalPointsBlack);

                updateDisplay();

                betAmountWhite.setText("0");
                betAmountBlack.setText("0");

                playButton.setText("Start Round");
                currentGameState = GameManager.GameState.NOT_STARTED;

            }
        });

        //Menu Button
        findViewById(R.id.quitGameBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            if (currentGameState == GameManager.GameState.INIT || currentGameState == GameManager.GameState.FINISHED) {
                intent.putExtra("player1", player1);
                intent.putExtra("player2", player2);
            }
            else {
                intent.putExtra("player1", player1Snapshot);
                intent.putExtra("player2", player2Snapshot);
            }
            startActivity(intent);
        });
    }

    private void updateDisplay() {
        TextView balanceWhiteText = findViewById(R.id.balanceTextWhite);
        TextView balanceBlackText = findViewById(R.id.balanceTextBlack);
        TextView totalBetWhiteText = findViewById(R.id.totalBetWhite);
        TextView totalBetBlackText = findViewById(R.id.totalBetBlack);
        TextView totalPotText = findViewById(R.id.pottotaltxt);

        balanceWhiteText.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(player1.getBalance()));
        balanceBlackText.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(player2.getBalance()));
        totalBetWhiteText.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(totalBetWhite));
        totalBetBlackText.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(totalBetBlack));
        String totalPotString = "Pot Total: " + NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(totalBetBlack+totalBetWhite);
        totalPotText.setText(totalPotString);
    }


}