package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvChildren;
    private ChildAdapter adapter;
    private List<Child> childList;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI
        rvChildren = findViewById(R.id.rvChildren);
        FloatingActionButton fabAddChild = findViewById(R.id.fabAddChild);
        tvEmpty = findViewById(R.id.tvEmpty); // Note: I'll make sure this exists in the layout

        // Setup RecyclerView
        rvChildren.setLayoutManager(new LinearLayoutManager(this));
        
        // Load actual registered data from storage
        refreshChildList();

        // Setup FAB to go to registration
        fabAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ChildRegistrationActivity.class);
            startActivity(intent);
        });
        
        // Setup back button in toolbar
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshChildList();
    }

    private void refreshChildList() {
        childList = ChildStorage.getChildren(this);
        
        if (childList.isEmpty()) {
            if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
            rvChildren.setVisibility(View.GONE);
        } else {
            if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
            rvChildren.setVisibility(View.VISIBLE);
            adapter = new ChildAdapter(childList);
            rvChildren.setAdapter(adapter);
        }
    }
}
