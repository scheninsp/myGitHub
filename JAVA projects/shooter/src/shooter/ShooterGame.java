package shooter;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import java.util.*;

import shooter.BoardPanel;
import shooter.Direction;
import shooter.TileType;
import shooter.Clock;

/**
 * thanks Radu Ciprian Rotaru for his Snakegame project.
 *
 */

public class ShooterGame extends JFrame{
	
	/**
	 * The number of milliseconds that should pass between each frame.
	 */
	private static final long FRAME_TIME = 1000L / 50L;
	
	private static final int MAX_DIRECTIONS = 3 ;
	
	/**
	 * The Board to paint everything.
	 */
	private BoardPanel board;
	
	/**
	 * The Flyer / Shooter.
	 */
	private Point flyer;
	
	/**
	 * The Stack of directions.
	 */
	private LinkedList<Direction> directions;
	
	/**
	 * The Clock instance for handling the game logic.
	 */
	private Clock logicTimer; 
	
	/**
	 * flag of game paused
	 */
	private boolean isPaused;
	
	/**
	 * flag of new game
	 */
	private boolean isNewGame;

	private ShooterGame(){
		super("Shooter Game");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
				
		/*
		 * Initialize the game's panels and add them to the window.
		 */
		this.board = new BoardPanel(this);
		add(board, BorderLayout.CENTER);
		
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {

				/*
				 * If the game is not paused, and the game is not over...
				 * 
				 * Ensure that the direction list is not full, and that the most
				 * recent direction is adjacent to North before adding the
				 * direction to the list.
				 */
				case KeyEvent.VK_W:
				case KeyEvent.VK_UP:
					if(!isPaused) {
						if(directions.size() < MAX_DIRECTIONS) {
							Direction last = directions.peekLast();
								directions.addLast(Direction.North);
						}
					}
					break;

				/*
				 * If the game is not paused, and the game is not over...
				 * 
				 * Ensure that the direction list is not full, and that the most
				 * recent direction is adjacent to South before adding the
				 * direction to the list.
				 */	
				case KeyEvent.VK_S:
				case KeyEvent.VK_DOWN:
					if(!isPaused) {
						if(directions.size() < MAX_DIRECTIONS) {
							Direction last = directions.peekLast();
								directions.addLast(Direction.South);
						}
					}
					break;
				
				/*
				 * If the game is not paused, and the game is not over...
				 * 
				 * Ensure that the direction list is not full, and that the most
				 * recent direction is adjacent to West before adding the
				 * direction to the list.
				 */						
				case KeyEvent.VK_A:
				case KeyEvent.VK_LEFT:
					if(!isPaused) {
						if(directions.size() < MAX_DIRECTIONS) {
							Direction last = directions.peekLast();
								directions.addLast(Direction.West);
						}
					}
					break;
			
				/*
				 * If the game is not paused, and the game is not over...
				 * 
				 * Ensure that the direction list is not full, and that the most
				 * recent direction is adjacent to East before adding the
				 * direction to the list.
				 */		
				case KeyEvent.VK_D:
				case KeyEvent.VK_RIGHT:
					if(!isPaused) {
						if(directions.size() < MAX_DIRECTIONS) {
							Direction last = directions.peekLast();
								directions.addLast(Direction.East);
						}
					}
					break;
				
				/*
				 * If the game is not over, toggle the paused flag and update
				 * the logicTimer's pause flag accordingly.
				 */
				case KeyEvent.VK_P:
					isPaused = !isPaused;
					logicTimer.setPaused(isPaused);
					break;
				
				/*
				 * Reset the game if one is not currently in progress.
				 */
				
				case KeyEvent.VK_ENTER:
					if(isNewGame) {
						resetGame();
					}
					break;
				}
			}
			
		});

		/*
		 * Resize the window to the appropriate size, center it on the
		 * screen and display it.
		 */
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	
	/**
	 * Gets the flag that indicates whether or not the game is paused.
	 * @return The paused flag.
	 */
	public boolean isPaused() {
		return isPaused;
	}
	
	/**
	 * Gets the flag that indicates whether or not we're playing a new game.
	 * @return The new game flag.
	 */
	public boolean isNewGame() {
		return isNewGame;
	}
	
	/**
	 * Start Game
	 */
	public void startGame(){
		this.flyer = new Point();
		this.isNewGame = true;
		this.directions = new LinkedList<>();
		this.logicTimer = new Clock(9.0f);
		
		//Set the timer to paused initially.
		logicTimer.setPaused(true);
		
		while(true){			
			long start = System.nanoTime();
			logicTimer.update();
			
			if (logicTimer.hasElapsedCycle()){
				updateGame();
			}
			
			//repaint board at FRAME_TIME rate
			board.repaint();
			
			long delta = (System.nanoTime() - start)/1000000L;  
			//change ns to ms
			
			if(delta<FRAME_TIME){
				try{
				Thread.sleep(FRAME_TIME-delta);
				}catch (Exception e){e.printStackTrace();}
			}
		}
	}
	
	public void updateGame(){
		TileType collision = updateFlyer(); //not used now
	}
	
	public TileType updateFlyer(){
		Direction direction = directions.peekFirst();
		TileType old = board.getTile(flyer.x,flyer.y);
		//need not process the original Tile now
		
		board.setTile(flyer, null);  //delete current position

		switch(direction){
		case North:
			if (flyer.y>0){
				flyer.y--;
			}
			break;
		case South:
			if(flyer.y<BoardPanel.ROW_COUNT-1){
				flyer.y++;
			}
			break;
		case East:
			if (flyer.x<BoardPanel.COL_COUNT){
				flyer.x++;
			}
			break;
		case West:
			if (flyer.x>0){
				flyer.x--;
			}
			break;
		}
		

		
		board.setTile(flyer,TileType.Flyer); //draw new position

		if (directions.size()>1){
			directions.poll();
		}
		return old;
	
	}

	public void resetGame(){
		/*
		 * Create the head at the center of the board.
		 */
		this.flyer = new Point(BoardPanel.COL_COUNT / 2, BoardPanel.ROW_COUNT / 2);

		/*
		 * Clear the board and add the head.
		 */
		board.clearBoard();
		board.setTile(flyer, TileType.Flyer);
		
		/*
		 * Clear the directions and add north as the
		 * default direction.
		 */
		directions.clear();
		directions.add(Direction.North);
		
		/*
		 * Reset the logic timer.
		 */
		logicTimer.reset();
	}
	
	public static void main(String[] args) {
		ShooterGame shooter = new ShooterGame();
		shooter.startGame();

	}
}
