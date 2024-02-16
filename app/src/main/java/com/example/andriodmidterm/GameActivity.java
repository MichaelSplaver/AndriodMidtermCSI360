package com.example.andriodmidterm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private enum GameState { NOT_STARTED, STARTED, AWAITING, FINISHED}

    //Player 1 = White | Player 2 = Black
    private enum Team { WHITE, BLACK }

    Random random;
    private Account player1;
    private Account player2;

    private Account player1Snapshot;
    private  Account player2Snapshot;

    private GameState currentGameState;

    private Button whiteRerollButton;
    private Button blackRerollButton;

    private int sumWhite;
    private int sumBlack;

    private double totalBetWhite;
    private double totalBetBlack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        random = new Random();

        //Manipulatable Accounts
        player1 = (Account) getIntent().getSerializableExtra("player1");
        player2 = (Account) getIntent().getSerializableExtra("player2");

        //Account Snapshots for restoring in event of Mid-Game leave
        player1Snapshot = player1;
        player2Snapshot = player2;

        currentGameState = GameState.NOT_STARTED;

        whiteRerollButton = findViewById(R.id.reroll1);
        blackRerollButton = findViewById(R.id.reroll2);

        updateDisplay();

        whiteRerollButton.setOnClickListener(view -> {

        });

        blackRerollButton.setOnClickListener(view -> {

        });

        //Play Button
        Button playButton = findViewById(R.id.continueGameBtn);
        playButton.setOnClickListener(view -> {
            if (currentGameState==GameState.FINISHED) {
                //...
            }
            else if (currentGameState==GameState.NOT_STARTED) {

                currentGameState = GameState.STARTED;
                sumWhite = rollDice(Team.WHITE);
                sumBlack = rollDice(Team.BLACK);

                player1.updateBalance(-50);
                player1.addTransaction(new Transaction(
                        -50, player1.getBalance(), "Dice Game Buy-In", Transaction.TransactionType.TRANSFER, "Player 1"));
                totalBetWhite+=50;

                player2.updateBalance(-50);
                player2.addTransaction(new Transaction(
                        -50, player2.getBalance(), "Dice Game Buy-In", Transaction.TransactionType.TRANSFER, "Player 2"));
                totalBetBlack+=50;

                updateDisplay();

                whiteRerollButton.setEnabled(true);
                blackRerollButton.setEnabled(true);

                playButton.setText("Next Round");
                playButton.setEnabled(true);
                currentGameState = GameState.AWAITING;

            }
            else if (currentGameState==GameState.AWAITING) {
                //...
            }
        });

        //Menu Button
        findViewById(R.id.quitGameBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            if (currentGameState==GameState.NOT_STARTED || currentGameState==GameState.FINISHED) {
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

    }

    private int rollDice(Team team) {
        int roll1 = random.nextInt(6) + 1;
        int roll2 = random.nextInt(6) + 1;
        int roll3 = random.nextInt(6) + 1;

        int rollSum = (roll1+roll2+roll3);

        String diceSumText = "Sum of Dice: " + rollSum;

        if (team==Team.WHITE) {

            TextView diceSumTextView = findViewById(R.id.diceSumWhite);
            diceSumTextView.setText(diceSumText);

            ImageView whiteDice1 = findViewById(R.id.whitedice1);
            whiteDice1.setImageResource(retrieveDiceImage(Team.WHITE, roll1));

            ImageView whiteDice2 = findViewById(R.id.whitedice2);
            whiteDice2.setImageResource(retrieveDiceImage(Team.WHITE, roll2));

            ImageView whiteDice3 = findViewById(R.id.whitedice3);
            whiteDice3.setImageResource(retrieveDiceImage(Team.WHITE, roll3));
        }
        else if (team==Team.BLACK) {

            TextView diceSumTextView = findViewById(R.id.diceSumBlack);
            diceSumTextView.setText(diceSumText);

            ImageView blackDice1 = findViewById(R.id.blackdice1);
            blackDice1.setImageResource(retrieveDiceImage(Team.BLACK, roll1));

            ImageView blackDice2 = findViewById(R.id.blackdice2);
            blackDice2.setImageResource(retrieveDiceImage(Team.BLACK, roll2));

            ImageView blackDice3 = findViewById(R.id.blackdice3);
            blackDice3.setImageResource(retrieveDiceImage(Team.BLACK, roll3));
        }
        return rollSum;
    }

    private int retrieveDiceImage (Team team, int number) {
        switch (number) {
            case 1:
                return (team==Team.WHITE) ? R.drawable.whitedice1 : R.drawable.blackdice1;
            case 2:
                return (team==Team.WHITE) ? R.drawable.whitedice2 : R.drawable.blackdice2;
            case 3:
                return (team==Team.WHITE) ? R.drawable.whitedice3 : R.drawable.blackdice3;
            case 4:
                return (team==Team.WHITE) ? R.drawable.whitedice4 : R.drawable.blackdice4;
            case 5:
                return (team==Team.WHITE) ? R.drawable.whitedice5 : R.drawable.blackdice5;
            case 6:
                return (team==Team.WHITE) ? R.drawable.whitedice6 : R.drawable.blackdice6;
            default:
                return -1;
        }
    }
}