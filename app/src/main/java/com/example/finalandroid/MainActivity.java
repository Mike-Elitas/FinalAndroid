package com.example.finalandroid;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

public class MainActivity extends AppCompatActivity {
    private ImageButton button;
    private ImageButton[][] imageButtons = new ImageButton[3][3];
    private char[][] board;
    private MediaPlayer sound;
    private GridLayout grid;
    private Handler handler;
    private boolean impossible = false;
    private boolean playerWin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid = findViewById(R.id.grid);

        Button restart = findViewById(R.id.restart);
        Button difficulty = findViewById(R.id.difficulty);

        restart.setOnClickListener(v -> {
            startGame();
            sound = MediaPlayer.create(this, R.raw.lose);
            sound.start();
        });

        difficulty.setOnClickListener(v -> {
            impossible = !impossible;
            if (difficulty.getText().toString().equals("Impossible")) difficulty.setText("Easy");
            else difficulty.setText("Impossible");
            startGame();
        });
        startGame();
    }

    private void startGame(){
        board = new char[][]{
                {(char) 0, (char) 0, (char) 0},
                {(char) 0, (char) 0, (char) 0},
                {(char) 0, (char) 0, (char) 0}};
        grid.removeAllViews();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                button = new ImageButton(this);
                button.setId(View.generateViewId());
                button.setBackgroundResource(R.drawable.blank);
                imageButtons[i][j] = button;
                grid.addView(button);
                button.setOnClickListener(this::onClick);
            }
        }
    }

    private void onClick(View v){
        button = (ImageButton) v;
        if (button.getBackground().getConstantState()
                == getDrawable(R.drawable.blank).getConstantState()){

                button.setBackgroundResource(R.drawable.x);
                sound = MediaPlayer.create(this, R.raw.play);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (button == imageButtons[i][j]) board[i][j] = (char) 1;
                    }
                }
                handler = new Handler();
                handler.postDelayed(this::aiMove, 2000);
                checkWin();

        } else sound = MediaPlayer.create(this, R.raw.error);
        sound.start();
    }

    private boolean equals3(char a, char b, char c) {
        return a == b && b == c && a != (char)0;
    }

    private char checkLines() {
        // Must use 'n' for "null" since Java doesn't allow primitive data types to be null
        char winner = 'n';

        // horizontal
        for (int i = 0; i < 3; i++) {
            if (equals3(board[i][0], board[i][1], board[i][2])) {
                winner = board[i][0];
            }
        }

        // Vertical
        for (int i = 0; i < 3; i++) {
            if (equals3(board[0][i], board[1][i], board[2][i])) {
                winner = board[0][i];
            }
        }

        // Diagonal
        if (equals3(board[0][0], board[1][1], board[2][2])) {
            winner = board[0][0];
        }
        if (equals3(board[2][0], board[1][1], board[0][2])) {
            winner = board[2][0];
        }

        int openSpots = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == (char)0) {
                    openSpots++;
                }
            }
        }

        if (winner == 'n' && openSpots == 0) {
            return 't';
        } else {
            return winner;
        }
    }

    private void checkWin(){
        char result = checkLines();
        if (result != 'n') {
            if (result == 't') {
                sound = MediaPlayer.create(this, R.raw.win);
                sound.start();
                startGame();
            } else if (result == 1){
                playerWin = true;
                sound = MediaPlayer.create(this, R.raw.win);
                sound.start();
                startGame();
            } else {
                sound = MediaPlayer.create(this, R.raw.lose);
                sound.start();
                startGame();
            }
        }
    }

    private Runnable aiMove(){
        if (!playerWin){
            if (!impossible) {
                int randomI = 0;
                int randomJ = 0;
                boolean occupied = true;
                while (occupied) {
                    randomI = (int) (Math.random() * 3);
                    randomJ = (int) (Math.random() * 3);
                    if (board[randomI][randomJ] == (char) 0) occupied = false;
                }
                board[randomI][randomJ] = (char) -1;
                button = imageButtons[randomI][randomJ];
                button.setBackgroundResource(R.drawable.o);
                sound.start();
                checkWin();
            } else {
                
            }
        } playerWin = false;
        return null;
    }
}