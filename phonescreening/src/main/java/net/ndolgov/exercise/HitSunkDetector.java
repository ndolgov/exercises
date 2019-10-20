import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class HitSunkDetector {
    public static void main(String[] args) {
        System.out.println(new HitSunkDetector().solution(26, "15P 15R,18P 19R", ""));//solution(4, "1B 2C,2D 4D", "2B 2D 3D 4D 4A"));
    }

    public String solution(int N, String S, String T) {
        if (T.isEmpty()) {
            return "0,0";
        }

        final List<Ship> ships = Stream.of(S.split(",")).map(Solution::ship).collect(Collectors.toList());

        final List<Point> hits = Stream.of(T.split(" ")).map(Solution::hit).collect(Collectors.toList());

        final AtomicInteger hitCount = new AtomicInteger(0);
        final AtomicInteger sunkCount = new AtomicInteger(0);

        // O(N*M)
        ships.stream().forEach(ship -> {
            switch (ship.stateAfter(hits)) {
                case HIT:
                    hitCount.incrementAndGet();
                    break;

                case SUNK:
                    sunkCount.incrementAndGet();
                    break;

                case INTACT:
                    break;
            }
        });

        return sunkCount.get() + "," + hitCount.get();
    }

    static Ship ship(String ltrb) {
        String[] a = ltrb.split(" ");
        return new Ship(point(a[0]), point(a[1]));
    }

    static Point hit(String xy) {
        return point(xy);
    }

    private static Point point(String str) {
        final int index = lettersStartAt(str);
        String xStr = str.substring(0, index);
        String yStr = str.substring(index);
        return new Point(yStr.charAt(0) - 'A' + 1, (Integer.valueOf(xStr)));
    }

    private static int lettersStartAt(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isAlphabetic(str.charAt(i))) {
                return i;
            }
        }

        throw new IllegalArgumentException(str);
    }

    final static class Point {
        final int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    final static class Ship {
        final Point l, r;

        Ship(Point l, Point r) {
            this.l = l;
            this.r = r;
        }

        private int area() {
            return (r.x - l.x + 1) * (r.y - l.y + 1);
        }

        private boolean within(Point p) {
            return (l.x <= p.x) && (p.x <= r.x) && (l.y <= p.y) && (p.y <= r.y);
        }

        public State stateAfter(Collection<Point> hits) {
            final AtomicInteger count = new AtomicInteger(0);
            hits.forEach(hit -> {
                if (within(hit)) {
                    count.incrementAndGet();
                }
            });

            final int nHits = count.get();
            final int max = area();
            return nHits == 0 ? State.INTACT : nHits < max ? State.HIT : State.SUNK;
        }

        @Override
        public String toString() {
            return "[" + l.toString() + " " + r.toString() + "]";
        }
    }

    enum State {INTACT, HIT, SUNK}
}


