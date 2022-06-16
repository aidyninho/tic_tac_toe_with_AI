package tictactoe;

import java.util.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    static char[][] gridFromUser(String userInput) {
        userInput = userInput.replaceAll("_", " ");
        char[][] grid = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid[i][j] = i == 0 ? userInput.charAt(j) : i == 1 ? userInput.charAt(j + 3) : userInput.charAt(j + 6);
            }
        }
        return grid;
    }

    static char[][] createEmptyGrid() {
        return gridFromUser("         ");
    }

    static void showGrid(char[][] grid) {
        System.out.printf("---------\n" +
                "| %c %c %c |\n" +
                "| %c %c %c |\n" +
                "| %c %c %c |\n" +
                "---------\n", grid[0][0], grid[0][1], grid[0][2],
                grid[1][0], grid[1][1], grid[1][2],
                grid[2][0], grid[2][1], grid[2][2]);
    }

    static void userMove(char[][] grid, char sign) {
        System.out.print("Enter the coordinates: ");
        String userInput = scanner.nextLine();
        try {
            int row = Integer.parseInt(userInput.split(" ")[0]) - 1;
            int col = Integer.parseInt(userInput.split(" ")[1]) - 1;
            if (row > 2 || col > 2) {
                System.out.println("Coordinates should be from 1 to 3!");
                userMove(grid, sign);
            } else if (grid[row][col] == ' ') {
                grid[row][col] = sign;
            } else {
                System.out.println("This cell is occupied! Choose another one!");
                userMove(grid, sign);
            }
        } catch (NumberFormatException e) {
            System.out.println("You should enter numbers!");
            userMove(grid, sign);
        }
    }

    static void aiMove(char[][] grid, char sign, String difficulty) {
        System.out.println("Making move level \"" + difficulty + "\"");
        Random random = new Random();
        char oppositeSign = sign == 'X' ? 'O' : 'X';
        ArrayList<Integer> coordinates = getWinnerCoordinates(grid, sign).isEmpty() ?
                getWinnerCoordinates(grid, oppositeSign) : getWinnerCoordinates(grid, sign);
        if (difficulty.equals("easy") || difficulty.equals("medium") && coordinates.isEmpty()) {
            int row = random.nextInt(3);
            int col = random.nextInt(3);
            while (true) {
                if (grid[row][col] == ' ') {
                    grid[row][col] = sign;
                    break;
                }
                row = random.nextInt(3);
                col = random.nextInt(3);
            }
        } else if (!coordinates.isEmpty()) {
            grid[coordinates.get(0)][coordinates.get(1)] = sign;
        } else if (difficulty.equals("hard")) {
            int[] coordinatesHardMode = findBestScore(grid, sign, oppositeSign);
            grid[coordinatesHardMode[0]][coordinatesHardMode[1]] = sign;
        }
    }

    static String checkGrid(char[][] grid) {
        int xCount = 0;
        int oCount = 0;
        int spaceCount = 0;
        int xWins = 0;
        int oWins = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == 'X') {
                    xCount++;
                } else if (grid[i][j] == 'O') {
                    oCount++;
                } else {
                    spaceCount++;
                }
            }
            if (grid[i][0] == 'X' && grid[i][1] == 'X' && grid[i][2] == 'X') {
                xWins++;
            } else if (grid[i][0] == 'O' && grid[i][1] == 'O' && grid[i][2] == 'O') {
                oWins++;
            } else if (grid[0][i] == 'X' && grid[1][i] == 'X' && grid[2][i] == 'X') {
                xWins++;
            } else if (grid[0][i] == 'O' && grid[1][i] == 'O' && grid[2][i] == 'O') {
                oWins++;
            }
        }

        if (grid[0][2] == 'X' && grid[1][1] == 'X' && grid[2][0] == 'X') {
            xWins++;
        } else if (grid[0][0] == 'O' && grid[1][1] == 'O' && grid[2][2] == 'O') {
            oWins++;
        } else if (grid[0][2] == 'O' && grid[1][1] == 'O' && grid[2][0] == 'O') {
            oWins++;
        } else if (grid[0][0] == 'X' && grid[1][1] == 'X' && grid[2][2] == 'X') {
            xWins++;
        }

        if (Math.abs(xCount - oCount) < 2) {
            if (xWins == 0 && oWins == 0 && spaceCount > 0) {
                return "Game not finished";
            } else if (xWins == 0 && oWins == 0 && spaceCount == 0) {
                return "Draw";
            } else if (xWins > oWins) {
                return "X wins";
            } else if (oWins > xWins) {
                return "O wins";
            }
        }
        return "Impossible";
    }

    static ArrayList<Integer> getWinnerCoordinates(char[][] grid, char sign) {
        ArrayList<Integer> coordinates = new ArrayList<>();

        int counter = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == sign) {
                    counter++;
                }
            }
            if (counter == 2) {
                for (int k = 0; k < 3; k++) {
                    if (grid[i][k] == ' ') {
                        coordinates.add(i);
                        coordinates.add(k);
                    }
                }
                return coordinates;
            }
            counter = 0;
        }

        for (int i = 0; i < 3; i++) {
            char[] col = {grid[0][i], grid[1][i], grid[2][i]};
            for (int j = 0; j < 3; j++) {
                if (col[j] == sign) {
                    counter++;
                }
            }
            if (counter == 2) {
                for (int k = 0; k < 3; k++) {
                    if (col[k] == ' ') {
                        coordinates.add(k);
                        coordinates.add(i);
                    }
                }
                return coordinates;
            }
            counter = 0;
        }

        char[] col = {grid[0][0], grid[1][1], grid[2][2]};
        for (int j = 0; j < 3; j++) {
            if (col[j] == sign) {
                counter++;
            }
        }
        if (counter == 2) {
            for (int k = 0; k < 3; k++) {
                if (col[k] == ' ') {
                    coordinates.add(k);
                    coordinates.add(k);
                    return coordinates;
                }
            }
        }
        counter = 0;


        char[] col1 = {grid[0][2], grid[1][1], grid[2][0]};
        for (int j = 0; j < 3; j++) {
            if (col1[j] == sign) {
                counter++;
            }
        }
        if (counter == 2) {
            for (int k = 0; k < 3; k++) {
                if (col1[k] == ' ') {
                    coordinates.add(k);
                    break;
                }
            }
            char[] col2 = {grid[2][0], grid[1][1], grid[0][2]};
            for (int k = 0; k < 3; k++) {
                if (col2[k] == ' ') {
                    coordinates.add(k);
                    return coordinates;
                }
            }
        }
        return coordinates;
    }

    static void playWithEasyAi(boolean userFirst, String difficulty) {
        char[][] grid = createEmptyGrid();
        showGrid(grid);
        for (int i = 0; i < 9; i++) {
            if (i % 2 == 0) {
                if (userFirst) {
                    userMove(grid, 'X');
                } else {
                    aiMove(grid, 'X', difficulty);
                }
            } else {
                if (userFirst) {
                    aiMove(grid, 'O', difficulty);
                } else {
                    userMove(grid, 'O');
                }
            }
            showGrid(grid);
            if (checkGrid(grid).equals("Draw") || checkGrid(grid).equals("X wins") || checkGrid(grid).equals("O wins")) {
                System.out.println(checkGrid(grid));
                break;
            }
        }
    }

    static void playWithUser() {
        char[][] grid = createEmptyGrid();
        showGrid(grid);
        for (int i = 0; i < 9; i++) {
            if (i % 2 == 0) {
                userMove(grid, 'X');
            } else {
                userMove(grid, 'O');
            }
            showGrid(grid);
            if (checkGrid(grid).equals("Draw") || checkGrid(grid).equals("X wins") || checkGrid(grid).equals("O wins")) {
                System.out.println(checkGrid(grid));
                break;
            }
        }
    }

    static void aiVsAi(String difficulty) {
        char[][] grid = createEmptyGrid();
        showGrid(grid);
        for (int i = 0; i < 9; i++) {
            if (i % 2 == 0) {
                aiMove(grid, 'X', difficulty);
            } else {
                aiMove(grid, 'O', difficulty);
            }
            showGrid(grid);
            if (checkGrid(grid).equals("Draw") || checkGrid(grid).equals("X wins") || checkGrid(grid).equals("O wins")) {
                System.out.println(checkGrid(grid));
                break;
            }
        }
    }

    static ArrayList<int[]> getEmptyIndexes(char[][] grid) {
        ArrayList<int[]> indexes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] == ' ') {
                    indexes.add(new int[] {i, j});
                }
            }
        }
        return indexes;
    }

    static int miniMax(char[][] grid, char currentPlayerSign, char userSign, char aiSign) {
        ArrayList<int[]> indexes = getEmptyIndexes(grid);

        if (checkGrid(grid).equals(userSign + " wins")) {
            return -10;
        } else if (checkGrid(grid).equals(aiSign + " wins")) {
            return 10;
        } else if (indexes.size() == 0) {
            return 0;
        }

        int best;
        if (currentPlayerSign == aiSign) {
            best = -1000;
            for (int[] index : indexes) {
                grid[index[0]][index[1]] = currentPlayerSign;

                best = Math.max(best, miniMax(grid, userSign, userSign, aiSign));

                grid[index[0]][index[1]] = ' ';
            }
        } else {
            best = 1000;
            for (int[] index : indexes) {
                grid[index[0]][index[1]] = currentPlayerSign;

                best = Math.min(best, miniMax(grid, aiSign, userSign, aiSign));

                grid[index[0]][index[1]] = ' ';
            }
        }
        return best;
    }

    static int[] findBestScore(char[][] grid, char aiSign, char userSign) {
        int bestScore = -1000;
        int row = -1;
        int col = -1;
        ArrayList<int[]> indexes = getEmptyIndexes(grid);

        for (int[] index : indexes) {
            grid[index[0]][index[1]] = aiSign;

            int moveScore = miniMax(grid, userSign, userSign, aiSign);

            grid[index[0]][index[1]] = ' ';

            if (moveScore > bestScore) {
                row = index[0];
                col = index[1];
                bestScore = moveScore;
            }
        }
        return new int[] {row, col};
    }

    static void organizeGame() {
        while (true) {
            System.out.print("Input command: ");
            String[] input = scanner.nextLine().split(" ");
            if (input.length == 1 && input[0].equals("exit")) {
                break;
            }
            if (input.length < 3) {
                System.out.println("Bad parameters!");
                continue;
            }
            if (input[0].equals("start")) {
                if (input[1].equals("easy") && input[2].equals("user")) {
                    playWithEasyAi(false, "easy");
                } else if (input[1].equals("user") && input[2].equals("easy")) {
                    playWithEasyAi(true, "easy");
                } else if (input[1].equals("easy") && input[2].equals("easy")) {
                    aiVsAi("easy");
                } else if (input[1].equals("medium") && input[2].equals("user")) {
                    playWithEasyAi(false, "medium");
                } else if (input[1].equals("user") && input[2].equals("medium")) {
                    playWithEasyAi(true, "medium");
                } else if (input[1].equals("medium") && input[2].equals("medium")) {
                    aiVsAi("medium");
                } else if (input[1].equals("hard") && input[2].equals("user")) {
                    playWithEasyAi(false, "hard");
                } else if (input[1].equals("user") && input[2].equals("hard")) {
                    playWithEasyAi(true, "hard");
                } else if (input[1].equals("hard") && input[2].equals("hard")) {
                    aiVsAi("hard");
                } else if (input[1].equals("user") && input[2].equals("user")) {
                    playWithUser();
                }
            }
        }
    }

    public static void main(String[] args) {
        organizeGame();
    }
}
