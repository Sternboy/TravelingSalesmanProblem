# TravelingSalesmanProblem
A Processing applet program for visualizing various solutions to the Traveling Salesman Problem

Uses Processing to display the controls in a nice UI.
Currently, it can create a list of locations in the XY plane.
These locations can then be traveled to in a variety of method selected on the left hand side.
Running the algorithm with a specific algorithm selected calculate the distance that algorithm would give.
It also draws a line connecting the points so show how it was calculated.
The starting (and therefore ending) location is a lighter blue and the final path is a slightly darker red.
Also has buttons for regenerating the locations to test different setups (as if running the algorithm in different cities), 
as well as buttons to add and subtract from the default number of locations.

Future implementation goals would be to 
1) add more algorithms (I have some in mind but don't currently have the time) 
2) Enhance the UI to change the colors of things based on the algorithm to make it more clear which is being used 
3) Allow for multiple algorithms to be run and displayed to show the differences 
4) Make a way to compare algorithms (perhaps recording the results of each test and displaying a bar graph of times when certain algs were most efficient) 
5) Use #4 to gather data to list the situations where one might use one algorithm over another 
