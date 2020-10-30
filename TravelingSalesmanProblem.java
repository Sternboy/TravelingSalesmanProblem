import processing.core.PApplet;
import java.util.Random;

/**
 * A Processing Applet Program for visualizing various solutions to the
 * Traveling Salesman Problem. Currently has a window with clickable buttons for
 * getting a list of random locations and picking the algorithm. Plans to
 * implement other algorithms (as currently it is just Nearest Neighbor) as well
 * as enhance the UI.
 * 
 * @author Samuel Sternman
 */
public class TravelingSalesmanProblem extends PApplet {

	/**
	 * Class for each location node with methods and data to make the algorithms
	 * easier
	 * 
	 * @author Sam Sternman
	 */
	private class LocationNode {
		private int xCord; // Position of location in X
		private int yCord; // Position of location in Y
		private boolean travelledTo; // Boolean storing whether a location has been visited

		// Constructor
		private LocationNode(int xCord, int yCord) {
			this.xCord = xCord;
			this.yCord = yCord;
			this.travelledTo = false;
		}

		// Returns distance between the two points (Pythagorean Theorem)
		private int distanceTo(LocationNode n) {
			return (int) Math.sqrt(Math.pow(n.xCord - this.xCord, 2) + Math.pow(n.yCord - this.yCord, 2));
		}

		// Setter for travelledTo
		private void travelTo() {
			this.travelledTo = true;
		}

		// Setter for travelledTo
		private void resetTravel() {
			this.travelledTo = false;
		}
	}

	private int locationColor = color(19, 7, 61);// Color of each location the salesman must travel to
	private int travelColor = color(235, 70, 61);// Color of the paths between location nodes
	private int backgroundColor = color(197, 242, 133); // Color of the background window
	private int backgroundMapColor = color(57, 219, 106); // Color of the map part of the window
	private int resetButtonColor = travelColor; // Color of the Reset Button (could change)
	private boolean drawnAlgorithm = false; // Keeps track of whether the selected algorithm has been drawn
	private boolean mouseClickedMoreLocations = false; // Keeps track of whether or not the mouse clicked the More
														// Locations Button
	private boolean mouseClickedFewerLocations = false; // Keeps track of whether or not the mouse clicked the Fewer
														// Locations Button
	private boolean mouseClickedNewLocations = false; // Keeps track of whether or not the mouse clicked the New
														// Locations Button
	private boolean mouseClickedNearestNeighbor = false; // Keeps track of whether or not the mouse clicked on the
															// Nearest Neighbor Button
	private boolean drawnNumLocations = false; // Keeps track of whether or not the number of locations has been drawn
												// yet
	private boolean drawOnceAfterNewLocations = true; // Keeps track of whether things that need to be drawn once after
														// new locations have been
	private boolean drawOnceAfterNewAlgorithm = true; // Keeps track of whether things that need to be drawn once after
														// new algorithms have been
	private String chosenAlgorithm = null; // Keeps track of the selected Algorithm
	private int numLocations = 10; // Number of locations for the salesman to travel to
	private int totalDistance = 0; // Total distance of the selected algorithm for the current map
	private final int xMax = 1500; // Max width of the canvas
	private final int yMax = 1050; // Max height of the canvas
	private final int mapWidth = 3 * xMax / 4; // width of the map area
	private final int mapHeight = 4 * yMax / 5; // height of the map area
	private final int mapXStart = 1 * xMax / 8; // starting X position of the map area
	private final int mapYStart = 1 * yMax / 10; // starting Y position of the map area
	private final int locationSize = 25; // Diameter of the location nodes
	private final int standardTextSize = 30; // Standard for the normal text size
	private final int smallTextSize = 15; // Standard for the small text size

	LocationNode[] locations = new LocationNode[numLocations]; // Locations on the screen

	// Runs the PApplet
	public static void main(String[] args) {
		PApplet.main("TravelingSalesmanProblem");
	}

	// Method used only for setting the size of the window
	public void settings() {
		size(xMax, yMax);
	}

	// Method to run setup functions once (background etc)
	public void setup() {
		background(backgroundColor);
		fill(backgroundMapColor);
		rect(mapXStart, mapYStart, mapWidth, mapHeight);
	}

	// Function to draw on the canvas (draws once per tick)
	public void draw() {
		// Draw text a single time after new locations are made
		if (drawOnceAfterNewLocations == true) {
			fill(color(0, 0, 0));
			textSize(smallTextSize);
			text("Number of Locations: ", xMax - 190, 60);
			textSize(standardTextSize);
			text("Algorithms ", 25, mapYStart + 25);
			rect(15, mapYStart + 30, 160, 3);
			drawOnceAfterNewLocations = false;
		}

		// Draw method text once a new algorithm is selected
		if (drawOnceAfterNewAlgorithm == true) {
			noStroke();
			fill(backgroundColor);
			rect(xMax * 8 / 16, 10, 500, 85);
			stroke(0, 0, 0);
			fill(0, 0, 0);
			textSize(standardTextSize);
			text("Total Distance: " + totalDistance, xMax * 9 / 16, 35);
			if (chosenAlgorithm == null) {
				text("Method: none selected", xMax * 9 / 16, 50 + standardTextSize);
			} else {
				text("Method: " + chosenAlgorithm, xMax * 9 / 16, 50 + standardTextSize);
			}

			drawOnceAfterNewAlgorithm = false;
		}

		// Draw New Locations Button
		int buttonX = 25; // also update values in mouseClicked
		int buttonY = 25;
		int buttonWidth = 250;
		int buttonHeight = standardTextSize + 20;
		fill(resetButtonColor);
		rect(buttonX, buttonY, buttonWidth, buttonHeight);
		fill(color(0, 0, 0));
		textSize(standardTextSize);
		text("New Locations", buttonX + 10, buttonY + 35);
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight) {
			fill(color(190, 60, 55));
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(standardTextSize);
			text("New Locations", buttonX + 10, buttonY + 35);
		}
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight && mouseClickedNewLocations == true) {
			mouseClickedNewLocations = false;
			newLocations();
			drawnAlgorithm = false;
		}

		// Draw Selected Algorithm Button
		buttonX = 400;
		buttonY = 25;
		buttonWidth = 250;
		buttonHeight = standardTextSize + 25;
		fill(resetButtonColor);
		rect(buttonX, buttonY, buttonWidth, buttonHeight);
		fill(color(0, 0, 0));
		textSize(standardTextSize);
		text("Draw Algorithm", buttonX + 10, buttonY + 35);
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight) {
			fill(color(190, 60, 55));
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(standardTextSize);
			text("Draw Algorithm", buttonX + 10, buttonY + 35);
		}
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight && mousePressed == true && chosenAlgorithm != null) {
			if (drawnAlgorithm != true) {
				drawnAlgorithm = true;
				switch (chosenAlgorithm) {
				case "Nearest Neighbor":
					nearestNeighbor();
					break;
				}
			}
		}

		// Draw More Locations Button
		buttonX = xMax - 175; // also update values in mouseClicked
		buttonY = 10;
		buttonWidth = 150;
		buttonHeight = 25;
		fill(resetButtonColor);
		rect(buttonX, buttonY, buttonWidth, buttonHeight);
		fill(color(0, 0, 0));
		textSize(15);
		text("More Locations", buttonX + 10, buttonY + 20);
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight) {
			fill(color(190, 60, 55));
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(15);
			text("More Locations", buttonX + 10, buttonY + 20);
		}
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight && mouseClickedMoreLocations == true) {
			numLocations++;
			mouseClickedMoreLocations = false;
			drawnNumLocations = false;
			fill(color(170, 50, 45)); // FIXME: Make color on click last longer?
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(15);
			text("More Locations", buttonX + 10, buttonY + 20);
			newLocations();
			drawnAlgorithm = false;
		}

		// Draw Fewer Locations Button
		buttonX = xMax - 175; // also update values in mouseClicked
		buttonY = 75;
		buttonWidth = 150;
		buttonHeight = 25;
		fill(resetButtonColor);
		rect(buttonX, buttonY, buttonWidth, buttonHeight);
		fill(color(0, 0, 0));
		textSize(15);
		text("Fewer Locations", buttonX + 10, buttonY + 20);
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight) {
			fill(color(190, 60, 55));
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(15);
			text("Fewer Locations", buttonX + 10, buttonY + 20);
		}
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight && mouseClickedFewerLocations == true) {
			numLocations--;
			mouseClickedFewerLocations = false;
			drawnNumLocations = false;
			fill(color(170, 50, 45)); // FIXME: Make color on click last longer?
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(15);
			text("Fewer Locations", buttonX + 10, buttonY + 20);
			newLocations();
			drawnAlgorithm = false;
		}

		// Number of Locations Text
		if (drawnNumLocations == false) {
			fill(color(245, 245, 60));
			rect(xMax - 45, buttonY - 33, 35, 25);
			fill(color(0, 0, 0));
			textSize(smallTextSize);
			text(numLocations, xMax - 40, buttonY - 15);
			drawnNumLocations = true;
		}

		// Nearest Neighbor Button
		buttonX = 18; // also update values in mouseClicked
		buttonY = mapYStart + 50;
		buttonWidth = 150;
		buttonHeight = 50;
		fill(color(66, 224, 245)); // Mouse not over and not selected
		rect(buttonX, buttonY, buttonWidth, buttonHeight);
		fill(color(0, 0, 0));
		textSize(smallTextSize);
		text("Nearest Neighbor", buttonX + 10, buttonY + 30);
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight
				&& (chosenAlgorithm == null || !chosenAlgorithm.equals("Nearest Neighbor"))) { // Mouse over button and
																								// not selected
			fill(color(69, 193, 209));
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(smallTextSize);
			text("Nearest Neighbor", buttonX + 10, buttonY + 30);
		} else if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight && chosenAlgorithm != null
				&& chosenAlgorithm.equals("Nearest Neighbor")) { // Mouse over button and it is selected
			fill(color(62, 145, 156));
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(smallTextSize);
			text("Nearest Neighbor", buttonX + 10, buttonY + 30);
		} else if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight && mouseClickedNearestNeighbor == true
				&& (chosenAlgorithm == null || !chosenAlgorithm.equals("Nearest Neighbor"))) { // Mouse over button and
																								// not selected and
																								// clicked
			chosenAlgorithm = "Nearest Neighbor";
			mouseClickedNearestNeighbor = false;
			fill(color(62, 145, 156)); // FIXME: Make color on click last longer?
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(smallTextSize);
			text("Nearest Neighbor", buttonX + 10, buttonY + 30);
			drawnAlgorithm = false;
		} else if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight && mouseClickedNearestNeighbor == true && chosenAlgorithm == null
				&& chosenAlgorithm.equals("Nearest Neighbor")) { // Mouse over button and
																	// is selected and
																	// clicked
			chosenAlgorithm = null;
			mouseClickedNearestNeighbor = false;
			fill(color(66, 224, 245));
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(smallTextSize);
			text("Nearest Neighbor", buttonX + 10, buttonY + 30);
			drawnAlgorithm = true;
		}
		if (chosenAlgorithm != null && chosenAlgorithm.equals("Nearest Neighbor")) { // Mouse not over and selected
			fill(color(62, 145, 156)); // FIXME: Make color on click last longer?
			rect(buttonX, buttonY, buttonWidth, buttonHeight);
			fill(color(0, 0, 0));
			textSize(smallTextSize);
			text("Nearest Neighbor", buttonX + 10, buttonY + 30);
		}

	}

	// Method for applet that runs when mouse is clicked
	public void mouseClicked() {
		// New Locations Button
		int buttonX = 25;
		int buttonY = 25;
		int buttonWidth = 250;
		int buttonHeight = standardTextSize + 20;
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight) {
			mouseClickedNewLocations = true;
		}

		// More Locations Button
		buttonX = xMax - 200;
		buttonY = 10;
		buttonWidth = 150;
		buttonHeight = 25;
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight) {
			mouseClickedMoreLocations = true;
		}

		// Fewer Location Button
		buttonX = xMax - 200;
		buttonY = 75;
		buttonWidth = 150;
		buttonHeight = 25;
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight) {
			mouseClickedFewerLocations = true;
		}

		// Nearest Neighbor Button
		buttonX = 18;
		buttonY = mapYStart + 50;
		buttonWidth = 150;
		buttonHeight = 50;
		if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight && chosenAlgorithm != null
				&& chosenAlgorithm.equals("Nearest Neighbor")) {
			mouseClickedNearestNeighbor = true;
			drawOnceAfterNewAlgorithm = true;
			chosenAlgorithm = null;
			totalDistance = 0;
			drawnAlgorithm = false;
			redrawLocations();
		} else if (mouseX >= buttonX && mouseX <= buttonWidth + buttonX && mouseY >= buttonY
				&& mouseY <= buttonY + buttonHeight
				&& (chosenAlgorithm == null || !chosenAlgorithm.equals("Nearest Neighbor"))) {
			mouseClickedNearestNeighbor = true;
			chosenAlgorithm = "Nearest Neighbor";
			drawOnceAfterNewAlgorithm = true;
		}

	}

	// Redraws the location dots
	public void redrawLocations() {
		fill(backgroundMapColor);
		rect(mapXStart, mapYStart, mapWidth, mapHeight);
		for (int i = 0; i < numLocations; i++) {
			stroke(locationColor);
			if (i == 0) {
				fill(67, 34, 181);
				circle(locations[i].xCord, locations[i].yCord, locationSize);
			} else {
				fill(locationColor);
				circle(locations[i].xCord, locations[i].yCord, locationSize);
			}
		}
		stroke(0, 0, 0);
	}

	// Draws the locations
	public void drawLocations() {
		locations = new LocationNode[numLocations];
		boolean drawnFirstLocation = false;
		Random randXY = new Random();
		for (int i = 0; i < numLocations; i++) {
			stroke(locationColor);
			if (drawnFirstLocation) {
				fill(locationColor);
			} else {
				drawnFirstLocation = true;
				fill(67, 34, 181);
			}
			int xCord = randXY.nextInt(mapWidth - (2 * locationSize)) + mapXStart + locationSize;
			int yCord = randXY.nextInt(mapHeight - (2 * locationSize)) + mapYStart + locationSize;
			circle(xCord, yCord, locationSize);
			locations[i] = new LocationNode(xCord, yCord);
		}
	}

	// Nearest Neighbor Algorithm
	public void nearestNeighbor() {
		// reset the locations and distance
		for (int i = 0; i < numLocations; i++) {
			locations[i].resetTravel();
		}
		totalDistance = 0;

		// start at the first node
		LocationNode startingNode = locations[0];
		startingNode.travelTo();
		int nearestLocationNumber = 1; // use for compacting code?
		int minDistance = startingNode.distanceTo(locations[nearestLocationNumber]);
		LocationNode nearestNeighbor = locations[nearestLocationNumber];
		for (int i = 0; i < numLocations - 1; i++) {
			if (locations[i + 1].travelledTo == false && startingNode.distanceTo(locations[i + 1]) < minDistance) {
				minDistance = startingNode.distanceTo(locations[i + 1]);
				nearestNeighbor = locations[i + 1];
				nearestLocationNumber = i + 1;
			}
		}
		totalDistance += minDistance;
		nearestNeighbor.travelTo();
		strokeWeight(6);
		stroke(travelColor);
		line(startingNode.xCord, startingNode.yCord, nearestNeighbor.xCord, nearestNeighbor.yCord);
		strokeWeight(1);
		stroke(color(0, 0, 0));

		// Other Travel
		for (int i = 0; i < numLocations - 2; i++) {
			startingNode = nearestNeighbor;
			int placeHolder = 0;
			while (true) {
				if (placeHolder > numLocations - 1) {
					System.out.println("No remaining points to visit ;c ");
					break;
				}
				if (locations[placeHolder].travelledTo == false
						&& startingNode.distanceTo(locations[placeHolder]) > 0) {
					minDistance = startingNode.distanceTo(locations[placeHolder]);
					nearestNeighbor = locations[placeHolder];
					break;
				}
				placeHolder++;
			}
			placeHolder = 0;
			for (int j = 0; j < numLocations - 1; j++) {
				if (locations[j + 1].travelledTo == false && startingNode.distanceTo(locations[j + 1]) < minDistance) {
					minDistance = startingNode.distanceTo(locations[j + 1]);
					nearestNeighbor = locations[j + 1];
					nearestLocationNumber = j + 1;
				}
			}
			totalDistance += minDistance;
			nearestNeighbor.travelTo();
			strokeWeight(6);
			stroke(travelColor);
			line(startingNode.xCord, startingNode.yCord, nearestNeighbor.xCord, nearestNeighbor.yCord);
			strokeWeight(1);
			stroke(color(0, 0, 0));
		}

		// Travel back to start
		strokeWeight(6);
		stroke(color(148, 66, 59));
		line(locations[0].xCord, locations[0].yCord, nearestNeighbor.xCord, nearestNeighbor.yCord);
		strokeWeight(1);
		stroke(color(0, 0, 0));
		totalDistance += locations[0].distanceTo(nearestNeighbor);

		drawOnceAfterNewAlgorithm = true;
	}

	// Method for drawing the new locations
	public void newLocations() {
		background(backgroundColor);
		fill(backgroundMapColor);
		rect(mapXStart, mapYStart, mapWidth, mapHeight);
		drawLocations();
		drawOnceAfterNewLocations = true;
		drawOnceAfterNewAlgorithm = true;
		totalDistance = 0;
		drawnNumLocations = false;
	}

}

//FIXME For next time: Pick future algorithms and auto thickness of points and lines and color code points with alg color
