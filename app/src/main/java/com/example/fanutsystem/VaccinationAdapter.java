package com.example.fanutsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class VaccinationAdapter extends BaseAdapter {
    private List<Vaccine> vaccineList;
    private OnStatusChangedListener listener;

    public interface OnStatusChangedListener {
        void onStatusChanged();
    }

    public VaccinationAdapter(List<Vaccine> vaccineList, OnStatusChangedListener listener) {
        this.vaccineList = vaccineList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return vaccineList.size();
    }

    @Override
    public Object getItem(int position) {
        return vaccineList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vaccination, parent, false);
        }

        Vaccine vaccine = vaccineList.get(position);
        TextView tvName = convertView.findViewById(R.id.tvVaccineName);
        TextView tvSchedule = convertView.findViewById(R.id.tvVaccineSchedule);
        ImageView ivStatus = convertView.findViewById(R.id.ivStatusIcon);

        tvName.setText(vaccine.getName());
        tvSchedule.setText(vaccine.getSchedule());

        if (vaccine.isCompleted()) {
            ivStatus.setImageResource(android.R.drawable.checkbox_on_background);
            ivStatus.setColorFilter(parent.getContext().getResources().getColor(R.color.status_success));
        } else {
            ivStatus.setImageResource(android.R.drawable.checkbox_off_background);
            ivStatus.setColorFilter(parent.getContext().getResources().getColor(R.color.gray_button));
        }

        convertView.setOnClickListener(v -> {
            vaccine.setCompleted(!vaccine.isCompleted());
            notifyDataSetChanged();
            if (listener != null) {
                listener.onStatusChanged();
            }
        });

        return convertView;
    }

    public static class Vaccine {
        private String name;
        private String schedule;
        private boolean isCompleted;

        public Vaccine(String name, String schedule) {
            this.name = name;
            this.schedule = schedule;
            this.isCompleted = false;
        }

        public String getName() { return name; }
        public String getSchedule() { return schedule; }
        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { isCompleted = completed; }
    }
}
