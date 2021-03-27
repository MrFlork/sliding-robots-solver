package net.booru.slidingrobots;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents the current state of robot positions A RobotState is immutable.
 */
public final class RobotsState {
    private final byte[] iPositions;

    /**
     * @param robotPositions the positions of robots as follows [main_robot_x, main_robot_y, helper_robot_1_x,
     *                       helper_robot_2_x, ..., helper_robot_n_x, helper_robot_n_x]
     */
    public RobotsState(byte[] robotPositions) {
        iPositions = robotPositions;
    }

    public RobotsState(List<Pair<Point, Piece>> robots) {
        final Optional<Pair<Point, Piece>> mainRobot =
                robots.stream().filter(pair -> pair.second == Piece.main_robot).findFirst();
        if (mainRobot.isEmpty()) {
            throw new IllegalArgumentException("No main robot");
        }

        final List<Pair<Point, Piece>> helperRobots =
                robots.stream().filter(pair -> pair.second == Piece.helper_robot).collect(Collectors.toList());

        if (helperRobots.size() + 1 != robots.size()) {
            throw new IllegalArgumentException("Input contains other pieces than one main robot and helper robots");
        }

        final byte[] robotPositions = new byte[robots.size() * 2];
        robotPositions[0] = (byte) mainRobot.get().first.x;
        robotPositions[1] = (byte) mainRobot.get().first.y;

        for (int i = 0; i < helperRobots.size(); i++) {
            final Pair<Point, Piece> pointPiecePair = helperRobots.get(i);
            final int index = 2 * (i + 1);
            robotPositions[index] = (byte) pointPiecePair.first.x;
            robotPositions[index + 1] = (byte) pointPiecePair.first.y;
        }

        iPositions = robotPositions;
    }

    public int getRobotCount() {
        return iPositions.length / 2;
    }

    public RobotsState withPositionX(int robotIndex, int x) {
        if (getPositionX(robotIndex) == x) {
            return this;
        }

        final byte[] positions = new byte[iPositions.length];
        System.arraycopy(iPositions, 0, positions, 0, iPositions.length);
        positions[2 * robotIndex] = (byte) x;
        return new RobotsState(positions);
    }

    public RobotsState withPositionY(int robotIndex, int y) {
        if (getPositionY(robotIndex) == y) {
            return this;
        }

        final byte[] positions = new byte[iPositions.length];
        System.arraycopy(iPositions, 0, positions, 0, iPositions.length);
        positions[2 * robotIndex + 1] = (byte) y;
        return new RobotsState(positions);
    }

    public int getPositionX(int robotIndex) {
        return iPositions[2 * robotIndex];
    }

    public int getPositionY(int robotIndex) {
        return iPositions[2 * robotIndex + 1];
    }

    /**
     * @param x          position
     * @param y          position
     * @param robotIndex the robot to ignore
     *
     * @return true if another robot (not robotIndex) is blocking position (x,y) else false
     */
    public boolean isOtherRobotBlocking(final int x, final int y, final int robotIndex) {
        for (int i = 0, len = getRobotCount(); i < len; i++) {
            if (i == robotIndex) {
                continue;
            }
            final boolean isBlocking = iPositions[2 * i] == x && iPositions[2 * i + 1] == y;
            if (isBlocking) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param x position
     * @param y position
     *
     * @return the index [0..robotCount-1] of the robot at position x,y. -1 if no robot is found.
     */
    public int getRobotAtPosition(final int x, final int y) {
        for (int i = 0, len = iPositions.length / 2; i < len; i++) {
            final boolean isBlocking = iPositions[2 * i] == x && iPositions[2 * i + 1] == y;
            if (isBlocking) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return Arrays.toString(iPositions);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RobotsState that = (RobotsState) o;
        return Arrays.equals(iPositions, that.iPositions);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(iPositions);
    }
}
