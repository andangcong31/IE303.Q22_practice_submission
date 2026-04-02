import javax.swing.*;
import java.awt.*;

public class Pipe {

    int x;
    int y;

    int width = 64;
    int height = 512;

    int speed = 5;

    boolean passed = false;

    Image pipeImage;

    Pipe(String imagePath) {

        pipeImage = new ImageIcon(imagePath).getImage();
    }

    public void move() {

        x -= speed;
    }
}