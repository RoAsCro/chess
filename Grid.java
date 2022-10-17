public class Grid {
	
	Piece[][] grid = new Piece[8][8];
	int kingLocationBY = 0, kingLocationBX = 4, kingLocationWY = 7, kingLocationWX = 4, 
			enPassantX = -1, enPassantY = -1,
			currentPlayer = 0;
	boolean enPassantFlag, enPassantTake, whiteCastleK = true, blackCastleK = true, 
			whiteCastleRookLeft = true, whiteCastleRookRight = true, blackCastleRookLeft = true, blackCastleRookRight = true,
			castleFlag, checking,
			go = true;
	
	public static void main(String[] args) {
		Grid game = new Grid();
		game.run();
		
	}
		
		
		////Starting variables
		
	
	void run() {	
			//Initialise the grid
			String[] orderOne = {"R", "N", "B", "Q", "K", "B", "N", "R"};
			//String[] orderOne = {"R", "N", "N", "N", "K", "N", "N", "R"}
			
			for (int j = 0; j < 8; j++) {
				int col = 0;
				if (j > 5) col = 1;
				if (j < 2 || j > 5) {
					for (int i = 0; i < 8; i++) {
			
						if (j == 1 || j == 6) {
							//System.out.print("");
							grid[j][i] = new Piece("P", col);
			
						} else /*if (i == 0 || i == 4 || i == 7 || i == 3|| i == 5)*/ {
							grid[j][i] = new Piece(orderOne[i], col);
			
						}
			
					}
				}
			}
			
			//Set king location
			
			
			checkCheckmate("N", 1, 0);
			
			//Loop while playing
			while (go) {
				printGrid();
				System.out.println("Player " + currentPlayer + " is in check? " + checkCheck());
				currentPlayer = Math.abs(currentPlayer - 1);
				System.out.println("Player " + currentPlayer + " is in check? " + checkCheck());
				//Piece selection formatted xy - 11, 14 etc.
				
				
				boolean loopDecision = true;
				while (loopDecision) {
					if (decideMove()) loopDecision = false;
				 }
			}
	}
	
	void printGrid() {
		String visualGrid = "";
		// y is used to  print the y axis labels
		int y = 8;
		for (Piece[] p : grid) {
	
			visualGrid = visualGrid + "\n" + "[" + y + "]";
			y--;
	
			for (Piece q : p) {
	
				visualGrid = visualGrid + "[ ";
				if (q != null) {
					visualGrid = visualGrid + q.type + q.colour;
	
				} else visualGrid = visualGrid + "  ";
				visualGrid = visualGrid + " ]";
			}
		}
		//print x axis labels
		visualGrid = visualGrid + "\n    [1]   [2]   [3]   [4]   [5]   [6]   [7]   [8]";
		System.out.println(visualGrid);
	}
	
	 boolean collision (int yIncrement, int xIncrement, int difference, int targetX, int targetY, Piece selectedPiece) {
		int j = 0;
	
		for (int i = 0; i != difference && j != difference;) {
			//CHECK IF YOU CAN REMOVE WHAT COMES AFTER THE OR - I think it's supposed to check the target piece is not one of the current player's, which is handled elsewhere now
			if (!(grid[targetY + i][targetX + j] == null || (i == 0 && grid[targetY + i][targetX + j] != null && grid[targetY + i][targetX + j].colour != selectedPiece.colour))) {
				return false;
			}
	
			i += yIncrement;
			j += xIncrement;
	
		}
		return true;
	}
	
	
	 boolean movePiece(int startX, int startY, int targetX, int targetY) {
		castleFlag = false;
		Piece selectedPiece = grid[startY][startX];
		String pieceType = selectedPiece.type;
		int xDifference = startX - targetX, yDifference = startY - targetY, xIncrement = 1, yIncrement = 1, angle = 0;
		Piece targetLocation = grid[targetY][targetX];
		
		if (startY != targetY) angle += 1;
		if (startX != targetX) angle += 2;
	
		//Piece is not attempting to move where it already is
		if (angle == 0) return false;
		
		//Target location is not occupied by one of current player's pieces
		if (targetLocation != null && targetLocation.colourCode == currentPlayer) {
			return false;
		}
		
		//Check the piece moves like that
		if (!moveCheck(xDifference, yDifference, pieceType, angle, targetLocation, startY, startX)) return false;
		
		//Check if piece in the way
		if (!selectedPiece.type.equals("N")) {
			
			if (xDifference < 0) xIncrement = -1;
			if (yDifference < 0) yIncrement = -1;
			
			// if diagonal (angle = 3), else if horizontal (2), else if vertical (1)
			switch(angle) {
			case 3:
				if (!collision(yIncrement, xIncrement, xDifference, targetX, targetY, selectedPiece)) return false;
				break;
				
			case 2:
				if (!collision(0, xIncrement, xDifference, targetX, targetY, selectedPiece)) return false;
				break;
	
			case 1:
				if (!collision(yIncrement, 0, yDifference, targetX, targetY, selectedPiece)) return false;
				break;
			}
			
		}
		
		
		
		//Checks passed!
		//Move the piece
		if (castleFlag) {
			changeCoordinates(startX, startY, targetX + (xDifference / 2), targetY);
			if (checkCheck()) {
				changeCoordinates(targetX + (xDifference / 2), targetY, startX, startY);
				return false;
			} else changeCoordinates(targetX + (xDifference / 2), targetY, startX, startY);
			
		}
		changeCoordinates(startX, startY, targetX, targetY);
		//if it was an en passant, make sure the pawn is removed
		Piece passantPawn = null;
		if (enPassantTake) {
			passantPawn = grid[targetY + yDifference][targetX];
			grid[targetY + yDifference][targetX] = null;
		}
		// If this movement results in check, move the piece back and return false
		if (checkCheck()) {
			changeCoordinates(targetX, targetY, startX, startY);
			grid[targetY][targetX] = targetLocation;
			if (enPassantTake) grid[targetY + yDifference][targetX] = passantPawn;
			return false;
		}
		//BELOW MUST ONLY TAKE PLACE IF A MOVE IS SUCCESSFULLY MADE
		if (checking) {
			//System.out.println("Confirmed");
			changeCoordinates(targetX, targetY, startX, startY);
			grid[targetY][targetX] = targetLocation;
			if (enPassantTake) grid[targetY + yDifference][targetX] = passantPawn;
			}
		//Allow for en passant next move
		
		if (enPassantFlag && !checking) {
			enPassantX = targetX;
			enPassantY = targetY + (yDifference / 2);
			enPassantFlag = false;
		} else {
			enPassantX = -1;
			enPassantY = -1;
		}
		//Make it impossible to castle after moving the king
		if (selectedPiece.type.equals("K") && !checking) {
			if (currentPlayer == 0) blackCastleK = false;
			else whiteCastleK = false;
		}
		//Make it impossible to castle with a rook after moving it
		if (selectedPiece.type.equals("R") && !checking) {
			int startXY = startX + startY;
			if (currentPlayer == 0) {
				if (startXY == 0) blackCastleRookRight = false;
				if (startXY == 7) blackCastleRookLeft = false;
			} else {
				if (startXY == 14) whiteCastleRookRight = false;
				if (startXY == 7) whiteCastleRookLeft = false;
			}
		}
		
		//Move the rook if castling takes place
		if (castleFlag && !checking) {
			if (currentPlayer == 0) {
				if (targetX == 2) {
					grid[0][3] = grid[0][0];
					grid[0][0] = null;
				} else if (targetX == 6) {
					grid[0][5] = grid[0][7];
					grid[0][7] = null;
				}
			} else {
				if (targetX == 2) {
					grid[7][3] = grid[7][0];
					grid[7][0] = null;
				} else if (targetX == 6) {
					grid[7][5] = grid[7][7];
					grid[7][7] = null;
				}
			}
			castleFlag = false;
		}
		//
		enPassantTake = false;
			
		return true;
	}
	
	 void changeCoordinates(int startX, int startY, int targetX, int targetY) {
		Piece selectedPiece = grid[startY][startX];
		
		if (selectedPiece.type.equals("K")) {
			if (selectedPiece.colourCode == 0) {
				kingLocationBX = targetX;
				kingLocationBY = targetY;
			} else {
				kingLocationWX = targetX;
				kingLocationWY = targetY;
			}
		}
	
		grid[targetY][targetX] = selectedPiece;
		grid[startY][startX] = null;
	}
	
	
	//Check the piece moves like that
	 boolean moveCheck(int xDifference, int yDifference, String pieceType, int angle, Piece targetLocation, int startY, int startX) {
	
		int addedDifference = Math.abs(yDifference) + Math.abs(xDifference);
		
		// angle 3 is diagonal, < 3 is straight
		if (angle == 3) {
			if (Math.abs(xDifference) != Math.abs(yDifference) && !pieceType.equals("N")) return false; 
			
			else if (pieceType.equals("K") && Math.abs(yDifference) == 1) return true;
			else if (pieceType.equals("P") && pawnCheck(yDifference)) {
				System.out.println("X = " + enPassantX);
				if (targetLocation != null) return true;
				else if (enPassantX != -1 && startX - xDifference == enPassantX && startY - yDifference == enPassantY) {
					//KILL PAWN CODE
					//Location of killed pawn = targetY + yDifference
					enPassantTake = true;
					return true;
				}
				else return false;
				
			} else if (pieceType.equals("N") && addedDifference == 3) return true; 
			
			else if (!(pieceType.equals("B") || pieceType.equals("Q"))) {
				return false;
			}
			
		} else if (angle < 3) {
	
			if (pieceType.equals("P")) {
				if (pawnCheck(yDifference)) return true; 
				else if ((currentPlayer == 0 && startY == 1 && yDifference == -2) || (currentPlayer == 1 && startY == 6 && yDifference == 2)) {
					enPassantFlag = true;
					return true;
					
				} else {
					return false;
				}
			}
		
			else if (pieceType.equals("K")) {
				if (addedDifference == 1) return true;
				//Generalise the below to a function?
				else if (currentPlayer == 0) {
					if (blackCastleK && ((xDifference == 2 && blackCastleRookRight && grid[startY][startX - 1] == null && grid[startY][startX - 2] == null && grid[startY][startX - 3] == null) || (xDifference == -2 && blackCastleRookLeft && grid[startY][startX + 1] == null && grid[startY][startX + 2] == null)) && !checkCheck()) {
						castleFlag = true;
						return true;
					}
					else return false;
				}else if (currentPlayer == 1) {
					if (whiteCastleK && ((xDifference == 2 && whiteCastleRookLeft && grid[startY][startX - 1] == null && grid[startY][startX - 2] == null && grid[startY][startX - 3] == null) || (xDifference == -2 && whiteCastleRookRight && grid[startY][startX + 1] == null && grid[startY][startX + 2] == null)) && !checkCheck()) {
						castleFlag = true;
						return true;
					}
					else return false;
				}else return false;
			}
			
			else if (!(pieceType.equals("R") || pieceType.equals("Q"))) return false;
		}
		return true;
	}
	
	//If piece is a pawn, check it's moving in the right direction
	 boolean pawnCheck(int yDifference) {
		if ((currentPlayer == 0 && yDifference != -1) || (currentPlayer == 1 && yDifference != 1)) return false;
		else return true;
	}
	
	
	//Check if in check
	 boolean checkCheck() {
		boolean checking = true;
		int startX, startY;
		if (currentPlayer == 0) {
			startX = kingLocationBX;
			startY = kingLocationBY;
		} else {
			startX = kingLocationWX;
			startY = kingLocationWY;
		}
		int testVertical = 0, testHorizontal = 1;
			
		for (int j = 0; checking; j++){
			for (int i = 1; ; i++) {
				int targetY = startY + i * testVertical, targetX = startX + (i * testHorizontal);
				if (j == 8) {
					switch(i) {
						case 1:
							targetX = startX + 1;
							targetY = startY + 2;
							break;
						case 2:
							targetX = startX + 1;
							targetY = startY - 2;
							break;
						case 3:
							targetX = startX - 1;
							targetY = startY - 2;
							break;
						case 4:
							targetX = startX - 1;
							targetY = startY + 2;
							break;
						case 5:
							targetX = startX - 2;
							targetY = startY - 1;
							break;
						case 6:
							targetX = startX - 2;
							targetY = startY + 1;
							break;
						case 7:
							targetX = startX + 2;
							targetY = startY + 1;
							break;
						case 8:
							targetX = startX + 2;
							targetY = startY - 1;
							break;
					}
				}
				if (targetY > -1 && targetY < 8 && targetX > -1 && targetX < 8) {
					if (grid[targetY][targetX] != null) {
						
						if (grid[targetY][targetX].colourCode != currentPlayer) {
							if ((threatCheck(grid[targetY][targetX].type, targetX, targetY, j, startX, startY))) return true;
						}
						if (j != 8) break;
					}
				} else if (j != 8) {
					break;
				}
				if (j == 8 && i == 8) break;
				
			}
			switch(j) {
				case 0:
					testHorizontal = -1;
					break;
				case 1:
					testHorizontal = 0;
					testVertical = 1;
					break;
				case 2:
					testVertical = -1;
					break;
				case 3:
					testVertical = 1;
					testHorizontal = 1;
					break;
				case 4:
					testVertical = -1;
					testHorizontal = -1;
					break;
				case 5:
					testVertical = 1;
					testHorizontal = -1;
					break;
				case 6:
					testVertical = -1;
					testHorizontal = 1;
					break;
				case 7:
					break;
				case 8:
					checking = false;
					break;
			}
		}
		return false;
	}
	
	
	
	
		
	boolean checkCheckmateIter() {
		
		return true;
	}
	
	 boolean checkCheckmate(String type, int startX, int startY) {
		checking = true;
		int targetX = 0, targetY = 0, startJ = -1, iterMax = 8, directions = 3, yMax = 1;
		boolean knight = false;
		switch(type) {
			case "P":
				startJ = currentPlayer * 2 - 1;
				yMax = startJ;
				directions = 3;
				iterMax = 3;
				break;
			case "R":
				directions = 1;
				break;
			case "B":
				directions = 2;
				break;
			case "Q":
				break;
			case "K":
				directions = 3;
				iterMax = 2;
				break;
			case "N":
				knight = true;
				break;	
		}
		
		
		if (!knight) {		
			for (int i = -1; i <= 1; i++) {
				//yIterator:
				for (int j = startJ; j <= yMax; j++) {
					if ((directions != 3 && Math.abs(i) + Math.abs(j) != directions) || Math.abs(i) + Math.abs(j) == 0) continue;
					System.out.println("I = " + i + " J = " + j);
					for (int k = 1; k < iterMax; k++) {
						targetX = startX - k * i;
						targetY = startY - k * j;
						System.out.println("TargetX = " + targetX + " TargetY + " + targetY);
						if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) break;
						if (movePiece(startX, startY, targetX, targetY)) {
							System.out.println("isFine");
							/*checking = false;
							return false;*/
						}
						if (grid[targetY][targetX] != null) break;
						
					}
					
				}
			}
		} else {
			for (int i = -2; i < 3; i++) {
				if (i == 0) continue;
				for (int j = 0; j <= 1; j++) {
					targetX = startX - i;
					targetY = (j == 0 ? -1 : +1) * (startY - (Math.abs(i) < 2 ? i * 2 : i / 2));
					System.out.println( "X = " + targetX + " Y = " + targetY);
					if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) continue;
					if (movePiece(startX, startY, targetX, targetY)) {
						System.out.println("isFine");
						/*checking = false;
						return false;*/
					}
				}
			}
		}
		checking = false;
		return true;
	}
	
	
	 boolean threatCheck(String threatType, int threatX, int threatY, int threatAngle, int kingX, int kingY) {
		//System.out.println("ANGLE = " + threatAngle + " THREAT X = " + threatX + " Y = " + threatY + " type = " + threatType);
		if ((threatAngle <= 3) && (threatType.equals("R") || (threatType.equals("K") && Math.abs(kingY - threatY) <= 1 && Math.abs(kingX - threatX) <= 1) || threatType.equals("Q"))) {
			return true;
		} else if ((threatAngle <= 7 && threatAngle > 3) && (threatType.equals("B") || (threatType.equals("K") && Math.abs(kingY - threatY) == 1 && Math.abs(kingX - threatX) == 1) || threatType.equals("Q") || threatType.equals("P"))) {
			if (threatType.equals("P")) {
				//Can probably use pawnCheck for this
				if ((currentPlayer == 0 && kingY - threatY == -1) || (currentPlayer == 1 && kingY - threatY == 1)) {
					return true;
				}
			} else return true;
		} else if (threatAngle == 8 && threatType.equals("N")) {
			return true;
		}
		return false;	
	}
	
	//Used to check all possible moves to check the collision function is working properly
	 void testMove(int startX, int startY) {
	
		Piece selectedPiece = grid[startY][startX];
		//Test the Y axis
		// I shouldn't need to repeat the for loop
		
		//While the testHorizontal/Vertical variables equal 1, that will be tested.
		int testVertical = 0, testHorizontal = -1;
		boolean testing = true;
		for (; testing == true; testVertical++, testHorizontal++) {
			if (testVertical > 2) {
				testing = false;
				testVertical = -3;
				testHorizontal = 1;
			}
			for (int i = 7; i > -8; i--) {
				int targetY = startY + (i * ((testVertical + 1) / 2)), targetX = startX + (i * ((Math.abs(testHorizontal) + 1) / 2));
	
				//Test that the target location is on the grid
				
				if (targetX < 8 && targetX > -1 && targetY < 8 && targetY > -1) {
					int xDifference = startX - targetX, yDifference = startY - targetY, xIncrement = 1, yIncrement = 1;
					if (xDifference < 0) xIncrement = -1;
					if (yDifference < 0) yIncrement = -1;
					if (startX != targetX && startY != targetY) {
						if (!collision(yIncrement, xIncrement, xDifference, targetX, targetY, selectedPiece)) System.out.println((Math.abs(targetX + 1)) + "," + (Math.abs(8 - targetY)) + " is not a valid move.");
					}else if (startY != targetY) {
						if (!collision(yIncrement, 0, yDifference, targetX, targetY, selectedPiece)) System.out.println((Math.abs(targetX + 1)) + "," + (Math.abs(8 - targetY)) + " is not a valid move.");
					}else if (startX != targetX) {
						if (!collision(0, xIncrement, xDifference, targetX, targetY, selectedPiece)) System.out.println((Math.abs(targetX + 1)) + "," + (Math.abs(8 - targetY)) + " is not a valid move.");
	
					}
				}
			}
		}
	}
	
	
	 boolean decideMove() {
		int startX = 0, startY = 0, targetX = 0, targetY = 0;
		boolean selectPiece = true;		
		while (selectPiece) {
			System.out.print("Select a piece: ");
			String targetPiece = System.console().readLine();
			System.out.println();
			if (targetPiece.length() != 2 || !Character.isDigit(targetPiece.charAt(0)) || !Character.isDigit(targetPiece.charAt(1))) {
				System.out.println("Sorry, that's not a valid input.");
				continue;
			}
			startX = Math.abs(Integer.parseInt(targetPiece.substring(0,1)) - 1);
			startY = Math.abs(Integer.parseInt(targetPiece.substring(1,2)) - 8);
	
			//Check valid piece
			if (grid[startY][startX] == null || grid[startY][startX].colourCode != currentPlayer) {
				System.out.println("Sorry, that's not a valid piece.");
			} else selectPiece = false;
		}
		boolean selectTarget = true;
		while (selectTarget) {
			System.out.print("Select where to move it: ");
			String targetLocation = System.console().readLine();
			System.out.println();
			// TEST will return a list of invalid moves in the horizontal, vertical, and diagonal directions. BACK will return to selec a piece.
			if (targetLocation.equals("TEST")) {
				testMove(startX, startY);
				continue;
			} else if (targetLocation.equals("BACK")) {
				return false;
			} else if (targetLocation.length() != 2 || !Character.isDigit(targetLocation.charAt(0)) || !Character.isDigit(targetLocation.charAt(1))) {
				System.out.println("Sorry, that's not a valid input.");
				continue;
			}/* else try {
				Integer.parseInt(targetLocation);
			
			} catch(NumberFormatException)*/
			targetX = Math.abs(Integer.parseInt(targetLocation.substring(0,1)) - 1);
			targetY = Math.abs(Integer.parseInt(targetLocation.substring(1,2)) - 8);
			
			//Go through the checks that it's a valid move, and if it is then move the piece and go to the next move. If not, come back here.
			if (movePiece(startX, startY, targetX, targetY)) selectTarget = false;
			else System.out.println("Sorry, that's not a valid move.");
		}
		return true;
	}
}
