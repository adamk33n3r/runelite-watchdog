package com.adamk33n3r.runelite.watchdog;

import net.runelite.client.util.Text;

import com.google.common.base.Splitter;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static <T> T defaultArg(T thing, T defaultValue) {
        if (thing instanceof String) {
            String string = (String) thing;
            return string.isEmpty() ? defaultValue : thing;
        }
        return thing != null ? thing : defaultValue;
    }

    // https://stackoverflow.com/a/17369948
    public static String createRegexFromGlob(String pattern) {
        StringBuilder sb = new StringBuilder(pattern.length());
        int inGroup = 0;
        int inClass = 0;
        int firstIndexInClass = -1;
        char[] arr = pattern.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char ch = arr[i];
            switch (ch) {
                case '\\':
                    if (++i >= arr.length) {
                        sb.append('\\');
                    } else {
                        char next = arr[i];
                        switch (next) {
                            case ',':
                                // escape not needed
                                break;
                            case 'Q':
                            case 'E':
                                // extra escape needed
                                sb.append('\\');
                            default:
                                sb.append('\\');
                        }
                        sb.append(next);
                    }
                    break;
                case '*':
                    if (inClass == 0)
                        sb.append(".*");
                    else
                        sb.append('*');
                    break;
                case '?':
                    if (inClass == 0)
                        sb.append('.');
                    else
                        sb.append('?');
                    break;
                case '[':
                    inClass++;
                    firstIndexInClass = i+1;
                    sb.append('[');
                    break;
                case ']':
                    inClass--;
                    sb.append(']');
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                    if (inClass == 0 || (firstIndexInClass == i && ch == '^'))
                        sb.append('\\');
                    sb.append(ch);
                    break;
                case '!':
                    if (firstIndexInClass == i)
                        sb.append('^');
                    else
                        sb.append('!');
                    break;
                case '{':
                    inGroup++;
                    sb.append('(');
                    break;
                case '}':
                    inGroup--;
                    sb.append(')');
                    break;
                case ',':
                    if (inGroup > 0)
                        sb.append('|');
                    else
                        sb.append(',');
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static String splitCamelCase(String s) {
        return s.replaceAll(
            String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"
            ),
            " "
        );
    }

    public static String humanReadableClass(Object obj) {
        return splitCamelCase(obj.getClass().getSimpleName());
    }

    public static String processTriggerValues(String string, String[] triggerValues) {
        if (string == null) {
            return null;
        }

        for (int i = 0; i < triggerValues.length; i++) {
            string = string.replaceAll("\\$"+(i+1), triggerValues[i]);
        }

        return string;
    }

    /**
     * Scale a number from one range to another
     * @param val the number to scale
     * @param srcMin min source range
     * @param srcMax max source range
     * @param destMin min dest range
     * @param destMax max dest range
     * @return the scaled number
     */
    public static int scale(int val, float srcMin, float srcMax, float destMin, float destMax) {
        return Math.round(((val - srcMin) / (srcMax - srcMin)) * (destMax - destMin) + destMin);
    }

    private static final Splitter SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();
    public static boolean searchText(String search, List<String> keywords) {
        String normalizedSearch = Normalizer.normalize(search, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        return Text.matchesSearchTerms(
            SPLITTER.split(normalizedSearch),
            keywords.stream().map(term -> Normalizer.normalize(term, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase()).collect(Collectors.toList()));
    }
}
