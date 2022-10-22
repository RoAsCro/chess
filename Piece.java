/**
 * Pieces for the Chess game.
 * Provides methods to access and update the type and coordinates.
 *
 */
public class Piece {

	//colour is represented by the letter, colourCode is 0 or 1, 0 = black, 1 = white
	//DEPRECATE COLOUR, not used in the code
	private String type;
	private final int colourCode;
	//Coordinates are handled through the grid, x, y, and list reference exist purely for the check/stalemate checks to save iterating through the whole grid
	private int x, y, listReference;
	private final Player player;
	
	

	public Piece(String inType, int inColour, int inX, int inY, int inListReference, Player inPlayer) {
		player = inPlayer;
		System.out.println(player.code());
		type = inType;
		colourCode = inColour;
		x = inX;		
		y = inY;
		listReference = inListReference;
	}
	
	public int getColour() {
		return colourCode;
	}
	
	public boolean isPlayer(Player player) {
		if (colourCode == player.code()) {
			return true;
		}
		else return false;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean checkType(String input) {
		if (input.equals(type)) return true;
		else return false;
	}
	
	public void setCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
				
	}
	
	public int getXY(String axis) {
		if (axis.equals("x")) return x;
		else return y;
	}
	
	public int getRef() {
		return listReference;
	}
	
	public void beTaken() {
		player.removePiece(listReference);
	}
	
	public void promote(String type) {
		this.type = type;
	}
}
