package za.ac.richfield.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import za.ac.richfield.smartpantry.model.PantryItem;
import za.ac.richfield.smartpantry.model.Recipe;
import za.ac.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), " +
                "unit TEXT NOT NULL, expiry_date TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        seedRecipes(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry_items");
        onCreate(db);
    }

    public long addPantryItem(String name, double quantity, String unit, String expiryDate) {
        return getWritableDatabase().insertOrThrow("pantry_items", null,
                pantryValues(name, quantity, unit, expiryDate));
    }

    public int updatePantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        return getWritableDatabase().update("pantry_items", pantryValues(name, quantity, unit, expiryDate),
                "id = ?", new String[]{String.valueOf(id)});
    }

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items", "id = ?", new String[]{String.valueOf(id)});
    }

    public PantryItem getPantryItem(long id) {
        try (Cursor cursor = getReadableDatabase().query("pantry_items", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? pantryFromCursor(cursor) : null;
        }
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("pantry_items", null, null,
                null, null, null, "name COLLATE NOCASE")) {
            while (cursor.moveToNext()) items.add(pantryFromCursor(cursor));
        }
        return items;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("recipes", null, null,
                null, null, null, "name COLLATE NOCASE")) {
            while (cursor.moveToNext()) {
                Recipe recipe = new Recipe(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("steps")));
                loadIngredients(recipe);
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        try (Cursor cursor = getReadableDatabase().query("recipes", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (!cursor.moveToFirst()) return null;
            Recipe recipe = new Recipe(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("steps")));
            loadIngredients(recipe);
            return recipe;
        }
    }

    private ContentValues pantryValues(String name, double quantity, String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        if (expiryDate == null || expiryDate.trim().isEmpty()) values.putNull("expiry_date");
        else values.put("expiry_date", expiryDate.trim());
        return values;
    }

    private PantryItem pantryFromCursor(Cursor cursor) {
        return new PantryItem(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                cursor.getString(cursor.getColumnIndexOrThrow("expiry_date")));
    }

    private void loadIngredients(Recipe recipe) {
        try (Cursor cursor = getReadableDatabase().query("recipe_ingredients", null,
                "recipe_id = ?", new String[]{String.valueOf(recipe.getId())},
                null, null, "id")) {
            while (cursor.moveToNext()) {
                recipe.addIngredient(new RecipeIngredient(
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))));
            }
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            addRecipe(db, "Scrambled Eggs", "1. Beat the eggs with milk and salt.\n2. Melt butter in a pan.\n3. Cook gently while stirring until set.",
                    ing("egg", 2, "item"), ing("milk", 30, "ml"), ing("butter", 10, "g"), ing("salt", 1, "tsp"));
            addRecipe(db, "Tomato Omelette", "1. Chop the tomato.\n2. Beat the eggs with salt.\n3. Cook the tomato briefly, add eggs and fold when set.",
                    ing("egg", 2, "item"), ing("tomato", 1, "item"), ing("oil", 1, "tbsp"), ing("salt", 1, "tsp"));
            addRecipe(db, "Banana Oatmeal", "1. Simmer oats and milk for 5 minutes.\n2. Slice the banana.\n3. Serve with banana and honey.",
                    ing("oats", 60, "g"), ing("milk", 250, "ml"), ing("banana", 1, "item"), ing("honey", 1, "tbsp"));
            addRecipe(db, "Garlic Pasta", "1. Boil pasta until tender.\n2. Gently fry chopped garlic in oil.\n3. Toss pasta with garlic oil and salt.",
                    ing("pasta", 200, "g"), ing("garlic", 2, "item"), ing("oil", 2, "tbsp"), ing("salt", 1, "tsp"));
            addRecipe(db, "Rice and Beans", "1. Cook the rice.\n2. Saute onion.\n3. Add beans and tomato, simmer, then serve with rice.",
                    ing("rice", 200, "g"), ing("beans", 200, "g"), ing("onion", 1, "item"), ing("tomato", 2, "item"), ing("oil", 1, "tbsp"));
            addRecipe(db, "Cheese Toast", "1. Butter the bread.\n2. Add cheese.\n3. Toast in a pan until golden and melted.",
                    ing("bread", 2, "slice"), ing("cheese", 50, "g"), ing("butter", 10, "g"));
            addRecipe(db, "Tuna Sandwich", "1. Mix tuna and mayonnaise.\n2. Spread over bread.\n3. Add sliced tomato and close the sandwich.",
                    ing("bread", 2, "slice"), ing("tuna", 120, "g"), ing("mayonnaise", 1, "tbsp"), ing("tomato", 1, "item"));
            addRecipe(db, "Simple Pancakes", "1. Mix flour, milk and egg.\n2. Pour portions into an oiled pan.\n3. Cook both sides until golden.",
                    ing("flour", 150, "g"), ing("milk", 250, "ml"), ing("egg", 1, "item"), ing("oil", 1, "tbsp"));
            addRecipe(db, "Potato Hash", "1. Dice the potato and onion.\n2. Fry in oil until tender and crisp.\n3. Season with salt.",
                    ing("potato", 3, "item"), ing("onion", 1, "item"), ing("oil", 2, "tbsp"), ing("salt", 1, "tsp"));
            addRecipe(db, "Vegetable Fried Rice", "1. Saute onion, carrot and pepper.\n2. Add cooked rice.\n3. Stir in soy sauce and cook until hot.",
                    ing("rice", 250, "g"), ing("carrot", 1, "item"), ing("onion", 1, "item"), ing("bell pepper", 1, "item"), ing("soy sauce", 1, "tbsp"));
            addRecipe(db, "Tomato Soup", "1. Chop tomato and onion.\n2. Simmer with water for 20 minutes.\n3. Blend and season.",
                    ing("tomato", 4, "item"), ing("onion", 1, "item"), ing("water", 500, "ml"), ing("salt", 1, "tsp"));
            addRecipe(db, "Fruit Salad", "1. Peel and chop all fruit.\n2. Combine in a bowl.\n3. Add honey and serve.",
                    ing("banana", 1, "item"), ing("apple", 1, "item"), ing("orange", 1, "item"), ing("honey", 1, "tbsp"));
            addRecipe(db, "Egg Fried Rice", "1. Scramble the eggs.\n2. Add cooked rice.\n3. Stir in soy sauce and spring onion.",
                    ing("rice", 250, "g"), ing("egg", 2, "item"), ing("soy sauce", 1, "tbsp"), ing("spring onion", 1, "item"));
            addRecipe(db, "Mashed Potatoes", "1. Boil peeled potatoes.\n2. Drain and mash.\n3. Mix in milk, butter and salt.",
                    ing("potato", 4, "item"), ing("milk", 100, "ml"), ing("butter", 20, "g"), ing("salt", 1, "tsp"));
            addRecipe(db, "Bean Salad", "1. Drain the beans.\n2. Chop tomato and onion.\n3. Combine with oil and salt.",
                    ing("beans", 200, "g"), ing("tomato", 2, "item"), ing("onion", 1, "item"), ing("oil", 1, "tbsp"), ing("salt", 1, "tsp"));
            addRecipe(db, "Peanut Butter Toast", "1. Toast the bread.\n2. Spread peanut butter evenly.\n3. Top with banana slices.",
                    ing("bread", 2, "slice"), ing("peanut butter", 2, "tbsp"), ing("banana", 1, "item"));
            addRecipe(db, "Creamy Tomato Pasta", "1. Boil the pasta.\n2. Simmer tomato with milk.\n3. Combine, add cheese and season.",
                    ing("pasta", 200, "g"), ing("tomato", 3, "item"), ing("milk", 100, "ml"), ing("cheese", 50, "g"), ing("salt", 1, "tsp"));
            addRecipe(db, "Garlic Beans", "1. Fry garlic in oil.\n2. Add beans and cook for 5 minutes.\n3. Season and serve.",
                    ing("beans", 250, "g"), ing("garlic", 2, "item"), ing("oil", 1, "tbsp"), ing("salt", 1, "tsp"));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private String[] ing(String name, double quantity, String unit) {
        return new String[]{name, String.valueOf(quantity), unit};
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, String[]... ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("steps", steps);
        long recipeId = db.insertOrThrow("recipes", null, recipeValues);
        for (String[] ingredient : ingredients) {
            ContentValues values = new ContentValues();
            values.put("recipe_id", recipeId);
            values.put("name", ingredient[0]);
            values.put("quantity", Double.parseDouble(ingredient[1]));
            values.put("unit", ingredient[2]);
            db.insertOrThrow("recipe_ingredients", null, values);
        }
    }
}
