package com.example.andriodmidterm;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.SerializationUtils;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class GameManager {
    public enum GameState { INIT, NOT_STARTED, AWAITING, FINISHED}
    //Player 1 = White | Player 2 = Black
    public enum Team { WHITE, BLACK }
    Activity gameActivity;
    Random random;

    Account player1, player2, player1Snapshot, player2Snapshot;
    TextView diceSumTextViewWhite, diceSumTextViewBlack, totalPointsWhiteText, totalPointsBlackText,
            rerollleftWhiteText, rerollleftBlackText, totalBetWhiteText, totalBetBlackText,
            totalBalanceBlackText, totalBalanceWhiteText, potTotalText;
    EditText betAmountWhite, betAmountBlack;
    ImageView whiteDice1, whiteDice2, whiteDice3, blackDice1, blackDice2, blackDice3;
    Button whiteRerollButton, blackRerollButton, playButton;
    ProgressBar progressBarWhite, progressBarBlack;
    int sumWhite, sumBlack, rerollCountWhite, rerollCountBlack, totalPointsWhite, totalPointsBlack;
    double totalBetWhite, totalBetBlack, betValueWhite, betValueBlack;
    private GameState currentGameState;
    Team winner;

    public GameManager(Activity activity) {
        gameActivity = activity;
        random = new Random();
        initializeAccounts();
        initializeUIElements();
        currentGameState = GameManager.GameState.INIT;
    }

    private void initializeAccounts() {
        //Manipulatable Accounts
        player1 = (Account) gameActivity.getIntent().getSerializableExtra("player1");
        player2 = (Account) gameActivity.getIntent().getSerializableExtra("player2");

        //Account Snapshots for restoring in event of Mid-Game leave
        player1Snapshot = SerializationUtils.clone(player1);
        player2Snapshot = SerializationUtils.clone(player2);
    }

    private void initializeUIElements() {
        whiteDice1 = gameActivity.findViewById(R.id.whitedice1);
        whiteDice2 = gameActivity.findViewById(R.id.whitedice2);
        whiteDice3 = gameActivity.findViewById(R.id.whitedice3);
        blackDice1 = gameActivity.findViewById(R.id.blackdice1);
        blackDice2 = gameActivity.findViewById(R.id.blackdice2);
        blackDice3 = gameActivity.findViewById(R.id.blackdice3);
        diceSumTextViewWhite = gameActivity.findViewById(R.id.diceSumWhite);
        diceSumTextViewBlack = gameActivity.findViewById(R.id.diceSumBlack);
        whiteRerollButton = gameActivity.findViewById(R.id.reroll1);
        blackRerollButton = gameActivity.findViewById(R.id.reroll2);
        betAmountWhite = gameActivity.findViewById(R.id.whiteBetAmount);
        betAmountBlack = gameActivity.findViewById(R.id.blackBetAmount);
        totalPointsWhiteText = gameActivity.findViewById(R.id.pointswhite);
        totalPointsBlackText = gameActivity.findViewById(R.id.pointsblack);
        rerollleftWhiteText = gameActivity.findViewById(R.id.rerollleft1);
        rerollleftBlackText = gameActivity.findViewById(R.id.rerollleft2);
        totalBetWhiteText = gameActivity.findViewById(R.id.totalBetWhiteText);
        totalBetBlackText = gameActivity.findViewById(R.id.totalBetBlackText);
        totalBalanceWhiteText = gameActivity.findViewById(R.id.balanceTextWhite);
        totalBalanceBlackText = gameActivity.findViewById(R.id.balanceTextBlack);
        progressBarWhite = gameActivity.findViewById(R.id.progressBarWhite);
        progressBarBlack = gameActivity.findViewById(R.id.progressBarBlack);
        potTotalText = gameActivity.findViewById(R.id.pottotaltxt);
        playButton = gameActivity.findViewById(R.id.continueGameBtn);
    }

    //progression of game
    public void progressGame() throws InterruptedException {
        if (currentGameState==GameState.INIT) {
            newGameSetup();
            chargeAccount(Team.WHITE, 50, "Dice Game Buy-In");
            chargeAccount(Team.BLACK, 50, "Dice Game Buy-In");
            rollDice(Team.WHITE);
            rollDice(Team.BLACK);
            refreshGUI();
            enableRerolling();
            Toast.makeText(gameActivity, "First Round started, first bet set at 50$ each", Toast.LENGTH_LONG).show();
            playButton.setText("Next Round");
            currentGameState=GameState.AWAITING;
        }
        else if (currentGameState==GameState.NOT_STARTED) {
            if (!validateBets()) {
                Toast.makeText(gameActivity, "Both players must enter a minimum bet of 5$", Toast.LENGTH_LONG).show();
                return;
            }
            disableBetting();
            player1.updateBalance(-betValueWhite);
            player2.updateBalance(-betValueBlack);
            totalBetWhite += betValueWhite;
            totalBetBlack += betValueBlack;
            //chargeAccount(Team.WHITE, betValueWhite, "Dice Game Bet");
            //chargeAccount(Team.BLACK, betValueBlack, "Dice Game Bet");
            rollDice(Team.WHITE);
            rollDice(Team.BLACK);
            refreshGUI();
            if (checkIfWinner()) {
                if (winner==null){
                    Toast.makeText(gameActivity,"Game was tied! Bets refunded.\nClick \"Finish Game\" to finalize the game", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(gameActivity, winner==Team.WHITE ? "Player 1 has won the game!\nClick \"Finish Game\" to finalize the game" : "Player 2 has won the game!\nClick \"Finish Game\" to finalize the game", Toast.LENGTH_LONG).show();
                }
                totalPointsWhite+=sumWhite;
                totalPointsBlack+=sumBlack;
                refreshGUI();
                playButton.setText("Finish Game");
                currentGameState=GameState.FINISHED;
                return;
            }
            enableRerolling();
            playButton.setText("Next Round");
            currentGameState=GameState.AWAITING;
        }
        else if (currentGameState==GameState.AWAITING) {
            //Starting Next Round
            totalPointsWhite+=sumWhite;
            totalPointsBlack+=sumBlack;
            refreshGUI();
            newRoundCleanup("5");
            disableRerolling();
            enableBetting();
            //Toast.makeText(gameActivity, "Please place a bet for the next round", Toast.LENGTH_SHORT).show();
            playButton.setText("Start Round");
            currentGameState = GameState.NOT_STARTED;
        }
        else if (currentGameState==GameState.FINISHED) {
            if (winner==null){
                player1 = player1Snapshot;
                player2 = player2Snapshot;
                chargeAccount(Team.WHITE, 50, "Dice Game Buy-In");
                chargeAccount(Team.BLACK, 50, "Dice Game Buy-In");
                Thread.sleep(10);
                chargeAccount(Team.WHITE, -50, "Dice Game Refund");
                chargeAccount(Team.BLACK, -50, "Dice Game Refund");
            }
            else {
                chargeAccount(Team.WHITE, totalBetWhite, "Dice Game Bet");
                chargeAccount(Team.BLACK, totalBetBlack, "Dice Game Bet");
                Thread.sleep(10);
                awardWinnings(winner,totalBetBlack+totalBetWhite, "Dice Game Winnings");
                player1Snapshot = SerializationUtils.clone(player1);
                player2Snapshot = SerializationUtils.clone(player2);
            }
            newRoundCleanup("");
            playButton.setText("New Game");
            currentGameState = GameState.INIT;
        }
    }

    private boolean checkIfWinner() {
        if (totalPointsWhite+sumWhite >= 100 && totalPointsBlack+sumBlack >= 100)
        {
            winner = null;
            return true;
        }
        if (totalPointsWhite+sumWhite >= 100){
            winner = Team.WHITE;
            return true;
        }
        else if (totalPointsBlack+sumBlack >= 100) {
            winner = Team.BLACK;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean validateBets() {
        if (betAmountWhite.getText().toString() == "" || betAmountBlack.getText().toString() == ""){
            return false;
        }
        betValueWhite = Double.valueOf(betAmountWhite.getText().toString());
        betValueBlack = Double.valueOf(betAmountBlack.getText().toString());
        if (betValueBlack < 5 || betValueWhite < 5) {
            return false;
        }
        return true;
    }

    private void newRoundCleanup(String betAmount) {
        betAmountWhite.setText(betAmount);
        betAmountBlack.setText(betAmount);

        rerollCountWhite = 3;
        rerollCountBlack = 3;
        clearDiceImages();
        rerollleftWhiteText.setText("");
        rerollleftBlackText.setText("");
        diceSumTextViewWhite.setText("");
        diceSumTextViewBlack.setText("");
    }

    private void newGameSetup() {
        winner = null;
        betAmountWhite.setText("50");
        betAmountBlack.setText("50");

        totalPointsWhite = 0;
        totalPointsBlack = 0;
        progressBarWhite.setProgress(0);
        progressBarBlack.setProgress(0);
        totalBetWhite = 0;
        totalBetBlack = 0;

        rerollCountWhite = 3;
        rerollCountBlack = 3;
    }

    private void awardWinnings(Team team, double amount, String message) {
        if (team==Team.WHITE) {
            player1.updateBalance(amount);
            player1.addTransaction(new Transaction(
                    amount, player1.getBalance(), message, Transaction.TransactionType.TRANSFER, "Player 1"));
        }
        else if (team==Team.BLACK) {
            player2.updateBalance(amount);
            player2.addTransaction(new Transaction(
                    amount, player2.getBalance(), message, Transaction.TransactionType.TRANSFER, "Player 2"));
        }
    }

    private void chargeAccount(Team team, double amount, String message) {
        if (team==Team.WHITE) {
            player1.updateBalance(-amount);
            player1.addTransaction(new Transaction(
                    -amount, player1.getBalance(), message, Transaction.TransactionType.TRANSFER, "Player 1"));
            totalBetWhite += amount;
        }
        else if (team==Team.BLACK) {
            player2.updateBalance(-amount);
            player2.addTransaction(new Transaction(
                    -amount, player2.getBalance(), message, Transaction.TransactionType.TRANSFER, "Player 2"));
            totalBetBlack+=amount;
        }
    }

    //refresh all the GUI components
    public void refreshGUI() {
        totalPointsWhiteText.setText("Points: " + totalPointsWhite);
        totalPointsBlackText.setText("Points: " + totalPointsBlack);
        progressBarWhite.setProgress(totalPointsWhite);
        progressBarBlack.setProgress(totalPointsBlack);
        rerollleftWhiteText.setText("Rerolls Left: " + rerollCountWhite);
        rerollleftBlackText.setText("Rerolls Left: " + rerollCountBlack);
        totalBetWhiteText.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(totalBetWhite));
        totalBetBlackText.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(totalBetBlack));
        totalBalanceWhiteText.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(player1.getBalance()));
        totalBalanceBlackText.setText(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(player2.getBalance()));
        potTotalText.setText("Pot Total: " + NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(totalBetBlack+totalBetWhite));
        diceSumTextViewWhite.setText("Sum of Dice: " + sumWhite);
        diceSumTextViewBlack.setText("Sum of Dice: " + sumBlack);
    }

    //----GUI Refresh helpers---
    private void refreshDiceImagesAndSum(int[] rolls, Team team) {
        if (team==Team.WHITE) {
            whiteDice1.setImageResource(retrieveDiceImage(Team.WHITE, rolls[0]));
            whiteDice2.setImageResource(retrieveDiceImage(Team.WHITE, rolls[1]));
            whiteDice3.setImageResource(retrieveDiceImage(Team.WHITE, rolls[2]));
            if (rerollCountWhite == 0) {
                whiteRerollButton.setEnabled(false);
            }
        }
        else if (team==Team.BLACK) {
            blackDice1.setImageResource(retrieveDiceImage(Team.BLACK, rolls[0]));
            blackDice2.setImageResource(retrieveDiceImage(Team.BLACK, rolls[1]));
            blackDice3.setImageResource(retrieveDiceImage(Team.BLACK, rolls[2]));
            if (rerollCountBlack == 0) {
                blackRerollButton.setEnabled(false);
            }
        }
    }

    private void clearDiceImages() {
        whiteDice1.setImageResource(R.drawable.whitediceblank);
        whiteDice2.setImageResource(R.drawable.whitediceblank);
        whiteDice3.setImageResource(R.drawable.whitediceblank);
        blackDice1.setImageResource(R.drawable.blackdiceblank);
        blackDice2.setImageResource(R.drawable.blackdiceblank);
        blackDice3.setImageResource(R.drawable.blackdiceblank);
    }
    public void rollDice(Team team) {
        if (team == Team.WHITE) {
            //if (rerollCountWhite <= 0) return;
            rerollCountWhite-= 1;
            int[] rolls = rollDiceValues();
            refreshDiceImagesAndSum(rolls, Team.WHITE);
            sumWhite = Arrays.stream(rolls).sum();
        }
        else if (team == Team.BLACK) {
            //if (rerollCountBlack <= 0) return;
            rerollCountBlack-= 1;
            int[] rolls = rollDiceValues();
            refreshDiceImagesAndSum(rolls, Team.BLACK);
            sumBlack = Arrays.stream(rolls).sum();
        }
        refreshGUI();
    }

    //Rolls the dice
    //returns the value of the 3 die together
    private int[] rollDiceValues() {
        int roll1 = random.nextInt(6) + 1;
        int roll2 = random.nextInt(6) + 1;
        int roll3 = random.nextInt(6) + 1;

        return new int[] {roll1,roll2,roll3};
    }

    //Find the resource Id for the respective dice roll and color
    private int retrieveDiceImage (Team team, int number) {
        switch (number) {
            case 1:
                return (team == Team.WHITE) ? R.drawable.whitedice1 : R.drawable.blackdice1;
            case 2:
                return (team == Team.WHITE) ? R.drawable.whitedice2 : R.drawable.blackdice2;
            case 3:
                return (team == Team.WHITE) ? R.drawable.whitedice3 : R.drawable.blackdice3;
            case 4:
                return (team == Team.WHITE) ? R.drawable.whitedice4 : R.drawable.blackdice4;
            case 5:
                return (team == Team.WHITE) ? R.drawable.whitedice5 : R.drawable.blackdice5;
            case 6:
                return (team == Team.WHITE) ? R.drawable.whitedice6 : R.drawable.blackdice6;
            default:
                return -1;
        }
    }

    private void enableBetting() {
        betAmountWhite.setEnabled(true);
        betAmountBlack.setEnabled(true);
    }

    private void disableBetting() {
        betAmountWhite.setEnabled(false);
        betAmountBlack.setEnabled(false);
    }

    private void enableRerolling() {
        whiteRerollButton.setEnabled(true);
        blackRerollButton.setEnabled(true);
    }
    private void disableRerolling() {
        whiteRerollButton.setEnabled(false);
        blackRerollButton.setEnabled(false);
    }


    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public Account getPlayer1() {
        return player1;
    }

    public Account getPlayer2() {
        return player2;
    }

    public Account getPlayer1Snapshot() {
        return player1Snapshot;
    }

    public Account getPlayer2Snapshot() {
        return player2Snapshot;
    }
}
