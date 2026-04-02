import javax.swing.*;
import java.awt.*;

public class FlappyBird {

    int x = 30;
    int y = 120;

    int width;
    int height;

    int velocity = 0;
    int gravity = 1;

    int windowHeight;

    Image birdImage;

    FlappyBird(int width, int height, int windowHeight) {

        this.width = width;
        this.height = height;
        this.windowHeight = windowHeight;

        birdImage = new ImageIcon("images/flappybird.png").getImage();
    }

    public void move() {

        velocity += gravity;
        y += velocity;

        if (y < 0) {
            y = 0;
            velocity = 0;
        }

        if (y > windowHeight - height) {
            y = windowHeight - height;
            velocity = 0;
        }
    }

    public void jump() {
        velocity = -10;
    }
}