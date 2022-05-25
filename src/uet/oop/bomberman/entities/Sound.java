package uet.oop.bomberman.entities;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Sound {


    public MediaPlayer mediaPlayerItem = new MediaPlayer(new Media(new File("res/sound/item.wav").toURI().toString()));
    public MediaPlayer mediaPlayerUplevel = new MediaPlayer(new Media(new File("res/sound/uplevel.wav").toURI().toString()));



}
