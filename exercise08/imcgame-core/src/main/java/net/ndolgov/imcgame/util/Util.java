package net.ndolgov.imcgame.util;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Util {
    public static <T> List<T> asList(Iterator<T> iterator) {
        return asStream(iterator).collect(Collectors.toList());
    }

    public static <T> Stream<T> asStream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, -1), false);
    }

    private Util() {
    }
}