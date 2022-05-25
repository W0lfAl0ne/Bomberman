package uet.oop.bomberman.entities;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import uet.oop.bomberman.BombermanGame;
import uet.oop.bomberman.graphics.Sprite;

import java.io.File;
import java.util.Random;

public class Oneal extends Entity {
    Random rd = new Random();
    private int direction = rd.nextInt(4);
    public boolean died = false;
    public long timeDied = 1;

    public Oneal(int x, int y, Image img) {
        super(x, y, img);
    }

    long startTime = BombermanGame.time;
    long lastTime = 0;
    long time = 0;
    @Override
    public void update() {
        if (timeDied<1) died = true;
        if ( (BombermanGame.time - startTime)/100> lastTime) {
            if (!died)move();
            lastTime = (BombermanGame.time - startTime)/100;
        }
        if (this.intersects(BombermanGame.bomberman)) BombermanGame.bomberman.bomberDied();

    }

    public void move() {

        if (direction == 0) {
            y = y - 0.1;
            if (canMove()) y = Math.round(y);
            this.img = Sprite.movingSprite(Sprite.oneal_left1,Sprite.oneal_left2,Sprite.oneal_left3,(int) lastTime,10).getFxImage();
        }
        if (direction == 1) {
            y = y + 0.1;
            if (canMove()) y = Math.round(y);
            this.img = Sprite.movingSprite(Sprite.oneal_right1,Sprite.oneal_right2,Sprite.oneal_right3,(int) lastTime,10).getFxImage();
        }
        if (direction == 2) {
            x = x - 0.1;
            if (canMove()) x = Math.round(x);
            this.img = Sprite.movingSprite(Sprite.oneal_left1,Sprite.oneal_left2,Sprite.oneal_left3,(int) lastTime,10).getFxImage();
        }

        if (direction == 3) {
            x = x + 0.1;
            if (canMove()) x = Math.round(x);
            this.img = Sprite.movingSprite(Sprite.oneal_right1,Sprite.oneal_right2,Sprite.oneal_right3,(int) lastTime,10).getFxImage();
        }

    }

    private boolean canMove(){
        for (Entity entity:BombermanGame.stillObjects ) {
            if (this.intersects(entity)) {
                direction = rd.nextInt(4);
                return true;
            }
        }
        for (Bomb bomb:BombermanGame.bombs) if(this.intersects(bomb)) {
            x = Math.round(x);
            y = Math.round(y);
            direction = rd.nextInt(4);
            return true;
        }

        return false;
    }

    private void onealAI(){

    }

    public void onealDied(){
        MediaPlayer mediaPlayerDied = new MediaPlayer(new Media(new File("res/sound/died.wav").toURI().toString()));
        mediaPlayerDied.play();
        if ( (BombermanGame.time - startTime)/1000 > time) {
            if (timeDied>=0) timeDied--;
            time = (BombermanGame.time - startTime)/1000;
        }
        this.img = Sprite.oneal_dead.getFxImage();
    }
}
