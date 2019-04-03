
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
                    if (canMove(currentShape, currentCol - 1)) {
                        moveLeft();
                    }                   
                    break;
                case KeyEvent.VK_RIGHT:
                    if (canMove(currentShape, currentCol + 1)) {
                        moveRight();
                    } 
                    break;
                case KeyEvent.VK_UP:
                    rotateCurrentShape();
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
    private static final int INITIAL_ROW = -2;
    private MyKeyAdapter keyAdepter;
    private ScoreBoard scoreBoard;
    
    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }
    
    public Board() {
        super();
        board = new Tetrominoes[NUM_ROWS][NUM_COLS];
        for(int row=0; row<NUM_ROWS; row++) {
            for(int col=0; col<NUM_COLS; col++) {
                board[row][col] = Tetrominoes.NoShape;
            }
        }
        currentShape = new Shape();
        currentRow = INITIAL_ROW;
        currentCol = NUM_COLS / 2;
        
        keyAdepter = new MyKeyAdapter();
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
    
    private void moveDown() {
        if (!collisions(currentRow + 1)) {
            currentRow ++;
        } else {
            makeCollision();
            detectFullLine();
        }
        repaint();
    }
    
    private void detectFullLine() {
        int counter;
        for (int row = 0; row < NUM_ROWS; row ++) {
            counter = 0;
            for (int col = 0; col < NUM_COLS; col ++) {
                if (board[row][col] != Tetrominoes.NoShape) {
                    counter ++;
                }
            }
            if (counter == NUM_COLS) {
                //System.out.println("Full line " + row);
                deleteLine(row);
                scoreBoard.incrementScore();
            }
        }
    }
    
    private void deleteLine(int rowToDelete) {
        for (int row = rowToDelete; row>1; row --) {
            for (int col = 0; col < NUM_COLS; col ++) {
                board[row][col] = board[row-1][col];
            }
        }
        for (int col = 0; col< NUM_COLS; col++) {
            board[0][col] = Tetrominoes.NoShape;
        }
    }
    
    private boolean canMove(Shape shape, int newCol) {
        if (newCol + shape.minX() < 0) {
            return false;
        }
        if (newCol + shape.maxX() > NUM_COLS -1) {
            return false;            
        }
        for (int i=0; i<=3; i++) {
            int row = currentRow + shape.getY(i);
            int col = newCol + shape.getX(i);
            if (row>=0) {
                if (board[row][col] != Tetrominoes.NoShape) {
                    return false;
                }
            }
        }
        return true;
    }
        
    private boolean collisions(int newRow) {
        if (newRow + currentShape.maxY() >= NUM_ROWS ) {
            return true;
        } else {
            for (int i=0; i<=3; i++) {
                int row = newRow + currentShape.getY(i);
                int col = currentCol + currentShape.getX(i);
                if (row>=0) {
                    if (board[row][col] != Tetrominoes.NoShape) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void rotateCurrentShape() {
        Shape rotatedShape = currentShape.rotateRight();
        if (canMove(rotatedShape, currentCol)) {
            currentShape = rotatedShape;
        }
    }
    
    private void makeCollision() {
        if (!movePieceToBoard()) {
            makeGameOver();
        } else {
            currentShape = new Shape();
            currentRow = INITIAL_ROW;
            currentCol = NUM_COLS / 2;
        }
    }
    
    public void makeGameOver() {
        timer.stop();
        removeKeyListener(keyAdepter);
    }
    
    private boolean movePieceToBoard() {
        for (int i=0; i<=3; i++) {
            int row = currentRow + currentShape.getY(i);
            int col = currentCol + currentShape.getX(i);    
            if (row<0) { // Game over
                return false;
            } else {
                board[row][col] = currentShape.getShape();
            }
        }
        return true;
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
        currentCol --;
    }
    
    public void moveRight() {
        currentCol ++;
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
