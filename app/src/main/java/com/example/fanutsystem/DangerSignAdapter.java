package com.example.fanutsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;

public class DangerSignAdapter extends BaseAdapter {
    private List<DangerSign> dangerSigns;

    public DangerSignAdapter(List<DangerSign> dangerSigns) {
        this.dangerSigns = dangerSigns;
    }

    @Override
    public int getCount() {
        return dangerSigns.size();
    }

    @Override
    public Object getItem(int position) {
        return dangerSigns.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_danger_sign, parent, false);
        }

        DangerSign sign = dangerSigns.get(position);
        TextView tvTitle = convertView.findViewById(R.id.tvSignTitle);
        CheckBox cbSelected = convertView.findViewById(R.id.cbSignSelected);

        tvTitle.setText(sign.getTitle());
        cbSelected.setChecked(sign.isSelected());

        cbSelected.setOnClickListener(v -> sign.setSelected(cbSelected.isChecked()));
        convertView.setOnClickListener(v -> {
            sign.setSelected(!sign.isSelected());
            cbSelected.setChecked(sign.isSelected());
        });

        return convertView;
    }

    public static class DangerSign {
        private String title;
        private boolean isSelected;

        public DangerSign(String title) {
            this.title = title;
            this.isSelected = false;
        }

        public String getTitle() { return title; }
        public boolean isSelected() { return isSelected; }
        public void setSelected(boolean selected) { isSelected = selected; }
    }
}
