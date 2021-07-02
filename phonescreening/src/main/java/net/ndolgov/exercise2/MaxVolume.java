package net.ndolgov.exercise2;

import java.util.*;
import java.util.stream.Collectors;

public class MaxVolume {
    public static void main(String[] args) {
        final ArrayList<Integer> start = new ArrayList<>();
        start.add(1);
        start.add(2);
        start.add(4);
        final ArrayList<Integer> duration = new ArrayList<>();
        duration.add(2);
        duration.add(2);
        duration.add(1);
        final ArrayList<Integer> volume = new ArrayList<>();
        volume.add(1);
        volume.add(2);
        volume.add(3);
        System.out.println(phoneCalls(start, duration, volume));
    }

    public static final class Call {
        final int start;
        final int duration;
        final int volume;

        public Call(int start, int duration, int volume) {
            this.start = start;
            this.duration = duration;
            this.volume = volume;
        }
    }

    public static final class Context {
        final int[] maxV;
        final List<Call> calls;
        final boolean[] taken;
        int depth = 0;
        int volume = 0;
        int maxVolume = 0;

        public Context(List<Call> calls) {
            this.calls = calls;

            maxV = new int[calls.size()];
            Arrays.fill(maxV, -1);

            taken = new boolean[calls.size()];
            Arrays.fill(taken, false);
        }
    }

    public static int phoneCalls(List<Integer> start, List<Integer> duration, List<Integer> volume) {
        if (start.isEmpty()) {
            return 0;
        }

        final List<Call> calls = new ArrayList<>(start.size());
        for (int i = 0; i < start.size(); i++) {
            calls.add(new Call(start.get(i), duration.get(1), volume.get(i)));
        }
        //calls.sort(Comparator.comparingInt(c -> c.start));

        final Context ctx = new Context(calls);
        findMaxCallVolume(ctx);
        return ctx.maxVolume;
    }

    private static void findMaxCallVolume(Context ctx) {
        if (ctx.depth >= ctx.calls.size()) {
            if (ctx.maxVolume < ctx.volume) {
                ctx.maxVolume = ctx.volume;
                ctx.volume = 0;
            }

            return;
        }

        for (int i = 0; i < ctx.calls.size(); i++) {
            if (!ctx.taken[i]) {
                ctx.taken[i] = true;
                ctx.depth++;

                if (!overlaps(ctx, i)) {
                    ctx.volume += ctx.calls.get(i).volume;

                    findMaxCallVolume(ctx);

                    ctx.volume -= ctx.calls.get(i).volume;
                } else {
                    findMaxCallVolume(ctx);
                }

                ctx.taken[i] = false;
                ctx.depth--;
            }
        }
    }

    private static boolean overlaps(Context ctx, int j) {
        for (int i = 0; i < ctx.calls.size(); i++) {
            if (i != j && ctx.taken[i] && !isIntersectionEmpty(ctx.calls.get(i), ctx.calls.get(j))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isIntersectionEmpty(Call l, Call r) {
        return (l.start + l.duration <= r.start) || (r.start + r.duration <= l.start);
    }


    public static List<String> fetchItemsToDisplay(List<List<String>> items, int sortParameter, int sortOrder, int itemsPerPage, int pageNumber) {

        return items.stream()
          .sorted((o1, o2) -> {
            final String l = sortOrder == 0 ? o1.get(sortParameter) : o2.get(sortParameter);
            final String r = sortOrder == 0 ? o2.get(sortParameter) : o1.get(sortParameter);
            return l.compareTo(r);
          })
          .skip(itemsPerPage * pageNumber)
          .limit(itemsPerPage)
          .map(o -> o.get(0))
         .collect(Collectors.toList());

    }
}
