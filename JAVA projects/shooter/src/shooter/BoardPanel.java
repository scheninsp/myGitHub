package shooter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import shooter.TileType;

public class BoardPanel extends JPanel{
	
	final static int ROW_COUNT = 40;
	final static int COL_COUNT = 40;
	final static int TILE_SIZE = 20;

	private ShooterGame game;
	private TileType tiles[];
	
	public BoardPanel (ShooterGame game){
		this.game = game;
		this.tiles = new TileType[ROW_COUNT * COL_COUNT];
		
		setPreferredSize(new Dimension(COL_COUNT * TILE_SIZE, ROW_COUNT * TILE_SIZE));
		setBackground(Color.WHITE);
	}
	
	public void clearBoard(){
		for(int i=0;i<tiles.length;i++){
			tiles[i]=null;
		}
	}
	
	public TileType getTile(int x, int y){
		return tiles[y*ROW_COUNT+x];
	}
	
	public void setTile(Point point, TileType type){
		setTile(point.x,point.y,type);
	}
	
	public void setTile(int x, int y, TileType type){
		tiles[y*ROW_COUNT+x]=type;
	}
	
	
	//paint
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		/*
		 * Loop through each tile on the board and draw it if it
		 * is not null.
		 */
		for(int x = 0; x < COL_COUNT; x++) {
			for(int y = 0; y < ROW_COUNT; y++) {
				TileType type = getTile(x, y);
				if(type != null) {
					drawTile(x * TILE_SIZE, y * TILE_SIZE, type, g);
				}
			}
		}
	}
	
	public void drawTile(int x, int y, TileType type, Graphics g){
		
		switch(type){
			case Flyer:
				int vertices_x[] = {x+TILE_SIZE/2,x+2,x+TILE_SIZE-2};
				int vertices_y[] = {y,y+TILE_SIZE,y+TILE_SIZE};
				g.fillPolygon(vertices_x,vertices_y,vertices_x.length);
				break;
			
			case Target:
				g.setColor(Color.RED);
				g.fillOval(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
				break;
		
		}
	}
	
}
