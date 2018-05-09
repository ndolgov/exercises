package net.ndolgov.exercise;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StringComparatorTest {
    @Test
    public void test() {


            System.out.println(cmp("aaaa","aaaa")); //True
            System.out.println(cmp("aaa","aaaa")); //False
            System.out.println(cmp("abcd","dcba")); //True
            System.out.println(cmp("abcd","ecba")); //False
            System.out.println(cmp("abbc","cba")); //False
            System.out.println(cmp("abbc","cbba")); //True
        }

        public boolean cmp(String a, String b) {
            if (a.length() != b.length()) {
                return false;
            }

            final Map<Character, Integer> ha = new HashMap<>(128);
            final Map<Character, Integer> hb = new HashMap<>(128);

            for (char ch : a.toCharArray()) {
                inc(ch, ha);
            }

            for (char ch : b.toCharArray()) {
                inc(ch, hb);
            }

            if (ha.size() != hb.size()) {
                return false;
            }

            for (char ch : ha.keySet()) {
                if (!ha.get(ch).equals(hb.get(ch))) {
                    return false;
                }
            }

            return true;
        }

        private void inc(char ch, Map<Character, Integer> h) {
            final Integer c = h.get(ch);
            if (c == null) {
                h.put(ch, 1);
            } else  {
                h.put(ch, c + 1);
            }
        }
}

