package za.ac.richfield.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    public interface Listener { void onRecipeSelected(Recipe recipe); }
    private final List<Recipe> recipes;
    private final Listener listener;

    public RecipeAdapter(List<Recipe> recipes, Listener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.name.setText(recipe.getName());
        holder.summary.setText(recipe.getIngredients().size() + " ingredients - all available");
        holder.itemView.setOnClickListener(v -> listener.onRecipeSelected(recipe));
    }

    @Override public int getItemCount() { return recipes.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, summary;
        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textRecipeName);
            summary = view.findViewById(R.id.textRecipeSummary);
        }
    }
}
