package me.chetan.chromahud;

public class NumberUtil {
    public static double round(double in, double places) {
        if (places == 0.0) {
            return Math.round(in);
        }
        return (double)Math.round(in * 10.0 * places) / (10.0 * places);
    }
}

