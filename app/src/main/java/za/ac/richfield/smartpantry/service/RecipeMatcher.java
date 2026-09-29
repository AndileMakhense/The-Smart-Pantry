package za.ac.richfield.smartpantry.service;

import za.ac.richfield.smartpantry.model.PantryItem;
import za.ac.richfield.smartpantry.model.Recipe;
import za.ac.richfield.smartpantry.model.RecipeIngredient;
import za.ac.richfield.smartpantry.util.IngredientNormalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implements the assignment's strict all-ingredients-and-quantities rule. */
public final class RecipeMatcher {
    private RecipeMatcher() { }

    public static List<Recipe> findStrictMatches(List<PantryItem> pantry, List<Recipe> recipes) {
        Map<String, Map<String, Double>> available = aggregatePantry(pantry);
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            boolean canMake = true;
            for (RecipeIngredient required : recipe.getIngredients()) {
                String name = IngredientNormalizer.name(required.getName());
                String dimension = IngredientNormalizer.dimension(required.getUnit());
                double needed = IngredientNormalizer.baseQuantity(required.getQuantity(), required.getUnit());
                double onHand = available.containsKey(name)
                        ? available.get(name).getOrDefault(dimension, 0.0) : 0.0;
                if (onHand + 0.0001 < needed) {
                    canMake = false;
                    break;
                }
            }
            if (canMake) matches.add(recipe);
        }
        return matches;
    }

    private static Map<String, Map<String, Double>> aggregatePantry(List<PantryItem> pantry) {
        Map<String, Map<String, Double>> result = new HashMap<>();
        for (PantryItem item : pantry) {
            String name = IngredientNormalizer.name(item.getName());
            String dimension = IngredientNormalizer.dimension(item.getUnit());
            double amount = IngredientNormalizer.baseQuantity(item.getQuantity(), item.getUnit());
            Map<String, Double> units = result.computeIfAbsent(name, ignored -> new HashMap<>());
            units.put(dimension, units.getOrDefault(dimension, 0.0) + amount);
        }
        return result;
    }
}
