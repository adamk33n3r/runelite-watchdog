package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.RegexMatcher;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.RuneLiteConfig;
import net.runelite.client.plugins.grounditems.config.OwnershipFilterMode;
import net.runelite.client.util.Text;

import com.google.common.base.Splitter;

import javax.swing.*;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.Base64;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static net.runelite.api.TileItem.*;

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

    /**
     * Match a pattern against an input string
     * @param pattern the pattern to match
     * @param regexEnabled whether the pattern is a regex
     * @param input the input string
     * @return the groups of the pattern, or null if no match
     */
    public static String[] matchPattern(
        Supplier<String> pattern,
        Supplier<Boolean> regexEnabled,
        String input
    ) {
        String regex = regexEnabled.get() ? pattern.get() : Util.createRegexFromGlob(pattern.get());
        Matcher matcher = Pattern.compile(regex, regexEnabled.get() ? 0 : Pattern.CASE_INSENSITIVE).matcher(input);
        if (!matcher.find()) return null;

        String[] groups = new String[matcher.groupCount()];
        for (int i = 0; i < matcher.groupCount(); i++) {
            groups[i] = matcher.group(i+1);
        }
        return groups;
    }

    public static String[] matchPattern(RegexMatcher regexMatcher, String input) {
        return matchPattern(regexMatcher::getPattern, regexMatcher::isRegexEnabled, input);
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

    public static void syncAlwaysOnTop(JDialog dialog) {
        RuneLiteConfig runeLiteConfig = WatchdogPlugin.getInstance().getInjector().getInstance(RuneLiteConfig.class);
        if (runeLiteConfig.gameAlwaysOnTop() && dialog.isAlwaysOnTopSupported()) {
            dialog.setAlwaysOnTop(true);
        }
    }

    /**
     * All      -> none | self | other | group<br>
     * Drops    -> self | group<br>
     * Takeable -> none | self | group | (if a main then other)<br>
     * <br>
     * Taken from GroundItemsPlugin
     */
    public static boolean shouldTriggerItem(OwnershipFilterMode filterMode, int ownership, int accountType) {
        switch (filterMode) {
            case DROPS:
                return ownership == OWNERSHIP_SELF || ownership == OWNERSHIP_GROUP;
            case TAKEABLE:
                return ownership != OWNERSHIP_OTHER || accountType == 0; // Mains can always take items
            default:
                return true;
        }
    }

    /**
     * Compresses the given alert string.
     * @param alertJSON expects a string
     * @return the compressed string
     */
    public static String compressAlerts(String alertJSON) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(alertJSON.getBytes());
            gzipOutputStream.close();
            byteArrayOutputStream.close();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            return alertJSON;
        }
    }

    /**
     * Decompresses the given compressed alert string.
     * @param compressedAlerts expects a base64 encoded string
     * @return the decompressed string
     */
    public static String decompressAlerts(String compressedAlerts) {
        try {
            byte[] bytes = Base64.getDecoder().decode(compressedAlerts);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            gzipInputStream.close();
            byteArrayInputStream.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            return compressedAlerts;
        }
    }

    public static boolean isCompressed(String compressedAlerts) {
        try {
            byte[] bytes = Base64.getDecoder().decode(compressedAlerts);
            return (bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
