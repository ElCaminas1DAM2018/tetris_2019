
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author victor
 */
public class Board extends JPanel {
    
    class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    moveRight();
                    break;
                case KeyEvent.VK_UP:
                    break;
                case KeyEvent.VK_DOWN:
                    moveDown();
                    break;                
            }
            repaint();
        }
    }
    
    public static final Color COLORS[] = {
            new Color(0, 0, 0), 
            new Color(204, 102, 102), 
            new Color(102, 204, 102), 
            new Color(102, 102, 204), 
            new Color(204, 204, 102), 
            new Color(204, 102, 204), 
            new Color(102, 204, 204), 
            new Color(218, 170, 0)};
    public static final int NUM_ROWS = 22;
    public static final int NUM_COLS = 10;    
    private Tetrominoes[][] board;
    private Shape currentShape;
    private int currentRow;
    private int currentCol;
    private Timer timer;
    private int deltaTime;
    
    public Board() {
        super();
        board = new Tetrominoes[NUM_ROWS][NUM_COLS];
        for(int row=0; row<NUM_ROWS; row++) {
            for(int col=0; col<NUM_COLS; col++) {
                board[row][col] = Tetrominoes.NoShape;
            }
        }
        currentShape = new Shape();
        currentRow = 2;
        currentCol = NUM_COLS / 2;
        
        MyKeyAdapter keyAdepter = new MyKeyAdapter();
        addKeyListener(keyAdepter);
        setFocusable(true);
        
        deltaTime = 500;
        timer = new Timer(deltaTime, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
	
                mainLoop();
            }
        });
        timer.start();
    }      
    
    public void mainLoop() {
        moveDown();
    }
    
    public void moveDown() {
        if (currentRow + currentShape.maxY() < NUM_ROWS - 1) {
            currentRow ++;  
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        paintBoard(g2d);
        paintShape(g2d);
    }
    
    private void paintShape(Graphics2D g2d) {
        for(int i=0; i<=3; i++) {
            drawSquare(g2d, currentRow + currentShape.getY(i),
                            currentCol + currentShape.getX(i),
                            currentShape.getShape());
        }
    }
    
    private void paintBoard(Graphics2D g2d) {
        for (int row = 0; row<NUM_ROWS; row ++) {
            for (int col = 0; col<NUM_COLS; col ++) {
                drawSquare(g2d, row, col, board[row][col]);
            }
        }
    }
    
    public void moveLeft() {
        if (currentCol + currentShape.minX() > 0) {
            currentCol --;
        }
    }
    
    public void moveRight() {
        if (currentCol + currentShape.maxX() < NUM_COLS - 1) {
            currentCol ++;
        }
    }
    
    private int squareWidth() {
        return getWidth() / NUM_COLS;
    }
    
    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }

    private void drawSquare(Graphics g, int row, int col, Tetrominoes shape) {        
        int x = col * squareWidth();
        int y = row * squareHeight();
        Color color = COLORS[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    

}
