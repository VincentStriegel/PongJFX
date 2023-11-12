package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javafx.util.Duration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HelloApplication extends Application {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int PLAYER_HEIGHT = 100;
    private static final int PLAYER_WIDTH = 15;
    private static final double BALL_R = 15;
    private static final int SPEED_FACTOR = 3;

    // Variables to control the ball's movement speed

    private double ballYSpeed = 1;
    private double ballXSpeed = 1;

    // Variables to track player paddle positions

    private double playerOneYPos = HEIGHT / 2;
    private double playerTwoYPos = HEIGHT / 2;

    // Variables for ball position

    private double ballXPos = WIDTH / 2;
    private double ballYPos = HEIGHT / 2;

    // Score keeping variables

    private int scoreP1 = 0;
    private int scoreP2 = 0;

    // Game state flag

    private boolean gameStarted;

    // Paddle X positions

    private final int playerOneXPos = 0;
    private final double playerTwoXPos = WIDTH - PLAYER_WIDTH;

    private enum GameMode {
        PLAYER_VS_PLAYER,
        PLAYER_VS_COMPUTER
    }

    private GameMode gameMode = GameMode.PLAYER_VS_PLAYER; // Default mode

    // Start method with menu implementation

    public void start(Stage stage) throws Exception {

        StackPane root = new StackPane();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Menu VBox for buttons

        VBox menuBox = new VBox(10);
        menuBox.setAlignment(Pos.CENTER);
        Button btnStartGame = new Button("Start Game");
        Button btnPlayerVsPlayer = new Button("Player vs Player");
        Button btnPlayerVsComputer = new Button("Player vs Computer");

        // Define the style for buttons

        String buttonStyle = "-fx-border-color: white; " +
                "-fx-border-WIDTH: 2; " +
                "-fx-background-color: black; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px;";

        // Define the style for buttons when they are pressed

        String buttonPressedStyle = "-fx-background-color: white; " +
                "-fx-text-fill: black;" +
                "-fx-font-size: 16px;";

        // Apply the styles to the buttons using setStyle for normal state and

        btnStartGame.setStyle(buttonStyle);
        btnStartGame.setOnMousePressed(e -> btnStartGame.setStyle(buttonPressedStyle));
        btnStartGame.setOnMouseReleased(e -> btnStartGame.setStyle(buttonStyle));
        btnPlayerVsPlayer.setStyle(buttonStyle);

        btnPlayerVsPlayer.setOnMousePressed(e -> {

            btnPlayerVsPlayer.setStyle(buttonPressedStyle);
            btnPlayerVsComputer.setStyle(buttonStyle);
        });

        btnPlayerVsComputer.setStyle(buttonStyle);
        btnPlayerVsComputer.setOnMousePressed(e -> {
            btnPlayerVsComputer.setStyle(buttonPressedStyle);
            btnPlayerVsPlayer.setStyle(buttonStyle);
        });

        // Set the same minimum WIDTH for all buttons

        double buttonWidth = 200; // Set this to the WIDTH you want
        btnStartGame.setMinWidth(buttonWidth);
        btnPlayerVsPlayer.setMinWidth(buttonWidth);
        btnPlayerVsComputer.setMinWidth(buttonWidth);

        // Configure button actions

        btnStartGame.setOnAction(e -> {
            gameStarted = true;
            root.getChildren().remove(menuBox);
            canvas.requestFocus();

        });

        btnPlayerVsPlayer.setOnAction(e -> gameMode = GameMode.PLAYER_VS_PLAYER);
        btnPlayerVsComputer.setOnAction(e -> gameMode = GameMode.PLAYER_VS_COMPUTER);

        // Add buttons to VBox

        menuBox.getChildren().addAll(btnStartGame, btnPlayerVsPlayer, btnPlayerVsComputer);

        Set<KeyCode> pressedKeys = new HashSet<>();
        canvas.setOnKeyPressed(event -> {

            KeyCode keyCode = event.getCode();
            pressedKeys.add(keyCode);

            if (pressedKeys.contains(KeyCode.W)) {
                playerOneYPos = Math.max(playerOneYPos - 15, 0);
            }
            if (pressedKeys.contains(KeyCode.S)) {
                playerOneYPos = Math.min(playerOneYPos + 15, HEIGHT - PLAYER_HEIGHT);
            }
            if (pressedKeys.contains(KeyCode.UP) && gameMode == GameMode.PLAYER_VS_PLAYER) {
                playerTwoYPos = Math.max(playerTwoYPos - 15, 0);
            }
            if (pressedKeys.contains(KeyCode.DOWN) && gameMode == GameMode.PLAYER_VS_PLAYER) {
                playerTwoYPos = Math.min(playerTwoYPos + 15, HEIGHT - PLAYER_HEIGHT);
            }
            if (pressedKeys.contains(KeyCode.ESCAPE)) {

                gameStarted = false;
                scoreP1 = 0;
                scoreP2 = 0;

                if (!root.getChildren().contains(menuBox)) {
                    root.getChildren().add(menuBox);
                }
                menuBox.requestFocus();
                pressedKeys.remove(event.getCode());
            }

        });

        canvas.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
        });

        // Add canvas and menuBox to root

        root.getChildren().add(canvas);
        root.getChildren().add(menuBox);

        // Timeline setup remains the same

        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();

        // Make sure the canvas can be focused to receive key inputs

        canvas.setFocusTraversable(true);
        stage.setScene(scene);
        stage.show();
        menuBox.requestFocus(); // Focus on menu initially

    }

    // The run method contains the game loop logic
    private void run(GraphicsContext gc) {

        // Drawing the game background and the paddles

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(25));

        // Drawing the player paddles

        gc.fillRect(playerOneXPos, playerOneYPos, PLAYER_WIDTH, PLAYER_HEIGHT);

        // Game logic for ball movement and collision with paddles

        if (gameStarted) {

            // Ball movement logic

            ballXPos += ballXSpeed;
            ballYPos += ballYSpeed;

            // "AI" for the second player paddle movement

            if (gameMode == GameMode.PLAYER_VS_COMPUTER && ballXPos > WIDTH * 0.5 && Math.signum(ballXSpeed) == 1) {
                int r = new Random().nextInt(1) == 0 ? 1 : -1;
                if (playerTwoYPos + PLAYER_HEIGHT / 2 < ballYPos + ( r * 100)) {
                    playerTwoYPos += 0.5 * SPEED_FACTOR;
                } else {
                    playerTwoYPos -= 0.5 * SPEED_FACTOR;
                }
            }

            // Drawing the ball
            gc.fillOval(ballXPos, ballYPos, BALL_R, BALL_R);
        } else {
            // Reset ball position and speed for the next game

            resetBall();
            ballXSpeed = new Random().nextInt(SPEED_FACTOR) == 0 ? SPEED_FACTOR : -SPEED_FACTOR;
            ballYSpeed = new Random().nextInt(SPEED_FACTOR) == 0 ? SPEED_FACTOR : -SPEED_FACTOR;

        }

        // Ball collision logic with the top and bottom of the window
        if (ballYPos > HEIGHT || ballYPos < 0) {
            // Makes ball reverse direction when it hits the top or bottom
            ballYSpeed *= -1;
            ballXSpeed *= 1;
        }

        // Score update logic when the ball is not hit, goes out of bounds.

        if (ballXPos < playerOneXPos - PLAYER_WIDTH) {
            scoreP2++;
            resetBall();
        }

        if (ballXPos > playerTwoXPos + PLAYER_WIDTH) {
            scoreP1++;
            resetBall();
        }

        // Ball collision logic with paddles

        if (((ballXPos + BALL_R > playerTwoXPos) && ballYPos >= playerTwoYPos
                && ballYPos <= playerTwoYPos + PLAYER_HEIGHT) ||
                ((ballXPos < playerOneXPos + PLAYER_WIDTH) && ballYPos >= playerOneYPos
                        && ballYPos <= playerOneYPos + PLAYER_HEIGHT)) {

            if (ballYPos >= (playerTwoYPos + (PLAYER_HEIGHT * 0.75)) && Math.signum(ballXSpeed) == 1
                    || ballYPos >= (playerOneYPos + (PLAYER_HEIGHT * 0.75)) && Math.signum(ballXSpeed) == -1) {
                ballYSpeed = -1.5 * SPEED_FACTOR;
                ballXSpeed = Math.signum(ballXSpeed) * -1.3 * SPEED_FACTOR;

            } else if (ballYPos <= (playerTwoYPos + (PLAYER_HEIGHT * 0.25)) && Math.signum(ballXSpeed) == 1
                    || ballYPos <= (playerOneYPos + (PLAYER_HEIGHT * 0.25)) && Math.signum(ballXSpeed) == -1) {
                ballYSpeed = 1.5 * SPEED_FACTOR;
                ballXSpeed = Math.signum(ballXSpeed) * -1.3 * SPEED_FACTOR;
            } else {
                ballYSpeed = Math.signum(ballXSpeed);
                ballXSpeed = Math.signum(ballXSpeed) * -2* SPEED_FACTOR;

            }
        }

        // Display the current score

        gc.fillText(scoreP1 + "\t\t\t\t\t\t\t\t" + scoreP2, WIDTH / 2, 100);

        // Drawing both player paddles

        gc.fillRect(playerTwoXPos, playerTwoYPos, PLAYER_WIDTH, PLAYER_HEIGHT);
        gc.fillRect(playerOneXPos, playerOneYPos, PLAYER_WIDTH, PLAYER_HEIGHT);

    }

    // Helper method to reset the ball to the center of the screen

    private void resetBall() {
        ballXPos = WIDTH / 2;
        ballYPos = HEIGHT / 2;
        ballXSpeed = new Random().nextInt(2) == 0 ? SPEED_FACTOR : -SPEED_FACTOR;
        ballYSpeed = new Random().nextInt(2) == 0 ? SPEED_FACTOR : -SPEED_FACTOR;

    }

}