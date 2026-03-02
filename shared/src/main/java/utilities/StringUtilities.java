package utilities;

public final class StringUtilities {

    private StringUtilities() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}