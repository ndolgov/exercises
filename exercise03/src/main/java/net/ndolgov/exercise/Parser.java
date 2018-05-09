package net.ndolgov.exercise;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private static final int EOF = -1;
    private final Map<String, String> kvs = new HashMap<>();
    private final Reader body;
    private int next;

    public static Map<String, String> parse(String path) {
        try {
            return new Parser(new FileReader(path)).parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Parser(Reader body) {
        this.body = body;
    }

    public Map<String, String> parse() {
        try {
            next = body.read();

            files();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return kvs;
    }

    private void files() throws IOException {
        while (file(kvs)) {}
    }

    private boolean file(Map<String, String> files) throws IOException {
        match("file");
        match('{');
        final String key = string();
        match(':');
        match("source");
        match("=>");
        final String value = string();
        match('}');

        files.put(key, value);

        return next != EOF;
    }

    private String string() throws IOException {
        ws();
        match('\"');
        return matchUpTo('\"');
    }

    private String matchUpTo(char upTo) throws IOException {
        final StringBuilder sb = new StringBuilder();
        while (next != EOF) {
            final int ch = next;
            next = body.read();

            if (ch == upTo) {
                return sb.toString();
            } else {
                sb.append(Character.valueOf((char) ch));
            }
        }

        throw new RuntimeException("No match: " + upTo);
    }

    private void match(String str) throws IOException {
        match(str.toCharArray());
    }

    private void match(char[] chs) throws IOException {
        for (char ch : chs) {
            match(ch);
        }
    }

    private void match(char ch) throws IOException {
        ws();

        if (next != ch) {
            throw new RuntimeException(Character.valueOf((char) next) + " != " + ch);
        }

        next = body.read();
    }

    private void ws() throws IOException {
        while ((next != EOF) && Character.isWhitespace(next)) {
            next = body.read();
        }
    }
}
