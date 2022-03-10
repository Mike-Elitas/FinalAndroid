package com.example.finalandroid;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

public class MainActivity extends AppCompatActivity {
    private ImageButton button;
    private final ImageButton[][] imageButtons = new ImageButton[3][3];
    private char[][] board;
    private MediaPlayer sound;
    private GridLayout grid;
    private boolean hard = false;
    private boolean playerWin = false;
    private TextView textView;
    private ImageButton popUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid = findViewById(R.id.grid);
        popUp = findViewById(R.id.imageButton2);

        Button restart = findViewById(R.id.restart);
        Button difficulty = findViewById(R.id.difficulty);
        textView = findViewById(R.id.textView);

        restart.setOnClickListener(v -> {
            startGame();
            sound = MediaPlayer.create(this, R.raw.lose);
            sound.start();
        });

        difficulty.setOnClickListener(v -> {
            hard = !hard;
            if (difficulty.getText().toString().equals("Hard")) difficulty.setText("Easy");
            else difficulty.setText("Hard");
            startGame();
        });
        startGame();
    }

    private void startGame(){
        textView.setText("Turno de Jugador");
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
                textView.setText("Turno de Maquina");
            Handler handler = new Handler();
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
                imageButtons[i][0].setBackgroundResource(R.drawable.win);
                imageButtons[i][1].setBackgroundResource(R.drawable.win);
                imageButtons[i][2].setBackgroundResource(R.drawable.win);
            }
        }

        // Vertical
        for (int i = 0; i < 3; i++) {
            if (equals3(board[0][i], board[1][i], board[2][i])) {
                winner = board[0][i];
                imageButtons[0][i].setBackgroundResource(R.drawable.win);
                imageButtons[1][i].setBackgroundResource(R.drawable.win);
                imageButtons[2][i].setBackgroundResource(R.drawable.win);
            }
        }

        // Diagonal
        if (equals3(board[0][0], board[1][1], board[2][2])) {
            winner = board[0][0];
            imageButtons[0][0].setBackgroundResource(R.drawable.win);
            imageButtons[1][1].setBackgroundResource(R.drawable.win);
            imageButtons[2][2].setBackgroundResource(R.drawable.win);
        }
        if (equals3(board[2][0], board[1][1], board[0][2])) {
            winner = board[2][0];
            imageButtons[2][0].setBackgroundResource(R.drawable.win);
            imageButtons[1][1].setBackgroundResource(R.drawable.win);
            imageButtons[0][2].setBackgroundResource(R.drawable.win);
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
        Handler handler = new Handler();
        char result = checkLines();
        if (result != 'n') {
            if (result == 't') {
                playerWin = true;
                sound = MediaPlayer.create(this, R.raw.win);
                sound.start();
                imagePop(R.drawable.tie);
                startGame();
            } else if (result == 1){
                playerWin = true;
                sound = MediaPlayer.create(this, R.raw.win);
                sound.start();
                imagePop(R.drawable.playerwin);
                handler.postDelayed(this::startGame, 2000);
            } else {
                sound = MediaPlayer.create(this, R.raw.lose);
                sound.start();
                imagePop(R.drawable.aiwin);
                handler.postDelayed(this::startGame, 2000);
            }
        }
    }

    private void aiMove(){
        if (!playerWin){
            if (!hard) {
                randomMove();
            } else {
                hardMove();
            }
            checkWin();
            textView.setText("Turno de Jugador");
        } playerWin = false;
    }

    private void randomMove(){
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
    }

    private boolean canWin(char a, char b, char c){
        if ( a == b && c == 0 && a == (char) -1)
            return true;
        else if (a == c && b == 0 && a == (char) -1)
            return true;
        else return b == c && a == 0 && b == (char) -1;
    }

    private boolean canLose(char a, char b, char c){
        if (a == b && c == 0 && a == 1)
            return true;
        else if (a == c && b == 0 && a == 1)
            return true;
        else return b == c && a == 0 && b == 1;
    }

    private void hardMove(){
        boolean defendAtack = false;

        for (int i = 0; i < 3; i++) {
            if (!defendAtack && (canWin(board[i][0], board[i][1], board[i][2]))){
                for (int j = 0; j < 3; j++) {
                    if (imageButtons[i][j].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                        imageButtons[i][j].setBackgroundResource(R.drawable.o);
                        board[i][j] = (char) -1;
                        break;
                    }
                }
                defendAtack = true;
            }

            if (!defendAtack && (canWin(board[0][i], board[1][i], board[2][i]))){
                if (imageButtons[0][i].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[0][i].setBackgroundResource(R.drawable.o);
                    board[0][i] = (char) -1;
                }
                if (imageButtons[1][i].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()) {
                    imageButtons[1][i].setBackgroundResource(R.drawable.o);
                    board[1][i] = (char) -1;
                }
                if (imageButtons[2][i].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[2][i].setBackgroundResource(R.drawable.o);
                    board[2][i] = (char) -1;
                }
                defendAtack = true;
            }
        }

        if (!defendAtack && canWin(board[0][0], board[1][1], board[2][2])){
            if (imageButtons[0][0].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                imageButtons[0][0].setBackgroundResource(R.drawable.o);
                board[0][0] = (char) -1;
            }
            if (imageButtons[1][1].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                imageButtons[1][1].setBackgroundResource(R.drawable.o);
                board[1][1] = (char) -1;
            }
            if (imageButtons[2][2].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                imageButtons[2][2].setBackgroundResource(R.drawable.o);
                board[2][2] = (char) -1;
            }
            defendAtack = true;
        }

        if (!defendAtack && canWin(board[2][0], board[1][1], board[0][2])){
            if (imageButtons[2][0].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                imageButtons[2][0].setBackgroundResource(R.drawable.o);
                board[2][0] = (char) -1;
            }
            if (imageButtons[1][1].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                imageButtons[1][1].setBackgroundResource(R.drawable.o);
                board[1][1] = (char) -1;
            }
            if (imageButtons[0][2].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                imageButtons[0][2].setBackgroundResource(R.drawable.o);
                board[0][2] = (char) -1;
            }
            defendAtack = true;
        }

        for (int j = 0; j < 3; j++) {
            if (!defendAtack && (canLose(board[j][0], board[j][1], board[j][2]))){
                for (int h = 0; h < 3; h++) {
                    if (imageButtons[j][h].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()) {
                        imageButtons[j][h].setBackgroundResource(R.drawable.o);
                        board[j][h] = (char) -1;
                        break;
                    }
                }
                defendAtack = true;
            }

            if (!defendAtack && (canLose(board[0][j], board[1][j], board[2][j]))){
                if (imageButtons[0][j].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[0][j].setBackgroundResource(R.drawable.o);
                    board[0][j] = (char) -1;
                }
                if (imageButtons[1][j].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()) {
                    imageButtons[1][j].setBackgroundResource(R.drawable.o);
                    board[1][j] = (char) -1;
                }
                if (imageButtons[2][j].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[2][j].setBackgroundResource(R.drawable.o);
                    board[2][j] = (char) -1;
                }
                defendAtack = true;
            }

            if (!defendAtack && canLose(board[0][0], board[1][1], board[2][2])){
                if (imageButtons[0][0].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[0][0].setBackgroundResource(R.drawable.o);
                    board[0][0] = (char) -1;
                }
                if (imageButtons[1][1].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[1][1].setBackgroundResource(R.drawable.o);
                    board[1][1] = (char) -1;
                }
                if (imageButtons[2][2].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[2][2].setBackgroundResource(R.drawable.o);
                    board[2][2] = (char) -1;
                }
                defendAtack = true;
            }

            if (!defendAtack && canLose(board[2][0], board[1][1], board[0][2])){
                if (imageButtons[2][0].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[2][0].setBackgroundResource(R.drawable.o);
                    board[2][0] = (char) -1;
                }
                if (imageButtons[1][1].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[1][1].setBackgroundResource(R.drawable.o);
                    board[1][1] = (char) -1;
                }
                if (imageButtons[0][2].getBackground().getConstantState() == getDrawable(R.drawable.blank).getConstantState()){
                    imageButtons[0][2].setBackgroundResource(R.drawable.o);
                    board[0][2] = (char) -1;
                }
                defendAtack = true;
            }
        }

        if (!defendAtack) randomMove();
        sound.start();
    }

    private void imagePop(int res){
        popUp.setBackgroundResource(res);
        popUp.setVisibility(View.VISIBLE);
        popUp.setOnClickListener(v -> popUp.setVisibility(View.INVISIBLE));
    }

}