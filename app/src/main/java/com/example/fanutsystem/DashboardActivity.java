package com.example.fanutsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvChildren;
    private ChildAdapter adapter;
    private List<Child> childList;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize UI
        rvChildren = findViewById(R.id.rvChildren);
        FloatingActionButton fabAddChild = findViewById(R.id.fabAddChild);
        emptyState = findViewById(R.id.emptyState);

        // Setup RecyclerView
        rvChildren.setLayoutManager(new LinearLayoutManager(this));
        
        // Load actual registered data from storage
        refreshChildList();

        // Setup FAB to go to registration
        fabAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ChildRegistrationActivity.class);
            startActivity(intent);
        });
        
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshChildList();
        syncChildrenFromCloud();
    }

    private void refreshChildList() {
        childList = ChildStorage.getChildren(this);
        
        if (childList.isEmpty()) {
            if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
            rvChildren.setVisibility(View.GONE);
        } else {
            if (emptyState != null) emptyState.setVisibility(View.GONE);
            rvChildren.setVisibility(View.VISIBLE);
            adapter = new ChildAdapter(childList);
            rvChildren.setAdapter(adapter);
        }
    }

    private void syncChildrenFromCloud() {
        FirebaseRepository.getInstance().fetchChildren(new FirebaseRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                if (children == null || children.isEmpty()) {
                    return;
                }
                ChildStorage.upsertChildren(DashboardActivity.this, children);
                refreshChildList();
            }

            @Override
            public void onError(Exception exception) {
                // Keep local data available even if network/cloud is unavailable.
            }
        });
    }
}
