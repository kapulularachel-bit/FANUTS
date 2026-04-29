package com.example.fanutsystem;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {

    private CommunityTipsAdapter adapter;
    private List<CommunityTip> allTips;
    private List<CommunityTip> displayedTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        ListView listView = findViewById(R.id.lvCommunityTips);
        allTips = KnowledgeBase.getPreloadedTips();
        displayedTips = new ArrayList<>(allTips);
        
        adapter = new CommunityTipsAdapter(displayedTips);
        listView.setAdapter(adapter);

        // Setup Filters
        ChipGroup chipGroup = findViewById(R.id.chipGroupFilters);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                filterTips("All Tips");
            } else {
                Chip chip = findViewById(checkedIds.get(0));
                filterTips(chip.getText().toString());
            }
        });

        ExtendedFloatingActionButton fabShare = findViewById(R.id.fabShareTip);
        fabShare.setOnClickListener(v -> showShareTipDialog());
    }

    private void filterTips(String category) {
        displayedTips.clear();
        if (category.equals("All Tips")) {
            displayedTips.addAll(allTips);
        } else {
            for (CommunityTip tip : allTips) {
                if (tip.getCategory().equalsIgnoreCase(category)) {
                    displayedTips.add(tip);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showShareTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.share_tip_title);

        final EditText input = new EditText(this);
        input.setHint(R.string.tip_hint);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setPadding(50, 40, 50, 40);
        builder.setView(input);

        builder.setPositiveButton(R.string.submit, (dialog, which) -> {
            String userTip = input.getText().toString().trim();
            if (!userTip.isEmpty()) {
                processAndSubmitTip(userTip);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void processAndSubmitTip(String tipText) {
        String[] forbiddenWords = {"badword1", "badword2", "harmfuladvice"}; 
        boolean isHarmful = false;
        for (String word : forbiddenWords) {
            if (tipText.toLowerCase().contains(word)) {
                isHarmful = true;
                break;
            }
        }

        if (isHarmful) {
            Toast.makeText(this, R.string.tip_moderated, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.tip_submitted, Toast.LENGTH_LONG).show();
            CommunityTip newTip = new CommunityTip(tipText, "Processing translation...", "Nutrition");
            allTips.add(0, newTip);
            filterTips("All Tips"); // Reset filter to show the new tip
        }
    }
}
