import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Background extends JPanel implements ActionListener, KeyListener {

    int width;
    int height;

    boolean gameOver = false;

    Image backgroundImage;

    FlappyBird flappyBird;

    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer pipeSpawner;

    float score = 0;

    Background(int width, int height) {

        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
        addKeyListener(this);

        backgroundImage = new ImageIcon("images/flappybirdbg.png").getImage();

        flappyBird = new FlappyBird(36, 24, height);

        pipes = new ArrayList<>();

        // GAME LOOP (60 FPS)
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        // SPAWN PIPE mỗi 1.2 giây
        pipeSpawner = new Timer(1200, e -> {
            if (!gameOver) {
                placePipes();
            }
        });

        pipeSpawner.start();

        // spawn pipe đầu tiên ngay khi bắt đầu game
        placePipes();
    }

    public void placePipes() {

        int gap = height / 4;

        Pipe topPipe = new Pipe("images/toppipe.png");
        topPipe.x = width + topPipe.width;

        int minTopY = height / 4 - topPipe.height;
        int maxTopY = height / 2 - topPipe.height;

        topPipe.y = minTopY + (int)(Math.random() * (maxTopY - minTopY));

        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe("images/bottompipe.png");
        bottomPipe.x = width + bottomPipe.width;

        bottomPipe.y = topPipe.y + topPipe.height + gap;

        pipes.add(bottomPipe);
    }

    public boolean collision(FlappyBird bird, Pipe pipe) {

        return bird.x < pipe.x + pipe.width &&
               bird.x + bird.width > pipe.x &&
               bird.y < pipe.y + pipe.height &&
               bird.y + bird.height > pipe.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (!gameOver) {

            flappyBird.move();

            for (Pipe pipe : pipes) {

                pipe.move();

                if (!pipe.passed && flappyBird.x > pipe.x + pipe.width) {

                    pipe.passed = true;
                    score += 0.5;
                }

                if (collision(flappyBird, pipe)) {

                    gameOver = true;
                    pipeSpawner.stop();
                }
            }

            // chạm đất cũng game over
            if (flappyBird.y >= height - flappyBird.height) {

                gameOver = true;
                pipeSpawner.stop();
            }

            pipes.removeIf(pipe -> pipe.x + pipe.width < 0);
        }

        repaint();
    }

    public void resetGame() {

        flappyBird.x = 30;
        flappyBird.y = 120;
        flappyBird.velocity = 0;

        pipes.clear();

        score = 0;

        gameOver = false;

        pipeSpawner.start();

        placePipes();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_SPACE ||
            e.getKeyCode() == KeyEvent.VK_ENTER) {

            if (!gameOver) {

                flappyBird.jump();

            } else {

                resetGame();
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, width, height, null);

        g.drawImage(
                flappyBird.birdImage,
                flappyBird.x,
                flappyBird.y,
                flappyBird.width,
                flappyBird.height,
                null
        );

        for (Pipe pipe : pipes) {

            g.drawImage(
                    pipe.pipeImage,
                    pipe.x,
                    pipe.y,
                    pipe.width,
                    pipe.height,
                    null
            );
        }

        // SCORE
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Score: " + (int)score, 20, 50);

        // GAME OVER TEXT (centered)
        if (gameOver) {

            // GAME OVER (màu đỏ)
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.setColor(Color.RED);

            String gameOverText = "GAME OVER";

            FontMetrics fm1 = g.getFontMetrics();

            int x1 = (width - fm1.stringWidth(gameOverText)) / 2;
            int y1 = height / 2;

            g.drawString(gameOverText, x1, y1);


            // PRESS SPACE TO RESTART (màu trắng)
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);

            String restartText = "Press [SPACE/ENTER] to try again";

            FontMetrics fm2 = g.getFontMetrics();

            int x2 = (width - fm2.stringWidth(restartText)) / 2;
            int y2 = y1 + 40;

            g.drawString(restartText, x2, y2);
        }
    }
}