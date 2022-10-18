
public class Player {
	private String playerName;
	private int playerCode, kingX, kingY;
	private Piece[] activePieces = new Piece[16];
	private boolean kingCanCastle = true, rightRookCanCastle = true, leftRookCanCastle = true;
	
	public Player(String name, int code, int x, int y) {
		playerName = name;
		playerCode = code;
		kingX = x;
		kingY = y;
	}
	
	public void cannotCastle(String type, String rook) {
		if (type.equals("K")) kingCanCastle = false;
		else if (rook == "right") rightRookCanCastle = false;
		else leftRookCanCastle = false;
		
	}
	
	public boolean canCastle(int xDifference) {
		if (kingCanCastle) {
			if (xDifference == 2 && leftRookCanCastle) return true;
			else if (xDifference == -2 && rightRookCanCastle) return true;
			else return false;
		} else return false;
	}
	
	public int code() {
		return playerCode;
	}
	
	public void addPiece(Piece piece, int reference) {
		activePieces[reference] = piece;
	}

	public void removePiece(int reference) {
		activePieces[reference] = null;
	}
	
	public Piece selectPiece(int reference) {
		return activePieces[reference];
	}
	
	public int findKing(String axis) {
		if (axis.equals("x")) return kingX;
		else return kingY;
	}
	
	public void moveKing(int x, int y) {
		kingX = x;
		kingY = y;
	}
	
	public void printList() {
		for (int i = 0; i < 16; i++) {
			if (activePieces[i] != null) System.out.print(activePieces[i].getType());
		}
	}
}
