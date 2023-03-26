This is  min-max function for playing a game similar to tic tac toe. The user gives a board size and output file and then plays the game against the A.I. The function itself does not prevent the player from making illegal moves, i.e., the player can provide a coordinate that does not exist (will crash the program) or overwrite a position on the board that has already been filled.

Objective:
- the goal is to have as many pieces in a row by the time the board fills completely
- the player and A.I. receive two points for every two in a row and three points for every three in a row
- these values can stack, meaning that a three in a row gives you 7 points because it contains two two in a rows
