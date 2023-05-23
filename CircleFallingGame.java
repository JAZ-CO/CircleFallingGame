package com.example.circlefallinggame;

// Java program to create circle by passing the
// coordinates of the center and radius
// as arguments in constructor
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Collections;



public class CircleFallingGame extends Application {
    // Public variables
    private final GamePane gamePane = new GamePane();
    // initial width and height of the scene ("window")
    private final int width = 500, height = 500;

    //     create a scene
    public Scene scene;


    // launch the application
    public void start(Stage stage) {// starting the game
        gamePane.getStartBtn().setOnAction(e -> {
            gamePane.play();
        });
        gamePane.getPlayAgain().setOnAction(e -> gamePane.play());

        // create a scene
        scene = new Scene(gamePane, width, height);

        // This will be able to sense mouse clicking everywhere in the scene
        scene.setOnMouseClicked(new MouseScore());


        // set the scene
        stage.setTitle("Circle Falling Game");
        stage.setScene(scene);
        stage.setMinHeight(height);
        stage.setMinWidth(width);
        stage.show();
    }

    // Main class
    public static void main(String args[]) {
        // launch the application
        launch(args);
    }

    // Customized Pane
    public class GamePane extends Pane implements Comparable<Integer>{
        // create circle with a created class that extends Circle

        private final GameCircle[] circle = new GameCircle[4];
        // Create introduction animation
        private final Label[] intro = {new Label("Circle Falling Game"),new Label("\nPresented by:"),new Label("\nJalal Zainaddin"),new Label("\nMohammed Alhamood")};

        private final Rectangle presentSquare = new Rectangle(300,40);
        private final VBox presentedBox = new VBox(intro[1],this.intro[2],this.intro[3]);

        private final Timeline introAnimation;
        // create how many times the player plays, useful for the top 5
        private int playCounter;
        // Another counter of how many times circles fall
        private int fallCounter;

        // Circle hit sound

        // create a score
        private int score;

        private ArrayList<Integer> allScores = new ArrayList<>();

        // scoring animation
        private Label scoring = new Label();


        //Create labels
        private final Label scoreLabel;
        private final Label[] rules = {new Label("Rule:"),new Label("\nTry to catch as many Circles as you can! \n"),new Label("\n Below represents color of each circle and their values \n\n"),new Label(),new Label(),new Label(),new Label()};

        //below label holds "GAMEOVER" and top 3 scores
        private final Label[] gameOverLabels = {new Label("GAMEOVER!"),new Label("\nTOP 5 scores"),new Label(),new Label(),new Label(),new Label(),new Label()};

        //VBoxes at the start and game Over
        VBox gameStart;
        VBox gameOver;


        //Create timelines
        private final Timeline fallAnimation;
        private final Timeline respawnAnimation;
        private final Timeline sizingPane;

        //Start and playAgain buttons
        private final Button startBtn;
        private final Button playAgain;

        //Images
        private final ImageView[] btnImages = {new ImageView("https://i.gifer.com/origin/fe/fe9eebde5e19b66192281164142359e4.gif"),
                                                new ImageView("https://i.gifer.com/33HX.gif")};





        //constructor
        GamePane() {
            this.setBackground(Background.fill(Color.WHITE));


            presentSquare.setFill(Color.WHITE);

            this.intro[0].setFont(new Font("Algerian",30));
            this.intro[0].setTextFill(Color.BLACK);
            this.intro[1].setFont(new Font("Algerian",20));
            this.intro[2].setFont(new Font("Algerian",20));
            this.intro[3].setFont(new Font("Algerian",20));

            this.intro[0].setScaleX(0.5);
            this.intro[0].setScaleY(0.5);

            this.intro[2].setTranslateX(-800);
            this.intro[3].setTranslateX(-850);

            this.presentSquare.setX(-700);


            this.introAnimation = new Timeline(new KeyFrame(Duration.seconds(0.01),new IntroHandler()));
            this.introAnimation.setCycleCount((int) ((30)/0.05));
            this.introAnimation.play();

            this.intro[0].setTranslateX(width/5);
            this.intro[0].setTranslateY(height/6);

            this.presentedBox.setTranslateX(this.intro[0].getTranslateX() +1);
            this.presentedBox.setTranslateY(this.intro[0].getTranslateY() -20);

            this.presentSquare.setTranslateX(this.intro[0].getTranslateX());
            this.presentSquare.setTranslateY(this.intro[0].getTranslateY());

            this.presentedBox.setVisible(false);

            this.presentSquare.setVisible(false);
            super.getChildren().addAll(this.intro[0],this.presentedBox,this.presentSquare);


            // SCORE LABEL (TOP LEFT)
            this.scoreLabel = new Label("Score: " + this.score);
            this.scoreLabel.setFont(new Font("Algerian",20));
            this.scoreLabel.setVisible(false);
            super.getChildren().add(scoreLabel);

            // scoring animation
            super.getChildren().add(scoring);
            scoring.setVisible(false);

            // FOUR CIRCLES added with HBox with their points

            this.circle[0] = new CircleYAxis(50);
            this.circle[0].setPaint(Color.GREEN);
            this.circle[0].setVisible(false);
            this.circle[0].setValue(10);

            this.circle[1] = new CircleYAxis(40);
            this.circle[1].setPaint(Color.RED);
            this.circle[1].setVisible(false);
            this.circle[1].setValue(20);

            this.circle[2] = new CircleVectors(30);
            this.circle[2].setPaint(Color.BLUEVIOLET);
            this.circle[2].setVisible(false);
            this.circle[2].setValue(30);

            this.circle[3] = new CircleVectors(25);
            this.circle[3].setVisible(false);
            this.circle[3].setPaint(Color.MIDNIGHTBLUE);
            this.circle[3].setValue(50);

            for (GameCircle gameCircle : circle)
                super.getChildren().add(gameCircle);


            // fall & respawn animation
            this.fallAnimation = new Timeline(new KeyFrame(Duration.seconds(0.02),new FallHandler()));
            this.fallAnimation.setCycleCount(Animation.INDEFINITE);

            this.respawnAnimation = new Timeline(new KeyFrame(Duration.seconds(3), new RespawnHandler()));
            this.respawnAnimation.setCycleCount(Animation.INDEFINITE);

            this.sizingPane = new Timeline(new KeyFrame(Duration.seconds(0.03),new SizingHandler()));
            this.sizingPane.setCycleCount(Animation.INDEFINITE);


            //code to edit rules label properties
            this.rules[0].setFont(new Font("Algerian",70));
            this.rules[1].setFont(new Font("Algerian",15));
            this.rules[2].setFont(new Font("Algerian",12.5));

            this.rules[3].setFont(new Font("Algerian",15));
            this.rules[3].setText(circle[0].getValue() + " pts");
            this.rules[3].setTextFill(circle[0].getFill());

            this.rules[4].setFont(new Font("Algerian",15));
            this.rules[4].setText(circle[1].getValue() + " pts");
            this.rules[4].setTextFill(circle[1].getFill());

            this.rules[5].setFont(new Font("Algerian",15));
            this.rules[5].setText(circle[2].getValue() + " pts");
            this.rules[5].setTextFill(circle[2].getFill());

            this.rules[6].setFont(new Font("Algerian",15));
            this.rules[6].setText(circle[3].getValue() + " pts");
            this.rules[6].setTextFill(circle[3].getFill());

            //code to edit gameOver Label properties
            this.gameOverLabels[0].setFont(new Font("Algerian",60));
            this.gameOverLabels[0].setTextFill(Color.RED);
            this.gameOverLabels[1].setFont(new Font("Algerian",20));
            this.gameOverLabels[2].setFont(new Font("Algerian",20));
            this.gameOverLabels[3].setFont(new Font("Algerian",20));
            this.gameOverLabels[4].setFont(new Font("Algerian",20));
            this.gameOverLabels[5].setFont(new Font("Algerian",20));
            this.gameOverLabels[6].setFont(new Font("Algerian",20));



            //code for start button
            this.startBtn = new Button();

            this.btnImages[0].setFitWidth(200);
            this.btnImages[0].setPreserveRatio(true);
            this.startBtn.setGraphic(this.btnImages[0]);

            //code for play again button
            this.playAgain = new Button();

            this.btnImages[1].setFitWidth(200);
            this.btnImages[1].setPreserveRatio(true);
            this.playAgain.setGraphic(this.btnImages[1]);



            // Create VBox for beginning rules and start button

            this.gameStart = new VBox(this.rules[0],this.rules[1],this.rules[2],this.rules[3],this.rules[4],this.rules[5],this.rules[6],this.startBtn);
            this.gameStart.setLayoutX(width/8);
            this.gameStart.setLayoutY(height/8);
            this.gameStart.setScaleX(0);
            this.gameStart.setScaleY(0);

            super.getChildren().add(gameStart);

            // Create VBox for GameOver
            this.gameOver = new VBox(this.gameOverLabels[0],this.playAgain,this.gameOverLabels[1],this.gameOverLabels[2],this.gameOverLabels[3]
            ,this.gameOverLabels[4],this.gameOverLabels[5],this.gameOverLabels[6]);
            this.gameOver.setVisible(false);
            this.gameOver.setScaleX(0);
            this.gameOver.setScaleY(0);
            this.gameOver.setLayoutX(width/8);
            this.gameOver.setLayoutY(height/8);
            super.getChildren().add(gameOver);

        }

        //getters


        public Label[] getIntro() {
            return intro;
        }

        public VBox getPresentedBox() {
            return presentedBox;
        }

        public Rectangle getPresentSquare() {
            return presentSquare;
        }

        public VBox getGameStart() {
            return gameStart;
        }

        public int getScore() {
            return score;
        }

        public Label getScoreLabel() {
            return scoreLabel;
        }



        public Button getStartBtn() {
            return startBtn;
        }

        public Button getPlayAgain() {
            return playAgain;
        }

        public GameCircle[] getCircle() {
            return circle;
        }
        public Duration getDuration(){
            return this.fallAnimation.getCurrentTime();
        }

        public VBox getGameOverPane() {
            return gameOver;
        }

        public int getFallCounter() {
            return fallCounter;
        }

        public Label getScoring() {
            return scoring;
        }

        //setters


        public void setScore(int score) {
            this.score = score;
        }

        public void setFallCounter(int fallCounter) {
            this.fallCounter = fallCounter;
        }
        // play and stop

        public void play(){

            //We stop it here because we'll use the play method more than once (not important)
            this.sizingPane.stop();

            // Below is we shrink the size of gamePane in order to resize it again after loss
            this.getGameOverPane().setScaleX(0);
            this.getGameOverPane().setScaleY(0);



            // increment play counter (useful for top scores label)
            this.playCounter++;


            //display the initial labels and buttons
            this.scoreLabel.setVisible(true);
            this.getScoreLabel().setText("Score: " + this.score);


            //hide the start menu and over menu if replay
            if (this.startBtn.isVisible()) {

                this.gameStart.setVisible(false);
            }
            else if (this.gameOver.isVisible()){

                this.gameOver.setVisible(false);
            }



            // recenter the green circle to top and return the default falling speed
            for (int i = 0; i < getCircle().length; i++) {
                this.getCircle()[i].setCenterY(0);
                this.getCircle()[i].setVisible(true);
                this.getCircle()[i].setCenterX(Math.random()*width);
                this.getCircle()[i].setFallDistance(GameCircle.getOriginalFallDistance());
            }


            // play the animations
            this.fallAnimation.play();
            this.respawnAnimation.play();

        }

        public void stop(){
            this.fallAnimation.stop();
            this.respawnAnimation.stop();
            this.sizingPane.play();
            this.scoring.setVisible(false);

            this.fallCounter = 0;

            this.allScores.add(this.score);
            Collections.sort(this.allScores);


            for (int i = 1; i <= this.playCounter && i<this.gameOverLabels.length-1; i++) {
                this.gameOverLabels[i + 1].setText(i+"- " + this.allScores.get(this.allScores.size()-i));
            }

            this.gameOver.setVisible(true);

            this.score = 0;



        }

        // compare the scores using Comparable interface

        @Override
        public int compareTo(Integer o) {
            if (allScores.get(0) > o) return 1;
            else if (allScores.get(0) == o)  return 0;
            else return -1;
        }

    }

    // Abstract Circle class

    public abstract static class GameCircle extends Circle{
        private static final double originalFallDistance = 4;


        GameCircle(int radius, double xPos, double yPos){
            super(radius);
            super.setCenterX(xPos);
            super.setCenterY(yPos);


        }



        public abstract int getValue();
        public abstract double getFallDistance();
        public abstract void setFallDistance(double s);

        public static double getOriginalFallDistance() {
            return originalFallDistance;
        }


        public abstract void setPaint(Paint paint);
        public abstract void setValue(int value);

    }


    public static class CircleYAxis extends GameCircle {
        private int value = 10;


        private double objFallSpeed = super.getOriginalFallDistance();


        public CircleYAxis(int radius){
            super(radius, 200, 50);

        }


        @Override
        public int getValue() {
            return value;
        }
        @Override
        public double getFallDistance() {
            return this.objFallSpeed;
        }

        @Override
        public void setFallDistance(double s){
            this.objFallSpeed = s;
        }

        @Override
        public void setPaint(Paint paint) {
            this.setFill(paint);
        }

        @Override
        public void setValue(int value) {
            this.value = value;
        }

    }

    public static class CircleVectors extends GameCircle {
        private double finalXPos;
        private int value = 50;


        private double objFallDistance = this.getOriginalFallDistance();

        public CircleVectors(int radius){
            super(radius, 100, 50);

        }


        @Override
        public void setFallDistance(double s){
            this.objFallDistance = s;
        }
        @Override
        public void setPaint(Paint paint) {
            this.setFill(paint);
        }
        @Override
        public void setValue(int value) {
            this.value = value;
        }
        @Override
        public double getFallDistance() {
            return this.objFallDistance;
        }
        @Override
        public int getValue() {
            return value;
        }

        //Below getters & setters is all for the second circle's x-axis velocity
        public void setFinalXPos(){

            this.finalXPos= getScene().getWidth() - this.getCenterX();
        }
        public double getFinalXPos(){
            return this.finalXPos;
        }
        public double getTotalXDistance(){
            return this.getCenterX() - this.getFinalXPos();
        }

        public double getTotalTime(){
            return getScene().getHeight() *0.1/this.getFallDistance();
        }
        public double getInitialXVelocity(){
            return 2*this.getTotalXDistance()/this.getTotalTime();
        }

        public double getXAcceleration(){
            return Math.pow(this.getInitialXVelocity(),2)/(2*this.getTotalXDistance());
        }



    }


    public class IntroHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent actionEvent) {

            if (gamePane.getIntro()[0].getScaleX() <1) {
                gamePane.getIntro()[0].setScaleX(gamePane.getIntro()[0].getScaleX() + 0.005);
                gamePane.getIntro()[0].setScaleY(gamePane.getIntro()[0].getScaleY() + 0.005);
            }
            else
                gamePane.getPresentSquare().setVisible(true);

            gamePane.getPresentSquare().setX(gamePane.getPresentSquare().getX() + 3);

            if (gamePane.getPresentSquare().getX() > 7){
                gamePane.getIntro()[0].setVisible(false);
                gamePane.getPresentedBox().setVisible(true);
            }
            if (gamePane.getIntro()[3].getTranslateX() != gamePane.getIntro()[0].getTranslateX()) {
                for (int i = 2; i < 4; i++) {
                    if (gamePane.getIntro()[i].getTranslateX() < gamePane.getIntro()[0].getTranslateX())
                        gamePane.getIntro()[i].setTranslateX(gamePane.getIntro()[i].getTranslateX() + 2);
                }
            }
            else if (gamePane.getIntro()[3].getTranslateX() == gamePane.getIntro()[0].getTranslateX() && gamePane.getPresentedBox().getScaleX() > 0){
                gamePane.getPresentedBox().setScaleX(gamePane.getPresentedBox().getScaleX() - 0.02);
                gamePane.getPresentedBox().setScaleY(gamePane.getPresentedBox().getScaleY() - 0.02);
            }
            else {
                gamePane.getGameStart().setScaleX(gamePane.getGameStart().getScaleX() + 0.015);
                gamePane.getGameStart().setScaleY(gamePane.getGameStart().getScaleY() + 0.015);
            }



        }
    }

    public class FallHandler implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent actionEvent) {

            for (GameCircle circle : gamePane.getCircle()){
                if (circle instanceof CircleYAxis){
                    if (circle.getCenterY() - circle.getRadius() <= scene.getHeight()) {
                        circle.setCenterY(circle.getCenterY() + 1.25*(scene.getHeight() / height) * circle.getFallDistance());
                    }
                    else if (circle.isVisible() && circle.getCenterY() - circle.getRadius() > scene.getHeight()) {
//                    gamePane.stop();
                }
                } else if (circle instanceof CircleVectors) {
                    if ( circle.getCenterY() - circle.getRadius() <= scene.getHeight()) {
                        //below is statement for going down by increasing the y-axis
                        circle.setCenterY(circle.getCenterY() + 1.25*(scene.getHeight()/height)* circle.getFallDistance());

                        circle.setCenterX(circle.getCenterX() - ((CircleVectors) circle).getXAcceleration());

                    }

                }
            }

        }
    }

    public class RespawnHandler implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent actionEvent) {
            if (gamePane.getFallCounter() < 4) {
                gamePane.setFallCounter(gamePane.getFallCounter()+ 1);
                for (GameCircle circle : gamePane.getCircle()) {
                    if (!circle.isVisible()) {
                        circle.setCenterY(0);
                        circle.setCenterX(Math.random() * scene.getWidth());
                        circle.setVisible(true);
                        if (circle instanceof CircleYAxis) {
                            circle.setFallDistance(circle.getFallDistance() + 2);
                        } else if (circle instanceof CircleVectors) {
                            ((CircleVectors) circle).setFinalXPos();
                        }

                    } else if (circle.getCenterY() - circle.getRadius() > scene.getHeight()) {
                        circle.setCenterY(0);
                        circle.setCenterX(Math.random() * scene.getWidth());
                        circle.setVisible(true);
                        if (circle instanceof CircleYAxis) {
                            circle.setFallDistance(circle.getFallDistance() + 2);
                        } else if (circle instanceof CircleVectors) {
                            ((CircleVectors) circle).setFinalXPos();
                        }
                    }

                }
            }
            else
                gamePane.stop();

        }
    }
    public class MouseScore implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event) {
            for (int i = 0; i < gamePane.getCircle().length; i++) {
                // Check if the mouse event occurred within the bounds of the circle & it is visible
                if (gamePane.getDuration() != Duration.ZERO&& gamePane.getCircle()[i].isVisible() && gamePane.getCircle()[i].contains(event.getX(), event.getY())) {
                    //temporary if statement to test hitbox, it will be added later to the top statements

                    gamePane.getCircle()[i].setVisible(false);

                    gamePane.setScore(gamePane.getScore() + gamePane.getCircle()[i].getValue());
                    gamePane.scoreLabel.setText("Score: " + gamePane.getScore());

                    gamePane.getScoring().setText("+"+gamePane.getCircle()[i].getValue());
                    gamePane.getScoring().setTranslateX(event.getX());
                    gamePane.getScoring().setTranslateY(event.getY());
                    gamePane.getScoring().setVisible(true);



                    Timeline scoringAni = new Timeline(new KeyFrame(Duration.seconds(0.04), new ScoringAnimation()));
                    scoringAni.setCycleCount(10);
                    scoringAni.play();
                }
            }


        }
        public class ScoringAnimation implements EventHandler<ActionEvent>{
            @Override
            public void handle(ActionEvent actionEvent) {
                gamePane.getScoring().setTranslateY(gamePane.getScoring().getTranslateY() - 0.5);


            }
        }

    }



    //Below is class for the animation of gameover and yellow circle after losing
    public class SizingHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent actionEvent) {
            if ( gamePane.getGameOverPane().getScaleY() < 1){
                gamePane.getGameOverPane().setScaleY(gamePane.getGameOverPane().getScaleY() + 0.1);
                gamePane.getGameOverPane().setScaleX(gamePane.getGameOverPane().getScaleY() + 0.1);

            }

        }
    }



}


