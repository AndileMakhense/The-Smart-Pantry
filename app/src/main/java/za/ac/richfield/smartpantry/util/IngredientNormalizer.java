package za.ac.richfield.smartpantry.util;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Normalises common ingredient names and compatible metric units. */
public final class IngredientNormalizer {
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        ALIASES.put("tomatoes", "tomato");
        ALIASES.put("potatoes", "potato");
        ALIASES.put("eggs", "egg");
        ALIASES.put("onions", "onion");
        ALIASES.put("bananas", "banana");
        ALIASES.put("cloves garlic", "garlic");
        ALIASES.put("garlic cloves", "garlic");
        ALIASES.put("bell peppers", "bell pepper");
        ALIASES.put("peppers", "bell pepper");
        ALIASES.put("spring onions", "spring onion");
    }

    private IngredientNormalizer() { }

    public static String name(String raw) {
        if (raw == null) return "";
        String value = Normalizer.normalize(raw.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll("\\s+", " ");
        if (ALIASES.containsKey(value)) return ALIASES.get(value);
        if (value.endsWith("s") && value.length() > 3 && !value.endsWith("ss")) {
            value = value.substring(0, value.length() - 1);
        }
        return ALIASES.getOrDefault(value, value);
    }

    public static String unit(String raw) {
        if (raw == null) return "item";
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.matches("kilograms?|kgs?")) return "kg";
        if (value.matches("grams?|g")) return "g";
        if (value.matches("litres?|liters?|l")) return "l";
        if (value.matches("millilitres?|milliliters?|ml")) return "ml";
        if (value.matches("pieces?|items?|units?|pcs?")) return "item";
        if (value.matches("tablespoons?|tbsp")) return "tbsp";
        if (value.matches("teaspoons?|tsp")) return "tsp";
        if (value.matches("cups?")) return "cup";
        if (value.matches("slices?")) return "slice";
        return value;
    }

    public static String dimension(String rawUnit) {
        String u = unit(rawUnit);
        if (u.equals("kg") || u.equals("g")) return "mass";
        if (u.equals("l") || u.equals("ml")) return "volume";
        return u;
    }

    public static double baseQuantity(double quantity, String rawUnit) {
        String u = unit(rawUnit);
        if (u.equals("kg") || u.equals("l")) return quantity * 1000.0;
        return quantity;
    }

    public static boolean compatible(String pantryUnit, String requiredUnit) {
        return dimension(pantryUnit).equals(dimension(requiredUnit));
    }
}
