class Piece {
	//colour is represented by the letter, colourCode is 0 or 1, 0 = black, 1 = white
	String type, colour;
	int colourCode;

	Piece(String inType, int inColour) {
		type = inType;
		colourCode = inColour;
		if (inColour == 0) colour = "b";
		else colour = "w";
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

		if (!(grid[targetY + i][targetX + j] == null || (i == 0 && grid[targetY + i][targetX + j] != null && grid[targetY + i][targetX + j].colour != selectedPiece.colour))) {
			return false;
		}

		i += yIncrement;
		j += xIncrement;

	}
	return true;
}


boolean movePiece(int startX, int startY, int targetX, int targetY) {
	Piece selectedPiece = grid[startY][startX];
	Piece targetLocation = grid[targetY][targetX];
	
	//Check if inCheck	
	


	//Check if piece in the way
	if (!selectedPiece.type.equals("N")) {
		int xDifference = startX - targetX, yDifference = startY - targetY, xIncrement = 1, yIncrement = 1;
		if (xDifference < 0) xIncrement = -1;
		if (yDifference < 0) yIncrement = -1;

		if (startX != targetX && startY != targetY) {
			if (!collision(yIncrement, xIncrement, xDifference, targetX, targetY, selectedPiece)) return false;

		} else if (startX != targetX) {
			if (!collision(0, xIncrement, xDifference, targetX, targetY, selectedPiece)) return false;


		} else if (startY != targetY) {
			if (!collision(yIncrement, 0, yDifference, targetX, targetY, selectedPiece)) return false;

		}
	}
	//Checks passed!
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
	return true;
}


void checkCheck() {
	boolean checking = true, inCheck = false;
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

		String threatType = "";
		for (int i = 1; ; i++) {
			System.out.println(i);
			int targetY = startY + i * testVertical, targetX = startX + (i * testHorizontal);
			System.out.println("A" + targetX + "," + targetY);
			if (targetY > -1 && targetY < 8 && targetX > -1 && targetX < 8) {
				if (grid[targetY][targetX] != null) {
					System.out.println(targetX + "," + targetY);
					if (grid[targetY][targetX].colourCode != currentPlayer) {
						threatType = grid[targetY][targetX].type;
					}
					break;
					
				}
			} else break;
		}
		System.out.println(threatType);
		if (threatType.equals("R")) System.out.println("See");
		switch(j) {
			case 0:
				if (threatType.equals("R")/* || threatType.equals("K") || threatType.equals("Q")*/) {
					System.out.println("See");
					/*checking = false;
					inCheck = True;*/
				}
				testHorizontal = -1;
				break;
			case 1:
				/*if (threatType.equals("R") || threatType.equals("K") || threatType.equals("Q")) {
					checking = false;
					inCheck = True;
				}*/
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
				checking = false;
				break;
		}
	}
	
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
				//System.out.print("Test");
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


//Starting variables
boolean go = true;
int currentPlayer = 0;

//Initialise the grid
String[] orderOne = {"R", "N", "B", "K", "Q", "B", "N", "R"}
String[] orderTwo = {"R", "N", "B", "Q", "K", "B", "N", "R"}
Piece[][] grid = new Piece[8][8];

for (int j = 0; j < 8; j++) {
	int col = 0;
	if (j > 5) col = 1;
	if (j < 2 || j > 5) {
		for (int i = 0; i < 8; i++) {

			if (j == 1 || j == 6) {
				grid[j][i] = new Piece("P", col);

			} else if (j == 0) {
				grid[j][i] = new Piece(orderOne[i], col);

			} else {
				grid[j][i] = new Piece(orderTwo[i], col);

			}

		}
	}
}

//Set king location
int kingLocationBY = 0, kingLocationBX = 3, kingLocationWY = 7, kingLocationWX = 4;



checkCheck();

//Cycle while playing
while (go) {
	printGrid();
	currentPlayer = Math.abs(currentPlayer - 1);
	int startX = 0, startY = 0, targetX = 0, targetY = 0;

	//Piece selection formatted xy - 11, 14 etc.
	boolean selectPiece = true;
	while (selectPiece) {
		System.out.print("Select a piece: ");
		String targetPiece = System.console().readLine();
		System.out.println();

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
		if (targetLocation.equals("TEST")) {
			testMove(startX, startY);
			continue;
		}
		targetX = Math.abs(Integer.parseInt(targetLocation.substring(0,1)) - 1);
		targetY = Math.abs(Integer.parseInt(targetLocation.substring(1,2)) - 8);
		if (movePiece(startX, startY, targetX, targetY)) selectTarget = false;
		else System.out.println("Sorry, that's not a valid move.");
	}


}
