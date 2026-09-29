package za.ac.richfield.smartpantry.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.data.DatabaseHelper;
import za.ac.richfield.smartpantry.model.Recipe;
import za.ac.richfield.smartpantry.model.RecipeIngredient;

import java.text.DecimalFormat;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private DatabaseHelper database;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        database = new DatabaseHelper(this);
        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = database.getRecipe(recipeId);
        if (recipe == null) { finish(); return; }

        ((TextView) findViewById(R.id.textRecipeTitle)).setText(recipe.getName());
        ((TextView) findViewById(R.id.textSteps)).setText(recipe.getSteps());
        DecimalFormat number = new DecimalFormat("0.##");
        StringBuilder ingredients = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            ingredients.append("• ").append(ingredient.getName()).append(": ")
                    .append(number.format(ingredient.getQuantity())).append(" ")
                    .append(ingredient.getUnit()).append("\n");
        }
        ((TextView) findViewById(R.id.textIngredients)).setText(ingredients.toString().trim());
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }

    @Override protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
