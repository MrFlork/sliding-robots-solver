package net.booru.slidingrobots.algorithm;

import net.booru.slidingrobots.common.Timer;
import net.booru.slidingrobots.state.Board;
import net.booru.slidingrobots.state.RobotsState;
import net.booru.slidingrobots.state.RobotsStateUtil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BreadthFirstSearchRecursive implements SlidingRobotsSearchAlgorithm {
    private final Board iBoard;

    /**
     * @param board the static board that we can make moves on
     */
    public BreadthFirstSearchRecursive(final Board board) {
        iBoard = board;
    }

    /**
     * @param startState  the initial state of the robotss.
     * @param endCriteria keeps track of the end criteria during search, knows when we have reach search target.
     *
     * @return The solution path including the start state, or empty if no solution was found.
     */
    @Override
    public Solution run(final RobotsState startState, final EndCriteria endCriteria) throws NoSolutionException {
        final Timer timer = new Timer();

        final LinkedList<Node> nodesToExpand = new LinkedList<>();
        final Set<RobotsState> seenStates = new HashSet<>(100000);
        final Node startNode = new Node(startState, null);
        nodesToExpand.add(startNode);
        seenStates.add(startNode.getState());

        final Statistics mutableStatistics = new Statistics();

        final Node endNode = searchBFS(nodesToExpand, seenStates, endCriteria, mutableStatistics);
        final LinkedList<RobotsState> solutionPath = RobotsStateUtil.extractRobotStatesFromNodePath(endNode);
        timer.close();

        mutableStatistics.setSolutionLength(solutionPath.size() - 1); // path includes start state
        mutableStatistics.setTime(timer.getDurationMillis());

        return new Solution(solutionPath, mutableStatistics);
    }


    private Node searchBFS(final LinkedList<Node> nodesToExpand,
                           final Set<RobotsState> seenStates,
                           final EndCriteria endCriteria,
                           final Statistics mutableStatistics)
            throws NoSolutionException {

        if (nodesToExpand.isEmpty()) {
            throw new NoSolutionException();
        }

        final Node currentNode = nodesToExpand.poll();
        final RobotsState currentState = currentNode.getState();
        mutableStatistics.increaseStatesVisited(1);

        final EndCriteria updatedEndCriteria = endCriteria.update(currentState);
        final Result result = updatedEndCriteria.getResult();

        if (result == Result.FULL) {
            return currentNode;
        } else if (result == Result.PARTIAL) {
            seenStates.clear();
            nodesToExpand.clear();
            seenStates.add(currentState);
        }

        final List<RobotsState> neighbors = currentState.getNeighbors(iBoard, seenStates);
        for (RobotsState neighbor : neighbors) {
            nodesToExpand.add(new Node(neighbor, currentNode));
        }
        seenStates.addAll(neighbors);
        mutableStatistics.increaseStatesCreated(neighbors.size());

        return searchBFS(nodesToExpand, seenStates, updatedEndCriteria, mutableStatistics);
    }
}
