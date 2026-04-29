package com.example.fanutsystem;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder> {

    private List<Child> childList;
    private final int[] avatarColors = {
        Color.parseColor("#1ABC9C"), Color.parseColor("#2ECC71"),
        Color.parseColor("#3498DB"), Color.parseColor("#9B59B6"),
        Color.parseColor("#34495E"), Color.parseColor("#F1C40F"),
        Color.parseColor("#E67E22"), Color.parseColor("#E74C3C")
    };

    public ChildAdapter(List<Child> childList) {
        this.childList = childList;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        Child child = childList.get(position);
        holder.tvName.setText(child.getName());
        holder.tvDetails.setText(String.format("%s | %s", child.getGender(), child.getDob()));
        holder.tvMuac.setText(String.format("MUAC: %s cm", child.getMuac()));

        // Set initials
        String initials = getInitials(child.getName());
        holder.tvInitials.setText(initials);

        // Set background color based on name to keep it consistent for each child
        int colorIndex = Math.abs(child.getName().hashCode()) % avatarColors.length;
        holder.cvAvatarBg.setCardBackgroundColor(avatarColors[colorIndex]);
        holder.tvInitials.setTextColor(Color.WHITE);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChildDetailsActivity.class);
            intent.putExtra("child_data", child);
            v.getContext().startActivity(intent);
        });
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "??";
        String[] parts = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvMuac, tvInitials;
        MaterialCardView cvAvatarBg;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvChildName);
            tvDetails = itemView.findViewById(R.id.tvChildDetails);
            tvMuac = itemView.findViewById(R.id.tvMuacValue);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            cvAvatarBg = itemView.findViewById(R.id.cvAvatarBg);
        }
    }
}
