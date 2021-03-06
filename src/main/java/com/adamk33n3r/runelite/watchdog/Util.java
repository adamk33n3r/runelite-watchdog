package com.adamk33n3r.runelite.watchdog;

public class Util {
    public static <T> T defaultArg(T thing, T defaultValue) {
        if (thing instanceof String) {
            String string = (String) thing;
            return string.isEmpty() ? defaultValue : thing;
        }
        return thing != null ? thing : defaultValue;
    }

    // https://stackoverflow.com/a/1248627/1260715
    public static String createRegexFromGlob(String glob) {
        StringBuilder out = new StringBuilder("^");
        for(int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch(c) {
                case '*': out.append(".*"); break;
                case '?': out.append('.'); break;
                case '.': out.append("\\."); break;
                case '\\': out.append("\\\\"); break;
                default: out.append(c);
            }
        }
        out.append('$');
        return out.toString();
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
}
