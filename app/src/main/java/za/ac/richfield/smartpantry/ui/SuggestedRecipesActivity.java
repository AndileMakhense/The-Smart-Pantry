package za.ac.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.data.DatabaseHelper;
import za.ac.richfield.smartpantry.model.Recipe;
import za.ac.richfield.smartpantry.service.RecipeMatcher;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private DatabaseHelper database;
    private RecyclerView recycler;
    private TextView noMatches;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        database = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerRecipes);
        noMatches = findViewById(R.id.textNoMatches);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        NavigationHelper.wire(this);
    }

    @Override protected void onResume() {
        super.onResume();
        List<Recipe> matches = RecipeMatcher.findStrictMatches(
                database.getAllPantryItems(), database.getAllRecipes());
        recycler.setAdapter(new RecipeAdapter(matches, recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        }));
        recycler.setVisibility(matches.isEmpty() ? View.GONE : View.VISIBLE);
        noMatches.setVisibility(matches.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
