package net.ndolgov.exercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * current/next cells: MSB = left count, LSB = right count; (0,0) == '.'
 */
public class Animation {
    private static final char MISSING = '.';
    private static final char PRESENT = 'X';
    private static final int BYTE_MASK = 0xFF;
    private static final char LEFT = 'L';
    private static final char RIGHT = 'R';
    private static final char EMPTY = marker(0, 0);

    private final int speed;

    private char[] current;

    private char[] next;

    private int particlesLeft;

    private final List<String> snapshots = new ArrayList<String>(128);

    public Animation(int speed) {
        this.speed = speed;
    }

    private String[] animate(String init) {
        initialize(init);

        while (particlesLeft > 0) {
            tick();
        }

        addSnapshot(next); // the last, no-X one
        return snapshots.toArray(new String[snapshots.size()]);
    }

    private void initialize(String input) {
        current = new char[input.length()];
        next = new char[input.length()];

        int dotCount = 0;

        for (int i = 0; i < input.length(); i++) {
            switch (input.charAt(i)) {
                case LEFT :
                    current[i] = marker(1, 0);
                    break;

                case RIGHT :
                    current[i] = marker(0, 1);
                    break;

                case MISSING:
                    current[i] = EMPTY;
                    dotCount++;
                    break;

                default: throw new IllegalArgumentException("Unexdpected symbol: " + current[i]);
            }
        }

        particlesLeft = input.length() - dotCount;
    }

    private void tick() {
        Arrays.fill(next, EMPTY);

        for (int i = 0; i < current.length; i++) {
            final int leftCountFrom = leftCount(current[i]);
            final int rightCountFrom = rightCount(current[i]);

            final int leftTo = i - speed;
            final int rightTo = i + speed;

            if (isValidPosition(leftTo)) {
                final int leftCountTo = leftCount(next[leftTo]);
                final int rightCountTo = rightCount(next[leftTo]);
                next[leftTo] = marker(leftCountTo + leftCountFrom, rightCountTo);
            } else {
                particlesLeft = particlesLeft - leftCountFrom;
            }

            if (isValidPosition(rightTo)) {
                final int leftCountTo = leftCount(next[rightTo]);
                final int rightCountTo = rightCount(next[rightTo]);
                next[rightTo] = marker(leftCountTo, rightCountTo + rightCountFrom);
            } else {
                particlesLeft = particlesLeft - rightCountFrom;
            }
        }

        final char[] snapshot = current;
        addSnapshot(snapshot);

        current = next;
        next = snapshot;
    }

    private static int leftCount(char ch) {
        return (ch >> 8) & BYTE_MASK;
    }

    private static int rightCount(char ch) {
        return ch & BYTE_MASK;
    }

    private static char marker(int left, int right) {
        return (char) (((left & BYTE_MASK) << 8) + (right & BYTE_MASK));
    }

    private boolean isValidPosition(int pos) {
        return (0 <= pos) && (pos < next.length);
    }

    private void addSnapshot(char[] snapshot) {
        for (int i = 0; i < snapshot.length; i++) {
            snapshot[i] = (current[i] == EMPTY) ? MISSING : PRESENT;
        }

        snapshots.add(new String(snapshot));
    }

    public static String[] animate(int speed, String init) {
        if ((init == null) || (init.length() == 0)) {
            return new String[] {""};
        }

        return new Animation(speed).animate(init);
    }
}
