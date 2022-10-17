
public class Piece {

	//colour is represented by the letter, colourCode is 0 or 1, 0 = black, 1 = white
	public String type, colour;
	public int colourCode;

	public Piece(String inType, int inColour) {
		type = inType;
		colourCode = inColour;
		if (inColour == 0) colour = "b";
		else colour = "w";
	}
}
