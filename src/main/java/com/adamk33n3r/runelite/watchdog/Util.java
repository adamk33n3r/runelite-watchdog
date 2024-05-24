package com.adamk33n3r.runelite.watchdog;

import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.util.Text;

import com.google.common.base.Splitter;

import java.awt.Color;
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

    public static Color colorAlpha(Color color, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("alpha must be >= 0 and <= 255");
        }

        return new Color((alpha << 24) | (color.getRGB() & 0xFFFFFF), true);
    }

    public static WorldPoint getClosestTile(WorldPoint playerLocation, GameObject gameObject) {
        int sizeX = gameObject.sizeX();
        int sizeY = gameObject.sizeY();
        WorldPoint worldLocation = gameObject.getWorldLocation();

        // given that the object is larger than 1 tile, the location is the center most tile, rounded to the south-west,
        // calculate the rectangle of all the tiles the object is in
        WorldPoint southWest = new WorldPoint(worldLocation.getX() - (sizeX - 1) / 2, worldLocation.getY() - (sizeY - 1) / 2, worldLocation.getPlane());
        WorldPoint northEast = new WorldPoint(worldLocation.getX() + sizeX / 2, worldLocation.getY() + sizeY / 2, worldLocation.getPlane());

        // calculate the closest tile on the edge of the rect made from southWest to northEast
        return new WorldPoint(
            Math.min(Math.max(playerLocation.getX(), southWest.getX()), northEast.getX()),
            Math.min(Math.max(playerLocation.getY(), southWest.getY()), northEast.getY()),
            worldLocation.getPlane()
        );
    }
}
