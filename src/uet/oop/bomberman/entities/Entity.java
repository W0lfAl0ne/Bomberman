package uet.oop.bomberman.entities;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import uet.oop.bomberman.graphics.Sprite;

public abstract class Entity {

    protected double x;    //Tọa độ X tính từ góc trái trên trong Canvas
    protected double y;    //Tọa độ Y tính từ góc trái trên trong Canvas

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    protected Image img;

    //Khởi tạo đối tượng, chuyển từ tọa độ đơn vị sang tọa độ trong canvas
    public Entity( double xUnit, double yUnit, Image img) {
        this.x = xUnit ;
        this.y = yUnit ;
        this.img = img;
    }


    public void render(GraphicsContext gc) {
        gc.drawImage(img, x* Sprite.SCALED_SIZE, y* Sprite.SCALED_SIZE);
    }

    public Rectangle2D getBoundary()
    {
        if (img == null) return new Rectangle2D(x* Sprite.SCALED_SIZE,y* Sprite.SCALED_SIZE,Sprite.SCALED_SIZE,Sprite.SCALED_SIZE);
        return new Rectangle2D(x* Sprite.SCALED_SIZE,y* Sprite.SCALED_SIZE,img.getWidth(),img.getHeight());
    }
    public boolean intersects(Entity s) {
        return s.getBoundary().intersects( this.getBoundary() );
    }
    public abstract void update();

}
