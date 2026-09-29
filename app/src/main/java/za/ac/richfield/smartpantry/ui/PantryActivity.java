package za.ac.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.data.DatabaseHelper;
import za.ac.richfield.smartpantry.model.PantryItem;

import java.util.List;

public class PantryActivity extends AppCompatActivity implements PantryAdapter.Listener {
    private DatabaseHelper database;
    private RecyclerView recycler;
    private TextView emptyMessage;
    private TextView summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        database = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerPantry);
        emptyMessage = findViewById(R.id.textEmptyPantry);
        summary = findViewById(R.id.textPantrySummary);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.buttonAddIngredient).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
        NavigationHelper.wire(this);
    }

    @Override protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<PantryItem> items = database.getAllPantryItems();
        recycler.setAdapter(new PantryAdapter(items, this));
        boolean empty = items.isEmpty();
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyMessage.setVisibility(empty ? View.VISIBLE : View.GONE);
        summary.setText(items.size() + (items.size() == 1 ? " pantry item" : " pantry items"));
    }

    @Override public void onEdit(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override public void onDelete(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Remove " + item.getName() + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    database.deletePantryItem(item.getId());
                    refreshList();
                }).show();
    }

    @Override protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
