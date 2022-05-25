package uet.oop.bomberman;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.graphics.Sprite;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class BombermanGame extends Application {

    public static int level = 1;
    public int row;
    public int column;
    public static long time = 0;
    public static long timeSpace = 0;
    public static boolean nextLevel = false;
    public static Sound sound = new Sound();

    private static GraphicsContext gc;
    private static Canvas canvas;
    private static Group root = new Group();
    private static Stage primaryStage;
    private static Scene scene;

    public static List<Entity> entities = new ArrayList<>();
    public static List<Entity> stillObjects = new ArrayList<>();
    public static List<Entity> grass = new ArrayList<>();
    public static ArrayList<Bomb> bombs = new ArrayList<>();
    public static ArrayList<Portal>  portals = new ArrayList<>();
    public static ArrayList<Item>  items = new ArrayList<>();


    private String[] maps ;
    public static ArrayList<String> input = new ArrayList<String>();
    public static Bomber bomberman ;
    private MediaPlayer mediaPlayerWIN = new MediaPlayer(new Media(new File("res/sound/win.wav").toURI().toString()));
    private MediaPlayer mediaPlayerEndgame = new MediaPlayer(new Media(new File("res/sound/endgame.wav").toURI().toString()));
    private MediaPlayer mediaPlayerBackGround = new MediaPlayer(new Media(new File("res/sound/background.wav").toURI().toString()));



    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage)  {
        primaryStage = stage;
        // Tao Canvas
        readFromFile(level);
        canvas = new Canvas(Sprite.SCALED_SIZE * column, Sprite.SCALED_SIZE * row);
        gc = canvas.getGraphicsContext2D();

        // Tao root container

        root.getChildren().add(canvas);

        // Tao scene
        scene = new Scene(root);

        gc.setFill( Color.BLACK );
        gc.setStroke( Color.WHITE );
        gc.setLineWidth(1);


        // Them scene vao stage
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("Bomberman");
        createMap();


        mediaPlayerBackGround.setCycleCount(-1);
        mediaPlayerBackGround.play();



        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if ( !input.contains(event.getCode().toString()) ) input.add( event.getCode().toString());

                timeSpace = System.currentTimeMillis()/100;
            }

        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                input.remove(event.getCode().toString());
            }
        });

        long longTime = System.nanoTime();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {

                    update();
                    render();
                time = (l - longTime)/1000000;


                if (nextLevel && level<=3){
                    nextLevel = false;
                    level++;
                    nextLevel();
                }

                if (bomberman.died || level>3) endGame();


            }
        };

        timer.start();


    }

    void readFromFile (int level)  {
        try {
            File f = new File("res/levels/Level" + level + ".txt");
            Scanner scanner = new Scanner(f);

            level = scanner.nextInt();
            row = scanner.nextInt();
            column = scanner.nextInt();
            maps = new String[column];

            scanner.nextLine();
            for (int i=0; i<row; i++) maps[i] = scanner.nextLine();
            scanner.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public void createMap() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                Entity object ;
                grass.add(new Grass(j,i, Sprite.grass.getFxImage()));
                if (maps[i].charAt(j)=='p'){
                    bomberman =new Bomber(j, i, Sprite.player_right.getFxImage());
                }
                else if(maps[i].charAt(j)=='1') entities.add(new Balloon(j, i, Sprite.balloom_left1.getFxImage()));

                else if(maps[i].charAt(j)=='2') entities.add(new Oneal(j, i, Sprite.oneal_left1.getFxImage()));


                if (maps[i].charAt(j)=='#') {
                    object = new Wall(j, i, Sprite.wall.getFxImage());
                }
                else if (maps[i].charAt(j)=='*'){
                    object = new Brick(j, i, Sprite.brick.getFxImage());
                }
                else if (maps[i].charAt(j)=='x'){
                    object = new Brick(j, i, Sprite.brick.getFxImage(),'x');
                }
                else if (maps[i].charAt(j)=='b'){
                    object = new Brick(j, i, Sprite.brick.getFxImage(),'b');
                }
                else if (maps[i].charAt(j)=='f'){
                    object = new Brick(j, i, Sprite.brick.getFxImage(),'f');
                }
                else if (maps[i].charAt(j)=='s'){
                    object = new Brick(j, i, Sprite.brick.getFxImage(),'s');
                }

                else continue;
                stillObjects.add(object);
            }
        }
    }

    public void update() {
        entities.forEach(Entity::update);
        bomberman.update();
        stillObjects.forEach(Entity::update);
        bombs.forEach(Bomb::update);
        portals.forEach(Portal::update);
        brickRemove();
        entityRemove();
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        grass.forEach(g->g.render(gc));
        portals.forEach(g->g.render(gc));
        items.forEach(g->g.render(gc));
        bombs.forEach(g -> g.render(gc));

        stillObjects.forEach(g -> g.render(gc));
        entities.forEach(g -> g.render(gc));
        bomberman.render(gc);

    }

    public void brickRemove() {
        Iterator<Entity> brickBag = BombermanGame.stillObjects.iterator();
        while (brickBag.hasNext()) {
            Entity brick = brickBag.next();
            if (brick instanceof Brick ) {
                if (!((Brick) brick).aBoolean){
                    if (((Brick) brick).aChar=='x'){
                        portals.add(new Portal((int)brick.getX(),(int)brick.getY(),Sprite.portal.getFxImage()));
                        brickBag.remove();
                    }
                    else if (((Brick) brick).aChar=='b'){
                        items.add(new Item((int)brick.getX(),(int)brick.getY(),Sprite.powerup_bombs.getFxImage(),'b'));
                        brickBag.remove();
                    }
                    else if (((Brick) brick).aChar=='f'){
                        items.add(new Item((int)brick.getX(),(int)brick.getY(),Sprite.powerup_flames.getFxImage(),'f'));
                        brickBag.remove();
                    }
                    else if (((Brick) brick).aChar=='s'){
                        items.add(new Item((int)brick.getX(),(int)brick.getY(),Sprite.powerup_speed.getFxImage(),'s'));
                        brickBag.remove();
                    }

                    else brickBag.remove();
                }
            }
        }
    }

    public void entityRemove() {
        Iterator<Entity> entitiesBag = BombermanGame.entities.iterator();
        while (entitiesBag.hasNext()) {
            Entity entity = entitiesBag.next();
            if (entity instanceof Balloon) {
                if (((Balloon) entity).timeDied<0){
                    entitiesBag.remove();
                }
            }
            if (entity instanceof Oneal) {
                if (((Oneal) entity).timeDied<0){
                    entitiesBag.remove();
                }
            }
        }
    }

    private  void nextLevel(){
        MediaPlayer mediaPlayerUplevel = new MediaPlayer(new Media(new File("res/sound/uplevel.wav").toURI().toString()));
        mediaPlayerUplevel.play();

        primaryStage.close();

        readFromFile(level);
        entities.clear();
        stillObjects.clear();
        bombs.clear();

        canvas = new Canvas(Sprite.SCALED_SIZE * column, Sprite.SCALED_SIZE * row);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().clear();
        root.getChildren().add(canvas);
        primaryStage.getScene().setRoot(root);
        primaryStage.isFullScreen();


        primaryStage.show();
        createMap();

    }

    private void endGame(){

        Font theFont = Font.font( "Times New Roman", FontWeight.BOLD, 64 );
        gc.setFont( theFont );
        if (level<=3) {
            gc.fillText("GAME OVER", canvas.getWidth() / 2 - 64 * 3, canvas.getHeight() / 2);
            gc.strokeText("GAME OVER", canvas.getWidth() / 2 - 64 * 3, canvas.getHeight() / 2);
            mediaPlayerBackGround.stop();
            mediaPlayerEndgame.play();
        }else {
            gc.fillText("WIN", canvas.getWidth() / 2 - 64 * 3, canvas.getHeight() / 2);
            gc.strokeText("WIN", canvas.getWidth() / 2 - 64 * 3, canvas.getHeight() / 2);
            mediaPlayerBackGround.stop();
            mediaPlayerWIN.play();
        }

        gc.clip();
    }

}
