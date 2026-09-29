package za.ac.richfield.smartpantry.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import za.ac.richfield.smartpantry.R;

public final class NavigationHelper {
    private NavigationHelper() { }

    public static void wire(Activity activity) {
        View pantry = activity.findViewById(R.id.navPantry);
        View recipes = activity.findViewById(R.id.navRecipes);
        View settings = activity.findViewById(R.id.navSettings);
        if (pantry != null) pantry.setOnClickListener(v -> open(activity, PantryActivity.class));
        if (recipes != null) recipes.setOnClickListener(v -> open(activity, SuggestedRecipesActivity.class));
        if (settings != null) settings.setOnClickListener(v -> open(activity, SettingsActivity.class));
    }

    private static void open(Activity from, Class<? extends Activity> target) {
        if (from.getClass().equals(target)) return;
        Intent intent = new Intent(from, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        from.startActivity(intent);
    }
}
