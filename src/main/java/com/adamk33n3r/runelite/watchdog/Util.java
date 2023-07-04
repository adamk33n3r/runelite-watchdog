package com.adamk33n3r.runelite.watchdog;

import com.adamk33n3r.runelite.watchdog.alerts.Alert;
import com.adamk33n3r.runelite.watchdog.alerts.AlertGroup;

import java.util.List;

public class Util {
    public static void setParentsOnAlerts(List<Alert> alerts) {
        for (Alert alert : alerts) {
            if (alert instanceof AlertGroup) {
                AlertGroup group = (AlertGroup) alert;
                setParentsOnAlertsHelper(group);
            }
        }
    }

    private static void setParentsOnAlertsHelper(AlertGroup parent) {
        for (Alert childAlert : parent.getAlerts()) {
            childAlert.setParent(parent);
            if (childAlert instanceof AlertGroup) {
                setParentsOnAlertsHelper((AlertGroup) childAlert);
            }
        }
    }

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
}
