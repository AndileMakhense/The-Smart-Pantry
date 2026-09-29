package za.ac.richfield.smartpantry.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import za.ac.richfield.smartpantry.R;
import za.ac.richfield.smartpantry.data.DatabaseHelper;
import za.ac.richfield.smartpantry.model.PantryItem;

import java.util.Calendar;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {
    public static final String EXTRA_ITEM_ID = "item_id";
    private static final String[] UNITS = {"item", "g", "kg", "ml", "l", "tbsp", "tsp", "cup", "slice"};
    private EditText nameInput, quantityInput, expiryInput;
    private Spinner unitSpinner;
    private DatabaseHelper database;
    private long itemId = -1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        database = new DatabaseHelper(this);
        nameInput = findViewById(R.id.inputName);
        quantityInput = findViewById(R.id.inputQuantity);
        expiryInput = findViewById(R.id.inputExpiry);
        unitSpinner = findViewById(R.id.spinnerUnit);
        unitSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));
        expiryInput.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.buttonSave).setOnClickListener(v -> save());
        findViewById(R.id.buttonCancel).setOnClickListener(v -> finish());

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId >= 0) loadItem();
    }

    private void loadItem() {
        PantryItem item = database.getPantryItem(itemId);
        if (item == null) { finish(); return; }
        ((TextView) findViewById(R.id.textFormTitle)).setText("Edit Ingredient");
        nameInput.setText(item.getName());
        quantityInput.setText(String.valueOf(item.getQuantity()));
        expiryInput.setText(item.getExpiryDate());
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equalsIgnoreCase(item.getUnit())) unitSpinner.setSelection(i);
        }
    }

    private void save() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        if (name.length() < 2) {
            nameInput.setError("Enter an ingredient name of at least 2 characters");
            nameInput.requestFocus();
            return;
        }
        double quantity;
        try { quantity = Double.parseDouble(quantityText); }
        catch (NumberFormatException ex) {
            quantityInput.setError("Enter a valid number");
            quantityInput.requestFocus();
            return;
        }
        if (quantity <= 0) {
            quantityInput.setError("Quantity must be greater than zero");
            quantityInput.requestFocus();
            return;
        }
        String unit = unitSpinner.getSelectedItem().toString();
        String expiry = expiryInput.getText().toString().trim();
        if (itemId < 0) database.addPantryItem(name, quantity, unit, expiry);
        else database.updatePantryItem(itemId, name, quantity, unit, expiry);
        Toast.makeText(this, itemId < 0 ? "Ingredient added" : "Ingredient updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showDatePicker() {
        Calendar today = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> expiryInput.setText(
                String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
