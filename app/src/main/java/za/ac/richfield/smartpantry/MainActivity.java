package za.ac.richfield.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import za.ac.richfield.smartpantry.ui.PantryActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, PantryActivity.class));
        finish();
    }
}
