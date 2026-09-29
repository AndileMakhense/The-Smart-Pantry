package za.ac.richfield.smartpantry.service;

import org.junit.Test;
import za.ac.richfield.smartpantry.model.PantryItem;
import za.ac.richfield.smartpantry.model.Recipe;
import za.ac.richfield.smartpantry.model.RecipeIngredient;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class RecipeMatcherTest {
    @Test public void excludesRecipeWhenOneIngredientIsMissing() {
        Recipe recipe = recipe("Omelette", ingredient("egg", 2, "item"), ingredient("milk", 50, "ml"));
        assertEquals(0, RecipeMatcher.findStrictMatches(
                Collections.singletonList(new PantryItem(1, "eggs", 2, "items", null)),
                Collections.singletonList(recipe)).size());
    }

    @Test public void acceptsPluralNamesAndConvertedMetricUnits() {
        Recipe recipe = recipe("Pasta", ingredient("tomato", 2, "item"), ingredient("pasta", 500, "g"));
        assertEquals(1, RecipeMatcher.findStrictMatches(Arrays.asList(
                new PantryItem(1, "Tomatoes", 2, "items", null),
                new PantryItem(2, "pasta", 0.5, "kg", null)),
                Collections.singletonList(recipe)).size());
    }

    @Test public void aggregatesDuplicatePantryEntries() {
        Recipe recipe = recipe("Soup", ingredient("tomato", 4, "item"));
        assertEquals(1, RecipeMatcher.findStrictMatches(Arrays.asList(
                new PantryItem(1, "tomato", 2, "item", null),
                new PantryItem(2, "tomatoes", 2, "items", null)),
                Collections.singletonList(recipe)).size());
    }

    private Recipe recipe(String name, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe(1, name, "steps");
        for (RecipeIngredient ingredient : ingredients) recipe.addIngredient(ingredient);
        return recipe;
    }

    private RecipeIngredient ingredient(String name, double amount, String unit) {
        return new RecipeIngredient(name, amount, unit);
    }
}
