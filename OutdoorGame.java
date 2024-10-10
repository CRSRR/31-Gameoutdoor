import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class OutdoorGame extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private int playerX = 300; // Player (basket) starting position
    private int playerY = 400;
    private int playerWidth = 80;  
    private int playerHeight = 60; 
    private int playerSpeed = 35; 
    private int score = 0;
    private int lives = 5;  

    private boolean isGameOver = false;  // Flag to track if the game is over

    private BufferedImage basketImage;   // To store the basket image
    private BufferedImage appleImage;    
    private BufferedImage goldenappleImage;
    private BufferedImage backgroundImage;  
    private ArrayList<Apple> apples = new ArrayList<>();
    private ArrayList<Goldenapple> goldenApples = new ArrayList<>(); 
    private Random random = new Random();

    private JButton restartButton;  

    public OutdoorGame() {
        timer = new Timer(40, this); 
        timer.start();

        try {
            backgroundImage = ImageIO.read(new File("nature.jpg")); 
        } catch (IOException e) {
            System.out.println("Background image not found.");
            e.printStackTrace();
        }

        try {
            basketImage = ImageIO.read(new File("basket.png"));
        } catch (IOException e) {
            System.out.println("Basket image not found.");
            e.printStackTrace();
        }

        try {
            appleImage = ImageIO.read(new File("apple.png")); 
        } catch (IOException e) {
            System.out.println("Apple image not found.");
            e.printStackTrace();
        }

        try {
            goldenappleImage = ImageIO.read(new File("goldenapple.png")); 
        } catch (IOException e) {
            System.out.println("Golden apple image not found.");
            e.printStackTrace();
        }

        
        for (int i = 0; i < 3; i++) {
            apples.add(new Apple(random.nextInt(600), random.nextInt(100) - 100)); // Random x, start y above screen
        }
        
        for (int i = 0; i < 1; i++) {
            goldenApples.add(new Goldenapple(random.nextInt(600), random.nextInt(100) - 100)); // Random x, start y above screen
        }

        // restart button
        restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        restartButton.setBounds(300, 400, 150, 50);
        restartButton.setVisible(false);  
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();  // Call restart method when button is clicked
            }
        });

        // Add the button to the JPanel
        setLayout(null);  // Set layout to null so we can manually position the button
        add(restartButton);

        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image 
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null); // Fit background to panel size
        }

        if (!isGameOver) {
            if (basketImage != null) {
                g.drawImage(basketImage, playerX, playerY, playerWidth, playerHeight, null);
            }

            for (Apple apple : apples) {
                if (appleImage != null) {
                    g.drawImage(appleImage, apple.getX(), apple.getY(), apple.getSize(), apple.getSize(), null);
                }
            }

            for (Goldenapple goldenapple : goldenApples) {
                if (goldenappleImage != null) {
                    g.drawImage(goldenappleImage, goldenapple.getX(), goldenapple.getY(), goldenapple.getSize(), goldenapple.getSize(), null);
                }
            }

            // Draw score and lives
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Lives: " + lives, 10, 50); 
        } 
        else {
            // Game Over screen
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("GAME OVER", getWidth() / 2 - 100, getHeight() / 2);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Final Score: " + score, getWidth() / 2 - 60, getHeight() / 2 + 40);
            restartButton.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            // Move apples downward
            for (int i = 0; i < apples.size(); i++) {
                Apple apple = apples.get(i);
                apple.setY(apple.getY() + apple.getSpeed());

                // Check if apple falls off screen (missed apple)
                if (apple.getY() > getHeight()) {
                    apples.remove(i);  // Remove it
                    apples.add(new Apple(random.nextInt(getWidth()), -20)); // Spawn a new one at the top
                    lives--;  // Decrease lives when an apple is missed
                    if (lives == 0) {
                        isGameOver = true;  // Set game over flag
                        timer.stop();  // Stop the game timer
                    }
                    break;
                }

                // Check for collisions (catching apples)
                if (new Rectangle(playerX, playerY, playerWidth, playerHeight).intersects(
                        new Rectangle(apple.getX(), apple.getY(), apple.getSize(), apple.getSize()))) {
                    apples.remove(i);
                    score++;
                    apples.add(new Apple(random.nextInt(getWidth()), -20)); // Spawn new apple at the top
                    break;
                }
            }

            // Move golden apples downward
            for (int i = 0; i < goldenApples.size(); i++) {
                Goldenapple goldenapple = goldenApples.get(i);
                goldenapple.setY(goldenapple.getY() + goldenapple.getSpeed());

                // Check if golden apple falls off screen (missed golden apple)
                if (goldenapple.getY() > getHeight()) {
                    goldenApples.remove(i);  // Remove it
                    goldenApples.add(new Goldenapple(random.nextInt(getWidth()), -20)); // Spawn a new one at the top
                    break;
                }

                // Check for collisions (catching golden apples)
                if (new Rectangle(playerX, playerY, playerWidth, playerHeight).intersects(
                        new Rectangle(goldenapple.getX(), goldenapple.getY(), goldenapple.getSize(), goldenapple.getSize()))) {
                    goldenApples.remove(i);
                    score += 2; // Increase score by 2 for golden apple
                    goldenApples.add(new Goldenapple(random.nextInt(getWidth()), -20)); // Spawn new golden apple at the top
                    break;
                }
            }

            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!isGameOver) {
            // Move the basket left or right when the arrow keys are pressed
            if (keyCode == KeyEvent.VK_LEFT && playerX > 0) {
                playerX -= playerSpeed;
            }
            if (keyCode == KeyEvent.VK_RIGHT && playerX < getWidth() - playerWidth) {
                playerX += playerSpeed;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private void restartGame() {
        isGameOver = false;
        score = 0;
        lives = 5;
        playerX = 300; 
        apples.clear();  
        goldenApples.clear(); 
        
        for (int i = 0; i < 3; i++) {
            apples.add(new Apple(random.nextInt(600), random.nextInt(100) - 100));
        }

        // Re-generate falling golden apples
        for (int i = 0; i < 1; i++) {
            goldenApples.add(new Goldenapple(random.nextInt(600), random.nextInt(100) - 100));
        }

        // Restart the game timer
        timer.start();
        restartButton.setVisible(false);  
        repaint();
    }

    // Apple Class for falling apples
    class Apple {
        private int x, y, size, speed;
        public Apple(int x, int y) {
            this.x = x;
            this.y = y;
            this.size = 30;  // Adjust size for the apple PNG
            this.speed = random.nextInt(5)+2; // Random falling speed for variation
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public int getSize() { return size; }
        public int getSpeed() { return speed; }
        
        public void setY(int y) { this.y = y; }
    }

    // Golden apple Class for falling golden apples
    class Goldenapple {
        private int x, y, size, speed;
        public Goldenapple(int x, int y) {
            this.x = x;
            this.y = y;
            this.size = 30;  // Adjust size for the golden apple PNG
            this.speed = random.nextInt(3)+1; 
        
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        public int getSize() { return size; }
        public int getSpeed() { return speed; }
        
        public void setY(int y) { this.y = y; }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Collecting Apples");
        OutdoorGame gamePanel = new OutdoorGame();
        frame.add(gamePanel);
        frame.setSize(700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
