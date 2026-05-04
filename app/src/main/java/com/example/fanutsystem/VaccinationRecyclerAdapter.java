package com.example.fanutsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VaccinationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Listener {
        void onToggle(VaccinationRepository.VaccineUiModel dose, boolean completed);
    }

    private final List<VaccinationRepository.RegisterRow> rows = new ArrayList<>();
    private final Listener listener;

    public VaccinationRecyclerAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<VaccinationRepository.RegisterRow> data) {
        rows.clear();
        if (data != null) {
            rows.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).rowType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VaccinationRepository.RegisterRow.TYPE_VISIT_HEADER) {
            View v = inflater.inflate(R.layout.item_immunization_visit_header, parent, false);
            return new VisitHeaderVh(v);
        }
        View v = inflater.inflate(R.layout.item_vaccination, parent, false);
        return new DoseVh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VaccinationRepository.RegisterRow row = rows.get(position);
        if (holder instanceof VisitHeaderVh) {
            VisitHeaderVh vh = (VisitHeaderVh) holder;
            vh.tvTitle.setText(row.visitTitle);
            vh.tvProgramme.setText(row.visitProgramme);
            return;
        }
        DoseVh vh = (DoseVh) holder;
        VaccinationRepository.VaccineUiModel dose = row.dose;
        vh.tvName.setText(dose.getDisplayName());
        vh.tvDue.setText(dose.getDueSummaryLine());
        vh.tvRoute.setText(dose.getRouteSiteLine());
        if (dose.isCompleted() && dose.getAdministeredSummaryLine() != null
                && !dose.getAdministeredSummaryLine().isEmpty()) {
            vh.tvAdministered.setVisibility(View.VISIBLE);
            vh.tvAdministered.setText(dose.getAdministeredSummaryLine());
        } else {
            vh.tvAdministered.setVisibility(View.GONE);
        }

        if (dose.isCompleted()) {
            vh.ivStatus.setImageResource(android.R.drawable.checkbox_on_background);
            vh.ivStatus.setColorFilter(ContextCompat.getColor(vh.itemView.getContext(), R.color.status_success));
        } else {
            vh.ivStatus.setImageResource(android.R.drawable.checkbox_off_background);
            vh.ivStatus.setColorFilter(ContextCompat.getColor(vh.itemView.getContext(), R.color.gray_button));
        }

        vh.itemView.setOnClickListener(v -> {
            int pos = vh.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) {
                return;
            }
            VaccinationRepository.RegisterRow r = rows.get(pos);
            if (r.dose == null) {
                return;
            }
            VaccinationRepository.VaccineUiModel model = r.dose;
            boolean next = !model.isCompleted();
            listener.onToggle(model, next);
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class VisitHeaderVh extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvProgramme;

        VisitHeaderVh(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvVisitTitle);
            tvProgramme = itemView.findViewById(R.id.tvVisitProgramme);
        }
    }

    static class DoseVh extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvDue;
        final TextView tvRoute;
        final TextView tvAdministered;
        final ImageView ivStatus;

        DoseVh(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVaccineName);
            tvDue = itemView.findViewById(R.id.tvVaccineDue);
            tvRoute = itemView.findViewById(R.id.tvVaccineRoute);
            tvAdministered = itemView.findViewById(R.id.tvVaccineAdministered);
            ivStatus = itemView.findViewById(R.id.ivStatusIcon);
        }
    }
}
