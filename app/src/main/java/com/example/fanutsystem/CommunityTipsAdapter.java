package com.example.fanutsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class CommunityTipsAdapter extends BaseAdapter {
    private List<CommunityTip> tips;

    public CommunityTipsAdapter(List<CommunityTip> tips) {
        this.tips = tips;
    }

    @Override
    public int getCount() {
        return tips.size();
    }

    @Override
    public Object getItem(int position) {
        return tips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_tip, parent, false);
        }

        CommunityTip tip = tips.get(position);
        TextView tvCategory = convertView.findViewById(R.id.tvTipCategory);
        TextView tvEnglish = convertView.findViewById(R.id.tvTipEnglish);
        TextView tvChichewa = convertView.findViewById(R.id.tvTipChichewa);

        tvCategory.setText(tip.getCategory());
        tvEnglish.setText(tip.getEnglishText());
        tvChichewa.setText(tip.getChichewaText());

        return convertView;
    }
}
