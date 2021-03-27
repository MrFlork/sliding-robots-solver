package net.booru.slidingrobots;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        final String boardStr =
                "map:helper_robot:1:4:blocker:1:6:blocker:2:0:main_robot:2:1:blocker:2:3:blocker:4:0:helper_robot:5:6" +
                ":blocker:6:1:blocker:7:7:goal:3:0";
        //final String boardStrSmall = "map_3:blocker:2:0:main_robot:2:2:goal:1:0";

        final Game game = Game.valueOf(boardStr);
        final Board board = game.getBoard();
        final RobotsState robotsState = game.getInitialRobotsState();

        System.out.println(board.printBoard(robotsState));

        final Timer timer = new Timer();
        final List<RobotsState> solution = new BreadthFirstSearchRecursive(board).run(robotsState);
        timer.close();
        System.out.println("Time ms: " + timer.getDurationMillis());

        if (solution.isEmpty()) {
            System.out.println("No solution!");
        } else {
            System.out.println("Solution length: " + (solution.size() - 1));
            System.out.println("Solution: " + solution);
        }
    }
}
