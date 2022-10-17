
public class Player {
	private String playerName;
	private int playerCode;
	private Piece[] activePieces = new Piece[16];
	private boolean kingCanCastle = true, rightRookCanCastle = true, leftRookCanCastle = true;
	
	public Player(String name, int code) {
		playerName = name;
		playerCode = code;
	}
	
	public void cannotCastle(String type) {
		
	}

}
