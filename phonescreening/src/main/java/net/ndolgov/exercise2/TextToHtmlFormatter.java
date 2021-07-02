package net.ndolgov.exercise2;

import java.util.*;

/*
text = "ABCDE"
formats = {"bold": [(0,2)]}
output = "<b>AB</b>CDE"

text = "ABCDE"
formats = {"bold": [(0,2)], "italic": [(1,3)]}
output = "ABCDE"
"<b>A<i>B</i></b><i>C</i>DE"
 */
class TextToHtmlFormatter {
    public static void main(String[] args) {
        HashMap<Character, List<Range>> styleToRanges = new HashMap<>();

        ArrayList<Range> bolds = new ArrayList<>();
        bolds.add(new Range(0, 2));
        styleToRanges.put('b', bolds);

        ArrayList<Range> italics = new ArrayList<>();
        italics.add(new Range(1, 3));
        styleToRanges.put('i', italics);

        final String actual = toHtml("ABCDE", styleToRanges);
        System.out.println(actual);

        final String expected = "<b>A<i>B</i></b><i>C</i>DE";
        if (!actual.equals(expected)) {
            throw new RuntimeException("Expected: " + expected + " found: " + actual);
        }
    }


    public static final class Range {
        final int left;
        final int right;

        public Range(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }

    public static final class Boundary {
        final char style;
        final boolean isOpen;

        public Boundary(char style, boolean isOpen) {
            this.isOpen = isOpen;
            this.style = style;
        }
    }

    public static String toHtml(String text, Map<Character, List<Range>> styleToRanges) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        final Map<Integer, List<Boundary>> textIndexToBoundaries = mapTextIndexToBoundaries(styleToRanges);

        final Stack<Character> endings = new Stack<>(); // the nested tags to be closed before closing a top-level tag
        final Stack<Character> beginnings = new Stack<>();

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++ ) {
            final List<Boundary> boundaries = textIndexToBoundaries.get(i);

            if (boundaries != null) {
                boundaries.stream().filter(b -> !b.isOpen).forEach(b -> {
                    Character nestedStyleToClose = endings.pop();
                    while (nestedStyleToClose != b.style) {
                        closeTag(sb, nestedStyleToClose);
                        beginnings.push(nestedStyleToClose);

                        nestedStyleToClose = endings.pop();
                    }
                    closeTag(sb, b.style);
                });

                while (!beginnings.isEmpty()) {
                    final Character nestedStyleToReopen = beginnings.pop();
                    openTag(sb, nestedStyleToReopen);
                    endings.push(nestedStyleToReopen);
                }

                boundaries.stream().filter(b -> b.isOpen).forEach(b -> {
                    endings.push(b.style);

                    openTag(sb, b.style);
                });
            }

            sb.append(text.charAt(i));
        }


        return sb.toString();
    }

    private static Map<Integer, List<Boundary>> mapTextIndexToBoundaries(Map<Character, List<Range>> styleToRanges) {
        final Map<Integer, List<Boundary>> indexToBoundaries = new HashMap<>();

        styleToRanges.forEach((style, ranges) -> {
            ranges.forEach(range -> {
                if (!indexToBoundaries.containsKey(range.left)) {
                    indexToBoundaries.put(range.left, new ArrayList<>());
                }
                if (!indexToBoundaries.containsKey(range.right)) {
                    indexToBoundaries.put(range.right, new ArrayList<>());
                }

                indexToBoundaries.get(range.left).add(new Boundary(style, true));
                indexToBoundaries.get(range.right).add(new Boundary(style, false));
            });
        });

        return indexToBoundaries;
    }

    private static void openTag(StringBuilder sb, Character tag) {
        sb.append("<").append(tag).append(">");
    }

    private static void closeTag(StringBuilder sb, Character tag) {
        sb.append("</").append(tag).append(">");
    }
}
