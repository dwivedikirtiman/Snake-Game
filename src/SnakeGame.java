import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x, y;
        Tile(int x, int y) { this.x = x; this.y = y; }
    }

    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    Tile food;
    Random random;

    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;
    boolean isPaused = false;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Grid Lines
        for (int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        // Food
        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Snake Head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Snake Body
        for (Tile snakePart : snakeBody) {
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Score and Game Over Display
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.white);
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over! Score: " + snakeBody.size(), 20, 40);
            g.drawString("Press 'R' to Restart", 20, 70);
        } else if (isPaused) {
            g.setColor(Color.yellow);
            g.drawString("Paused. Press 'P' to Resume", 20, 40);
        } else {
            g.drawString("Score: " + snakeBody.size(), 20, 40);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevPart = snakeBody.get(i - 1);
                snakePart.x = prevPart.x;
                snakePart.y = prevPart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Boundary Wrap-around Logic
        if (snakeHead.x < 0) snakeHead.x = boardWidth / tileSize - 1;
        if (snakeHead.x >= boardWidth / tileSize) snakeHead.x = 0;
        if (snakeHead.y < 0) snakeHead.y = boardHeight / tileSize - 1;
        if (snakeHead.y >= boardHeight / tileSize) snakeHead.y = 0;

        for (Tile part : snakeBody) {
            if (collision(snakeHead, part)) {
                gameOver = true;
                gameLoop.stop();
            }
        }
    }

    public boolean collision(Tile t1, Tile t2) {
        return t1.x == t2.x && t1.y == t2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !isPaused) {
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0; velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0; velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1; velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1; velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        } else if (e.getKeyCode() == KeyEvent.VK_P) {
            isPaused = !isPaused;
        }
    }

    public void restartGame() {
        snakeHead = new Tile(5, 5);
        snakeBody.clear();
        placeFood();
        gameOver = false;
        isPaused = false;
        velocityX = 1;
        velocityY = 0;
        gameLoop.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame(500, 500);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
