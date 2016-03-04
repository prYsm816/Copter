package MainGame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MainGame.Copter
 * Created by Dan on 2/24/2016.
 */

// TODO reset barriers after game end, add explosion, make player and barrier all actors.

public class Controller extends Application {

    Random rand;

    private Group gameLayout;
    private VBox menuItems;
    private Scene sceneGame;
    private Scene mainMenu;
    private Actor player;
    private int playerSpeed;

    private List<Actor> barriers;
    private int nBarriers;
    private int barrierGap;
    private int barrierSpeed;
    Actor topRect;
    Actor botRect;
    private List<Actor> trail;
    private boolean noclip;

    int tempScore = 0;
    private int score = 0;


    public Controller() {
        noclip = false;
        rand = new Random();
        gameLayout = new Group();
        menuItems = new VBox(20);
        sceneGame = new Scene(gameLayout, 800, 300, Color.BLACK);
        mainMenu = new Scene(menuItems, 300, 300, Color.BLACK);
        barrierSpeed = -10;
        nBarriers = 2;
        barrierGap = 300;
    }

    public void start(Stage primaryStage) throws Exception {
        Button start = new Button("Start");
        Button quit = new Button("Exit");
        menuItems.getChildren().addAll(start, quit);
        menuItems.setStyle("-fx-background-color: black");
        menuItems.setAlignment(Pos.CENTER);
        start.setOnAction(e -> {
            primaryStage.setScene(sceneGame);
            gameStart(primaryStage);
        });
        quit.setOnAction(e -> primaryStage.close());

        primaryStage.setTitle("Copter");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../images/copter.png")));
        primaryStage.setScene(mainMenu);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void gameStart(Stage primaryStage) {
        player = new Actor();
        double playerX = 50;
        double playerY = 100;
        barriers = new ArrayList<>();
        topRect = new Actor() {
        };
        botRect = new Actor() {
        };
        playerSpeed = 5;
        trail = new ArrayList<>();

        player.setSrc(new Rectangle());
        player.setBounds(playerX, playerY, 50, 50);
        player.setImageSrc(new Image(getClass().getResourceAsStream("../images/copter.png")));
        player.setRotate(20);
        gameLayout.getChildren().add(player.getImageSrc());


        for (int i = 0; i < nBarriers; i++) {
            barriers.add(new Actor() {
            });
            barriers.get(i).setSrc(new Rectangle());
            double x = 0;
            if (i == 0) {
                x = sceneGame.getWidth();
            }
            if (i > 0) {
                x = barriers.get(i - 1).getX() + barrierGap;
            }

            double y = rand.nextInt(((int) sceneGame.getHeight() - 100) - 10) + 10;

            barriers.get(i).setBounds(x, y, 20, rand.nextInt(100 - 50) + 50);
            barriers.get(i).setFill(Color.LIME);
            barriers.get(i).setSpeed(barrierSpeed);
            gameLayout.getChildren().add(barriers.get(i).getSrc());

        }

        topRect.setSrc(new Rectangle());
        topRect.setBounds(0, 0, (int) sceneGame.getWidth() + 10, 10);
        topRect.setFill(Color.LIME);

        botRect.setSrc(new Rectangle());
        botRect.setBounds(0, 290, (int) sceneGame.getWidth() + 10, 10);
        botRect.setFill(Color.LIME);
        gameLayout.getChildren().addAll(topRect.getSrc(), botRect.getSrc());
        tick(primaryStage);
    }

    private void gameStop(Stage primaryStage) {
        gameLayout.getChildren().removeAll(player.getSrc(), topRect.getSrc(), botRect.getSrc());
        for (Actor barrierList : barriers) {
            gameLayout.getChildren().remove(barrierList.getSrc());
        }
        for (Actor trails : trail) {
            gameLayout.getChildren().remove(trails.getSrc());
        }
        score = 0;
        primaryStage.setScene(mainMenu);
    }

    public void tick(Stage primaryStage) {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                int i = 0;
                trail.add(i, new Actor());
                trail.get(i).setSrc(new Rectangle());
                trail.get(i).setBounds(player.getX(), player.getY() + 5, 10, 3);
                trail.get(i).setFill(Color.WHITESMOKE);
                trail.get(i).setSpeed(-10);
                gameLayout.getChildren().add(trail.get(i).getSrc());
                i++;

                for (Actor trails : trail) {
                    if (trails.getY() < 0) {
                        gameLayout.getChildren().remove(trails.getSrc());
                        trails.setSpeed(0);
                    }
                    trails.moveX(trails.getSpeed());
                }

                player.moveY(player.getSpeed());

                if (!noclip) {
                    for (Actor barrierList : barriers) {

                        if (player.getImageSrc().getBoundsInParent().intersects(barrierList.getSrc().getBoundsInParent())) {
                            playerCrash();
                        }
                        if (barrierList.getX() + barrierList.getWidth() < 0) {
                            gameLayout.getChildren().remove(barrierList.getSrc());
                            barrierList.setSpeed(0);
                        }
                    }

                    if (player.getImageSrc().getBoundsInParent().intersects(topRect.getSrc().getBoundsInParent()) || player.getImageSrc().getBoundsInParent().intersects(botRect.getSrc().getBoundsInParent())) {
                        playerCrash();

                    }
                }

                playerMove();
                barrierMove();
                score(primaryStage);
            }

            private void playerCrash() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stop();
                gameStop(primaryStage);
            }
        }.start();
    }

    private void playerMove() {
        sceneGame.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.DOWN) || event.getCode().equals(KeyCode.S)) {
                    player.setSpeed(playerSpeed);
                    player.setRotate(30);
                    System.out.println(player.getY());
                }
                if (event.getCode().equals(KeyCode.UP) || event.getCode().equals(KeyCode.W)) {
                    player.setSpeed(playerSpeed * (-1));
                    player.setRotate(10);
                    System.out.println(player.getY());
                }
                if (event.getCode() == KeyCode.SPACE) {
                    player.setRotate(10);
                    player.setSpeed(-5);
                }
            }
        });
        sceneGame.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                player.setSpeed(0);
                player.setRotate(20);
                if (event.getCode().equals(KeyCode.SPACE)) {
                    player.setSpeed(5);
                }
            }
        });
    }

    private void barrierMove() {
        for (Actor barrierList : barriers) {
            barrierList.moveX(barrierList.getSpeed());
        }
    }

    private void score(Stage primaryStage) {
        primaryStage.setTitle("Copter | " + score);
        if (tempScore % 10 == 0) {
            score++;
        }
        tempScore++;
        System.out.println(score);

    }
}