package za.ac.richfield.smartpantry.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import za.ac.richfield.smartpantry.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String[] SYSTEMS = {"Metric", "Common kitchen units"};
    private CheckBox expiryAlerts;
    private Spinner measurement;
    private SharedPreferences preferences;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getSharedPreferences("smart_pantry_settings", MODE_PRIVATE);
        expiryAlerts = findViewById(R.id.checkExpiryAlerts);
        measurement = findViewById(R.id.spinnerMeasurement);
        measurement.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SYSTEMS));
        expiryAlerts.setChecked(preferences.getBoolean("expiry_alerts", true));
        measurement.setSelection(preferences.getInt("measurement_system", 0));
        findViewById(R.id.buttonSaveSettings).setOnClickListener(v -> {
            preferences.edit()
                    .putBoolean("expiry_alerts", expiryAlerts.isChecked())
                    .putInt("measurement_system", measurement.getSelectedItemPosition())
                    .apply();
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        });
        NavigationHelper.wire(this);
    }
}
