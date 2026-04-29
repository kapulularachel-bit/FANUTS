package com.example.fanutsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ChildRegistrationActivity extends AppCompatActivity {
    private EditText nameInput, dobInput;
    private AutoCompleteTextView genderDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_registration);

        // Link UI elements
        nameInput = findViewById(R.id.ChildName);
        dobInput = findViewById(R.id.DOb);
        final EditText weightInput = findViewById(R.id.Weight);
        final EditText heightInput = findViewById(R.id.Height);
        final EditText muacInput = findViewById(R.id.MUAC);
        genderDropdown = findViewById(R.id.Gender);
        Button btnRegister = findViewById(R.id.btnRegister);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Setup Back Button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup Gender Dropdown
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, genders);
        genderDropdown.setAdapter(adapter);

        // Setup Date Picker
        dobInput.setOnClickListener(v -> showDatePicker());

        // Register button logic
        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                // Read values
                String name = nameInput.getText().toString().trim();
                String dob = dobInput.getText().toString().trim();
                String weight = weightInput.getText().toString().trim();
                String height = heightInput.getText().toString().trim();
                String muac = muacInput.getText().toString().trim();
                String gender = genderDropdown.getText().toString().trim();

                // Create Child object
                Child newChild = new Child(name, dob, gender, muac, weight, height);

                // Save to local storage
                ChildStorage.saveChild(this, newChild);

                // Success message
                String successMsg = getString(R.string.registration_success) + ": " + name;
                Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
                
                // Return to previous screen
                finish();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            dobInput.setText(date);
        }, year, month, day);
        datePicker.show();
    }

    private boolean validateInputs() {
        String name = nameInput.getText().toString().trim();
        String dob = dobInput.getText().toString().trim();
        String gender = genderDropdown.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError(getString(R.string.error_name_required));
            return false;
        }
        if (dob.isEmpty()) {
            dobInput.setError(getString(R.string.error_dob_required));
            return false;
        }
        if (gender.isEmpty()) {
            genderDropdown.setError(getString(R.string.error_gender_required));
            return false;
        }
        return true;
    }
}
