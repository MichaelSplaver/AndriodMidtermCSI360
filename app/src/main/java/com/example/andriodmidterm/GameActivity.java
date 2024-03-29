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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameManager = new GameManager(this);

        //=====Start Listeners Setup======

        //Reroll White
        findViewById(R.id.reroll1).setOnClickListener(view -> {
            gameManager.rollDice(GameManager.Team.WHITE);
        });

        //Reroll Black
        findViewById(R.id.reroll2).setOnClickListener(view -> {
            gameManager.rollDice(GameManager.Team.BLACK);
        });

        //Play Button
        findViewById(R.id.continueGameBtn).setOnClickListener(view -> {
            try {
                gameManager.progressGame();
            }
            catch (Exception ignored) {}
        });

        //Menu Button
        findViewById(R.id.quitGameBtn).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            //if game is in INITIALIZATION game state the game may be exited without consequences
            if (gameManager.getCurrentGameState() == GameManager.GameState.INIT) {
                intent.putExtra("player1", gameManager.getPlayer1());
                intent.putExtra("player2", gameManager.getPlayer2());
            }
            //else restore to the snapshot of previous players
            else {
                intent.putExtra("player1", gameManager.getPlayer1Snapshot());
                intent.putExtra("player2", gameManager.getPlayer2Snapshot());
            }
            startActivity(intent);
        });
        //=====End Listeners Setup======
    }

}