package com.example.andriodmidterm;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GameManager {
    public enum GameState { INIT, NOT_STARTED, STARTED, AWAITING, FINISHED}

    //Player 1 = White | Player 2 = Black
    public enum Team { WHITE, BLACK }
    Activity gameActivity;
    Random random;

    public GameManager(Activity activity) {
        gameActivity = activity;
        random = new Random();
    }

    //refresh all the GUI components
    public void refreshGUI() {

    }

    //Rolls the dice (Updates the GUI)
    //returns the value of the 3 die together
    public int rollDice(Team team) {
        int roll1 = random.nextInt(6) + 1;
        int roll2 = random.nextInt(6) + 1;
        int roll3 = random.nextInt(6) + 1;

        int rollSum = (roll1+roll2+roll3);

        String diceSumText = "Sum of Dice: " + rollSum;

        if (team==Team.WHITE) {

            TextView diceSumTextView = gameActivity.findViewById(R.id.diceSumWhite);
            diceSumTextView.setText(diceSumText);

            ImageView whiteDice1 = gameActivity.findViewById(R.id.whitedice1);
            whiteDice1.setImageResource(retrieveDiceImage(Team.WHITE, roll1));

            ImageView whiteDice2 = gameActivity.findViewById(R.id.whitedice2);
            whiteDice2.setImageResource(retrieveDiceImage(Team.WHITE, roll2));

            ImageView whiteDice3 = gameActivity.findViewById(R.id.whitedice3);
            whiteDice3.setImageResource(retrieveDiceImage(Team.WHITE, roll3));
        }
        else if (team==Team.BLACK) {

            TextView diceSumTextView = gameActivity.findViewById(R.id.diceSumBlack);
            diceSumTextView.setText(diceSumText);

            ImageView blackDice1 = gameActivity.findViewById(R.id.blackdice1);
            blackDice1.setImageResource(retrieveDiceImage(Team.BLACK, roll1));

            ImageView blackDice2 = gameActivity.findViewById(R.id.blackdice2);
            blackDice2.setImageResource(retrieveDiceImage(Team.BLACK, roll2));

            ImageView blackDice3 = gameActivity.findViewById(R.id.blackdice3);
            blackDice3.setImageResource(retrieveDiceImage(Team.BLACK, roll3));
        }
        return rollSum;
    }

    //Find the resource Id for the respective dice roll and color
    public int retrieveDiceImage (Team team, int number) {
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
}
