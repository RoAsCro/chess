
public class Piece {

	//colour is represented by the letter, colourCode is 0 or 1, 0 = black, 1 = white
	//DEPRECATE COLOUR, not used in the code
	public String type, colour;
	public int colourCode;
	//Coordinates are handled through the grid, x, y, and list reference exist purely for the check/stalemate checks to save iterating through the whole grid
	public int x, y, listReference;

	public Piece(String inType, int inColour, int inX, int inY, int inListReference) {
		type = inType;
		colourCode = inColour;
		x = inX;		
		y = inY;
		listReference = inListReference;
		if (inColour == 0) colour = "b";
		else colour = "w";
	}
}
