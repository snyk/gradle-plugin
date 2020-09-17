package io.snyk.gradle.plugin;

public class Version implements Comparable<Version> {

    public final int[] numbers;
    private String literal;

    public static Version of(String version) {
        return new Version(version);
    }

    private Version(String version) {
        this.literal = version;
        final String split[] = literal.split("\\-")[0].split("\\.");
        numbers = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            numbers[i] = Integer.valueOf(split[i]);
        }
    }

    @Override
    public int compareTo(Version another) {
        final int maxLength = Math.max(numbers.length, another.numbers.length);
        for (int i = 0; i < maxLength; i++) {
            final int left = i < numbers.length ? numbers[i] : 0;
            final int right = i < another.numbers.length ? another.numbers[i] : 0;
            if (left != right) {
                return left < right ? -1 : 1;
            }
        }
        return 0;
    }


    public boolean isGreaterThan(Version next) {
        return this.compareTo(next) > 0;
    }

    public boolean isGreaterOrEqualThan(Version next) {
        return this.compareTo(next) >= 0;
    }

    @Override
    public String toString() {
        return literal;
    }
}