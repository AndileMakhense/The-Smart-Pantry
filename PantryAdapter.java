package za.ac.richfield.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.model.PantryItem;

import java.text.DecimalFormat;
import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {
    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items;
    private final Listener listener;
    private final DecimalFormat number = new DecimalFormat("0.##");

    public PantryAdapter(List<PantryItem> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.quantity.setText(number.format(item.getQuantity()) + " " + item.getUnit());
        String expiry = item.getExpiryDate();
        holder.expiry.setText(expiry == null || expiry.isEmpty() ? "No expiry date" : "Expires: " + expiry);
        holder.edit.setOnClickListener(v -> listener.onEdit(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final Button edit, delete;
        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textIngredientName);
            quantity = view.findViewById(R.id.textIngredientQuantity);
            expiry = view.findViewById(R.id.textExpiry);
            edit = view.findViewById(R.id.buttonEdit);
            delete = view.findViewById(R.id.buttonDelete);
        }
    }
}
