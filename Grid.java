public class Grid {
	
	Player white = new Player("White", 1, 4, 7), black = new Player("Black", 0, 4, 0);
	Player[] players = {black, white};
	Piece[][] grid = new Piece[8][8];
	Player currentPlayer = players[0];
	//Lists of untaken pieces
	//Where the king currently is - might be redundant with the lists
	int 
			//Where an en passant is happening - (-1,-1) is nowhere
			enPassantX = -1, enPassantY = -1;
	//Flags for if an en passant happened last turn, if something needs to be removed if the en passant is successful
	boolean enPassantFlag, enPassantTake, 
			//Whether a castle is attempting to take place, whether the game is checking for checkmate and not making real moves
			castleFlag, checking,
			go = true;
	
	public static void main(String[] args) {
		Grid game = new Grid();
		game.run();
		
	}
		
		
		////Starting variables
		
	
	void run() {
			//Initialise the two players
		
			//Initialise the grid
			String[] orderOne = {"R", "N", "B", "Q", "K", "B", "N", "R"};
			//String[] orderOne = {"R", "N", "N", "N", "K", "N", "N", "R"}
			
			for (int j = 0; j < 8; j++) {
				int col = 0;
				if (j > 5) col = 1;
				if (j < 2 || j > 5) {
					for (int i = 0; i < 8; i++) {
			
						if (j == 1 || j == 6) {
							grid[j][i] = new Piece("P", col, i, j, i, col == 0 ? black : white);
							
							//addSelf method maybe?
							if (j == 1) {
								black.addPiece(grid[j][i], i);
							} else  white.addPiece(grid[j][i], i);
							
			
						} else if (i == 0 || i == 4 || i == 7 /*|| i == 3|| i == 1*/) {
							grid[j][i] = new Piece(orderOne[i], col, i, j, i+8, col == 0 ? black : white);
							if (j == 0) {
								black.addPiece(grid[j][i], i+8);
							} else  white.addPiece(grid[j][i], i+8);
			
						}
			
					}
				}
			}
			
			
			
			//Loop while playing
			while (go) {
				currentPlayer.printList();
				printGrid();
				currentPlayer = players[Math.abs(currentPlayer.code() - 1)];
				if (checkCheckmateIter()) {
					if (checkCheck()) System.out.println("Chcekmate! Game Over!");
					else System.out.println("Stalemate! Game Over!");
					go = false;
					break;	
				}
				/*currentPlayer = players[Math.abs(currentPlayer.code() - 1)];
				if (checkCheckmateIter()) {
					System.out.println("Stalemate! Game Over!");
					go = false;
					break;
				}*/
				//System.out.println("Player " + currentPlayer + " is in check? " + checkCheck());
				//currentPlayer = players[Math.abs(currentPlayer.code() - 1)];
				//System.out.println("Player " + currentPlayer + " is in check? " + checkCheck());
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
	
				visualGrid = visualGrid + "[";
				if (q != null) {
					visualGrid = visualGrid + q.getType() + q.getColour();
	
				} else visualGrid = visualGrid + "  ";
				visualGrid = visualGrid + "]";
			}
		}
		//print x axis labels
		visualGrid = visualGrid + "\n   [1] [2] [3] [4] [5] [6] [7] [8]";
		System.out.println(visualGrid);
	}
	
	 boolean collision (int yIncrement, int xIncrement, int difference, int targetX, int targetY, Piece selectedPiece) {
		int j = 0;
	
		for (int i = 0; i != difference && j != difference;) {
			//CHECK IF YOU CAN REMOVE WHAT COMES AFTER THE OR - I think it's supposed to check the target piece is not one of the current player's, which is handled elsewhere now
			if (!(grid[targetY + i][targetX + j] == null || (i == 0 && grid[targetY + i][targetX + j] != null && grid[targetY + i][targetX + j].getColour() != selectedPiece.getColour()))) {
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
		String pieceType = selectedPiece.getType();
		int xDifference = startX - targetX, yDifference = startY - targetY, xIncrement = 1, yIncrement = 1, angle = 0;
		Piece targetLocation = grid[targetY][targetX];
		
		if (startY != targetY) angle += 1;
		if (startX != targetX) angle += 2;
	
		//Piece is not attempting to move where it already is
		if (angle == 0) return false;
		//Target location is not occupied by one of current player's pieces
		if (targetLocation != null && targetLocation.isPlayer(currentPlayer)) return false;
		
		//Check the piece moves like that
		if (!moveCheck(xDifference, yDifference, pieceType, angle, targetLocation, startY, startX)) return false;
		
		//Check if piece in the way
		if (!pieceType.equals("N")) {
			
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
		
		
		
		
		//Check not castling through check
		if (castleFlag) {
			changeCoordinates(startX, startY, targetX + (xDifference / 2), targetY);
			if (checkCheck()) {
				changeCoordinates(targetX + (xDifference / 2), targetY, startX, startY);
				return false;
			} else changeCoordinates(targetX + (xDifference / 2), targetY, startX, startY);
			
		}
		//Checks passed!
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
		
		//If this is during a checkmate check, put the board back to how it was. If not, finishing touches on the move
		if (checking) {
			//System.out.println("Confirmed");
			changeCoordinates(targetX, targetY, startX, startY);
			grid[targetY][targetX] = targetLocation;
			if (enPassantTake) grid[targetY + yDifference][targetX] = passantPawn;
			}
		
		//remove pieces from the list of active pieces
		else {
			//Set the moving piece's internal coordinates and remove the taken piece from the list of active pieces, then do the same for an en passant-ed piece
			selectedPiece.setCoordinates(targetX, targetY);
			if (targetLocation != null) {
				targetLocation.beTaken();
			}
			if (enPassantTake) {
				passantPawn.beTaken();
			}
		
			//Allow for en passant next move
			if (enPassantFlag) {
				enPassantX = targetX;
				enPassantY = targetY + (yDifference / 2);
				enPassantFlag = false;
			} else if (!checking) {
				enPassantX = -1;
				enPassantY = -1;
			}
			
			if (selectedPiece.checkType("P") && targetY == Math.abs(currentPlayer.code() - 1) * 7) {
				while (1 == 1) {
					System.out.println("Select a piece to promote to:\nR = Rook\nB = Bishop\nN = Knight\nQ = Queen");
					String input = System.console().readLine();
					System.out.println(input);
					System.out.println(input.length());
					if (input.length() != 1 || (!input.equals("R") && !input.equals("B") && !input.equals("N") && !input.equals("Q"))) {
						System.out.println("Sorry, that's not a valid input");
						continue;
					} else {
						selectedPiece.promote(input);
						break;
					}
				}
			}
			
			//Make it impossible to castle after moving the king
			if (selectedPiece.checkType("K")) {
				currentPlayer.cannotCastle("K", "");
			}
			//Make it impossible to castle with a rook after moving it
			else if (selectedPiece.checkType("R")) {
				
				if (startY == currentPlayer.code() * 7 && startX == 0 || startY == 7) {
					currentPlayer.cannotCastle("R", startX == 0 ? "left" : startX == 7 ? "right" : "");
				}
			}
			
			//Move the rook if castling takes place - NOTE: there is no conceivable situation where the rook's movement alone would affect check checking
			if (castleFlag) {
				//Castle X - where the rook is to move, Rook X - where the rook in question is located
				int castleX = targetX - (targetX / 2 - 2), rookX = (targetX - 2) / 4 * 7;
				grid[startY][rookX].setCoordinates(castleX, startY);
				changeCoordinates(rookX, startY, castleX, startY);			
				castleFlag = false;
			}
		}
		enPassantTake = false;
			
		return true;
	}
	
	 void changeCoordinates(int startX, int startY, int targetX, int targetY) {
		Piece selectedPiece = grid[startY][startX];
		
		if (selectedPiece.checkType("K")) {
			currentPlayer.moveKing(targetX, targetY);
		};
		grid[targetY][targetX] = selectedPiece;
		grid[startY][startX] = null;
	}
	
	
	//Check the piece moves like that
	 boolean moveCheck(int xDifference, int yDifference, String pieceType, int angle, Piece targetLocation, int startY, int startX) {
	
		int addedDifference = Math.abs(yDifference) + Math.abs(xDifference);
		
		// angle 3 is diagonal, < 3 is vertical/horizontal
		if (angle == 3) {
			//Check this is an x = y or -x = y diagonal
			if (Math.abs(xDifference) != Math.abs(yDifference) && !pieceType.equals("N")) return false; 
			
			//Check king is moving only one space - since this is diagonal, the abs(yDifference) will be 1, and xDifference will be the same as y.
			else if (pieceType.equals("K") && Math.abs(yDifference) == 1) return true;
			
			//Check: 1. Pawn is going in the right direction 2a. There is a piece for the pawn to take, or 2b. An en passant can be made there.
			else if (pieceType.equals("P") && pawnCheck(yDifference)) {
				if (targetLocation != null) return true;
				else if (enPassantX != -1 && startX - xDifference == enPassantX && startY - yDifference == enPassantY) {
					enPassantTake = true;
					return true;
				}
				else return false;
			
			//Since we're in the diagonal condition, the only way for the sum of the abs of the differences to be 3 is if one is 1 and the other is 2
			} else if (pieceType.equals("N") && addedDifference == 3) return true; 
			
			//If no other conditions are met, and it's not a bishop or queen, then the piece cannot move
			else if (!(pieceType.equals("B") || pieceType.equals("Q"))) {
				return false;
			}
			
		} else if (angle < 3) {
			//Check: 1. Pawn is moving in the right direction && there's no piece at all in the way, or 2. The pawn is in its starting position and moving 2 spaces
			if (pieceType.equals("P")) {
				if (pawnCheck(yDifference) && targetLocation == null) return true; 
				else if (startY == currentPlayer.code() * 5 + 1 && Math.abs(yDifference) == 2) {
					enPassantFlag = true;
					return true;
					
				} else return false;
			}
			//As this is horizontal or vertical, the sum of the abs of the differences must be 1 for the king to move. Else castle checks
			else if (pieceType.equals("K")) {
				if (addedDifference == 1) return true;
				//If the king hasn't moved, and the rook in the xDifference direction hasn't moved 
				//&& the space between king and target location is empty && if castling to the queen side, the space between the target location and the rook is empty
				//&& king is not in check
				else if (currentPlayer.canCastle(xDifference) && grid[startY][startX - xDifference / 2] == null && grid[startY][startX - xDifference - 1] == null && !checkCheck()) {
					castleFlag = true;
					return true;
				} else return false;
			}
			//If no other conditions are fulfilled, if it's not a rook or queen, then the piece does not move like that.
			else if (!(pieceType.equals("R") || pieceType.equals("Q"))) return false;
		}
		return true;
	}
	
	//If piece is a pawn, check it's moving in the right direction
	 boolean pawnCheck(int yDifference) {
		if (!(currentPlayer.code() * 2 - 1 == yDifference)) return false;
		else return true;
	}
	
	
	//Check if in check
	 boolean checkCheck() {
		//boolean checking = true;
		int startX = currentPlayer.findKing("x"), startY = currentPlayer.findKing("y");
		//int testVertical = 0, testHorizontal = 1;
		
		if (!iterateDirection(-1, 1, 3, 8, startX, startY, false, "Check")) return true;
		return false;
		
		/*
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
						
						if (!(grid[targetY][targetX].isPlayer(currentPlayer))) {
							if ((threatCheck(grid[targetY][targetX].getType(), targetX, targetY, j, startX, startY))) return true;
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
		return false;*/
	}
	
	
	
	
		
	boolean checkCheckmateIter() {
		for (int i = 0 ; i < 16; i++) {
			Piece piece = currentPlayer.selectPiece(i);
			if (piece != null) {
				if (!checkCheckmate(piece.getType(), piece.getXY("x"), piece.getXY("y"))) return false;			
			}
		}
		//if no pieces disprove that you're in checkmate, you're in checkmate
		return true;
	}
	
	 boolean checkCheckmate(String type, int startX, int startY) {
		//checking is a flag to ensure nothing permanant happens during this check.
		//Note - there is no situation where castling alone would prevent checkmate or stalemate
		checking = true;
		int /*targetX = 0, targetY = 0,*/ startJ = -1, iterMax = 8, directions = 3, yMax = 1;
		boolean knight = false;
		switch(type) {
			case "P":
				//Pawns are a special case
				startJ = currentPlayer.code() * 2 - 1;
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
		if (iterateDirection(startJ, yMax, directions, iterMax, startX, startY, knight, "Checkmate")) return true;
		return false;
		/*
		if (!knight) {		
			//Go through every possible move a piece might make, going through that piece's eligible directions
			//i and j are essentially functions of x and y, (i= 1, j= 1) meaning x=y, (-1,1) meaning -x = y, but direction from the origin matters here
			for (int i = -1; i <= 1; i++) {
				for (int j = startJ; j <= yMax; j++) {
					//absolute values of i + j = 2 means diagonal, = 1 means horizontal. 3 Just indicates every direction.
					//The below skips over directions the piece can't move in
					if ((directions != 3 && Math.abs(i) + Math.abs(j) != directions) || Math.abs(i) + Math.abs(j) == 0) continue;
					//Finally k is the direction
					for (int k = 1; k < iterMax; k++) {
						targetX = startX - k * i;
						targetY = startY - k * j;
						//System.out.println("TargetX = " + targetX + " TargetY + " + targetY);
						if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) break;
						
						//If a piece has a spot they can move to without it resulting in check, end the the checkmate iter returning false
						if (movePiece(startX, startY, targetX, targetY)) {
							checking = false;
							return false;
						}
						//If a piece is encountered, stop looking in that direction
						if (grid[targetY][targetX] != null) break;
						
					}
					
				}
			}
		//Knights have their own special rules
		} else {
			for (int i = -2; i < 3; i++) {
				if (i == 0) continue;
				for (int j = 0; j <= 1; j++) {
					targetX = startX - i;
					targetY = (j == 0 ? -1 : +1) * (startY - (Math.abs(i) < 2 ? i * 2 : i / 2));
					System.out.println( "X = " + targetX + " Y = " + targetY);
					if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) continue;
					if (movePiece(startX, startY, targetX, targetY)) {
						checking = false;
						return false;
					}
				}
			}
		}
		checking = false;
		//If return true, continue the checkCheckmateIter
		return true;*/
	}
	//Carries out a check on every space in the direction specified to the degree specified - Check mode will do every direction + knight directions for the king, Checkmate mode will check every legal direction for a piece
	 boolean iterateDirection(int startJ, int yMax, int directions, int iterMax, int startX, int startY, boolean knight, String mode) {
		 int targetX, targetY;
		 if (!knight) {
			 for (int i = -1; i <= 1; i++) {
					for (int j = startJ; j <= yMax; j++) {
						int angle = Math.abs(i) + Math.abs(j);
						//absolute values of i + j = 2 means diagonal, = 1 means horizontal. 3 Just indicates every direction.
						//The below skips over directions the piece can't move in
						if ((directions != 3 && angle != directions) || angle == 0) continue;
						//Finally k is the direction
						for (int k = 1; k < iterMax; k++) {
							targetX = startX - k * i;
							targetY = startY - k * j;
							//System.out.println("TargetX = " + targetX + " TargetY + " + targetY);
							if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) break;
							
							//If a piece has a spot they can move to without it resulting in check, end the the checkmate iter returning false
							if (mode.equals("Checkmate")) {
								if (movePiece(startX, startY, targetX, targetY)) {
									checking = false;
									return false;
								}
							} else if (mode.equals("Check")) {
								Piece threat = grid[targetY][targetX];
								if (threat != null && threatCheck(threat.getType(), targetX, targetY, angle * 2, startX, startY)) return false;
							}
							//If a piece is encountered, stop looking in that direction
							if (grid[targetY][targetX] != null) break;
						}
					}
			} 
		}
		if (mode.equals("Check")) knight = true;
		if (knight) {
				for (int i = -2; i < 3; i++) {
					if (i == 0) continue;
					for (int j = 0; j <= 1; j++) {
						targetX = startX - i;
						targetY = (j == 0 ? -1 : +1) * (startY - (Math.abs(i) < 2 ? i * 2 : i / 2));
						//System.out.println( "X = " + targetX + " Y = " + targetY);
						if (targetX < 0 || targetX > 7 || targetY < 0 || targetY > 7) continue;
						
						if (mode.equals("Checkmate")) {
							if (movePiece(startX, startY, targetX, targetY)) {
								checking = false;
								return false;
							}
						} else if (mode.equals("Check")) {
							Piece threat = grid[targetY][targetX];
							if (threat != null && threatCheck(grid[targetY][targetX].getType(), targetX, targetY, 8, startX, startY)) return false;
						}
						
					}
				}
			}
		 return true;
	 }
	 
	 boolean threatCheck(String threatType, int threatX, int threatY, int threatAngle, int kingX, int kingY) {
		boolean kingThreat = threatType.equals("K") && Math.abs(kingY - threatY) <= 1 && Math.abs(kingX - threatX) <= 1 ? true : false;
		//System.out.println("ANGLE = " + threatAngle + " THREAT X = " + threatX + " Y = " + threatY + " type = " + threatType);
		if (grid[threatY][threatX].isPlayer(currentPlayer)) return false;
		if ((threatAngle <= 3) && (threatType.equals("R") || kingThreat || threatType.equals("Q"))) {
			return true;
		} else if ((threatAngle <= 7 && threatAngle > 3) && (threatType.equals("B") || kingThreat || threatType.equals("Q") || threatType.equals("P"))) {
			if (threatType.equals("P")) {
				//Can probably use pawnCheck for this
				if (currentPlayer.code() * 2 - 1 == kingY - threatY) {
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
			if (grid[startY][startX] == null || !grid[startY][startX].isPlayer(currentPlayer)) {
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
			}
			targetX = Math.abs(Integer.parseInt(targetLocation.substring(0,1)) - 1);
			targetY = Math.abs(Integer.parseInt(targetLocation.substring(1,2)) - 8);
			
			//Go through the checks that it's a valid move, and if it is then move the piece and go to the next move. If not, come back here.
			if (movePiece(startX, startY, targetX, targetY)) selectTarget = false;
			else System.out.println("Sorry, that's not a valid move.");
		}
		return true;
	}
}
