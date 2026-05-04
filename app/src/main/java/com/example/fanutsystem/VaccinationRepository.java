package com.example.fanutsystem;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class VaccinationRepository {

    private VaccinationRepository() {}

    public static List<VaccineUiModel> loadSchedule(Context context, Child child) {
        List<VaccineUiModel> models = buildModels(context, child);
        models.sort(BY_ENCOUNTER_THEN_DUE);
        return models;
    }

    /** Rows for facility-style register: visit headers + dose lines. */
    public static List<RegisterRow> loadRegisterRows(Context context, Child child) {
        List<VaccineUiModel> models = buildModels(context, child);
        models.sort(BY_ENCOUNTER_THEN_DUE);
        List<RegisterRow> rows = new ArrayList<>();
        int lastEncounter = -1;
        for (VaccineUiModel v : models) {
            if (v.getEncounterOrder() != lastEncounter) {
                rows.add(RegisterRow.visitHeader(v.getEncounterTitle(), v.getEncounterProgramme()));
                lastEncounter = v.getEncounterOrder();
            }
            rows.add(RegisterRow.dose(v));
        }
        return rows;
    }

    private static final Comparator<VaccineUiModel> BY_ENCOUNTER_THEN_DUE =
            Comparator.comparingInt(VaccineUiModel::getEncounterOrder)
                    .thenComparingLong(VaccineUiModel::getDueDateMillis);

    private static List<VaccineUiModel> buildModels(Context context, Child child) {
        if (child == null || child.getId() == null || child.getId().isEmpty()) {
            return Collections.emptyList();
        }
        List<EpiSchedule.ScheduledDose> doses;
        try {
            doses = EpiSchedule.buildSchedule(child.getDob());
        } catch (ParseException e) {
            return Collections.emptyList();
        }

        VaccinationDbHelper db = VaccinationDbHelper.getInstance(context);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        long now = System.currentTimeMillis();

        List<VaccineUiModel> out = new ArrayList<>();
        for (EpiSchedule.ScheduledDose d : doses) {
            boolean done = db.isCompleted(child.getId(), d.code);
            long givenAt = db.getGivenAtMillis(child.getId(), d.code);
            String duePart = df.format(d.dueDateMillis);
            String statusSuffix = "";
            if (!done) {
                if (d.dueDateMillis < startOfTodayMillis()) {
                    statusSuffix = " · " + context.getString(R.string.vaccine_status_overdue);
                } else if (d.dueDateMillis <= now + 7L * 24 * 60 * 60 * 1000) {
                    statusSuffix = " · " + context.getString(R.string.vaccine_status_due_soon);
                }
            }
            String dueLine = context.getString(R.string.immunization_due_line_fmt, duePart) + statusSuffix;

            String administeredLine = "";
            if (done && givenAt > 0) {
                administeredLine = context.getString(R.string.immunization_administered_fmt,
                        df.format(givenAt));
            } else if (done) {
                administeredLine = context.getString(R.string.immunization_administered_unknown);
            }

            String encounterTitle = contactTitle(context, d.encounterOrder);
            String encounterProgramme = contactProgramme(context, d.encounterOrder);

            out.add(new VaccineUiModel(
                    d.code,
                    d.displayName,
                    dueLine,
                    administeredLine,
                    routeSiteLine(context, d.code),
                    d.dueDateMillis,
                    givenAt,
                    d.encounterOrder,
                    encounterTitle,
                    encounterProgramme,
                    done));
        }
        return out;
    }

    private static String contactTitle(Context context, int encounterOrder) {
        String[] titles = context.getResources().getStringArray(R.array.immunization_contact_titles);
        if (encounterOrder >= 0 && encounterOrder < titles.length) {
            return titles[encounterOrder];
        }
        return "";
    }

    private static String contactProgramme(Context context, int encounterOrder) {
        String[] lines = context.getResources().getStringArray(R.array.immunization_contact_programme);
        if (encounterOrder >= 0 && encounterOrder < lines.length) {
            return lines[encounterOrder];
        }
        return "";
    }

    private static String routeSiteLine(Context context, String code) {
        int resId;
        switch (code) {
            case "BCG":
                resId = R.string.vaccine_route_bcg;
                break;
            case "OPV0":
            case "OPV1":
            case "OPV2":
            case "OPV3":
                resId = R.string.vaccine_route_opv;
                break;
            case "DPT1":
            case "DPT2":
            case "DPT3":
                resId = R.string.vaccine_route_penta;
                break;
            case "PCV1":
            case "PCV2":
            case "PCV3":
                resId = R.string.vaccine_route_pcv;
                break;
            case "ROTA1":
            case "ROTA2":
                resId = R.string.vaccine_route_rota;
                break;
            case "IPV":
                resId = R.string.vaccine_route_ipv;
                break;
            case "VITA1":
                resId = R.string.vaccine_route_vita;
                break;
            case "MR1":
            case "MR2":
                resId = R.string.vaccine_route_mr;
                break;
            default:
                resId = R.string.vaccine_route_default;
                break;
        }
        return context.getString(resId);
    }

    private static long startOfTodayMillis() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(java.util.Calendar.HOUR_OF_DAY, 0);
        c.set(java.util.Calendar.MINUTE, 0);
        c.set(java.util.Calendar.SECOND, 0);
        c.set(java.util.Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    /** Prefer earliest overdue dose; otherwise earliest upcoming incomplete (list sorted by schedule). */
    public static VaccineUiModel findNextIncomplete(List<VaccineUiModel> sortedBySchedule) {
        long startToday = startOfTodayMillis();
        VaccineUiModel firstOverdue = null;
        VaccineUiModel firstUpcoming = null;
        for (VaccineUiModel v : sortedBySchedule) {
            if (v.isCompleted()) {
                continue;
            }
            if (v.getDueDateMillis() < startToday) {
                if (firstOverdue == null) {
                    firstOverdue = v;
                }
            } else if (firstUpcoming == null) {
                firstUpcoming = v;
            }
        }
        return firstOverdue != null ? firstOverdue : firstUpcoming;
    }

    public static class RegisterRow {
        public static final int TYPE_VISIT_HEADER = 0;
        public static final int TYPE_DOSE = 1;

        public final int rowType;
        public final String visitTitle;
        public final String visitProgramme;
        public final VaccineUiModel dose;

        private RegisterRow(int rowType, String visitTitle, String visitProgramme, VaccineUiModel dose) {
            this.rowType = rowType;
            this.visitTitle = visitTitle;
            this.visitProgramme = visitProgramme;
            this.dose = dose;
        }

        public static RegisterRow visitHeader(String title, String programmeLine) {
            return new RegisterRow(TYPE_VISIT_HEADER, title, programmeLine, null);
        }

        public static RegisterRow dose(VaccineUiModel model) {
            return new RegisterRow(TYPE_DOSE, null, null, model);
        }
    }

    public static class VaccineUiModel {
        private final String code;
        private final String displayName;
        private final String dueSummaryLine;
        private final String administeredSummaryLine;
        private final String routeSiteLine;
        private final long dueDateMillis;
        private final long givenAtMillis;
        private final int encounterOrder;
        private final String encounterTitle;
        private final String encounterProgramme;
        private boolean completed;

        public VaccineUiModel(String code, String displayName,
                              String dueSummaryLine, String administeredSummaryLine,
                              String routeSiteLine,
                              long dueDateMillis, long givenAtMillis,
                              int encounterOrder, String encounterTitle, String encounterProgramme,
                              boolean completed) {
            this.code = code;
            this.displayName = displayName;
            this.dueSummaryLine = dueSummaryLine;
            this.administeredSummaryLine = administeredSummaryLine;
            this.routeSiteLine = routeSiteLine;
            this.dueDateMillis = dueDateMillis;
            this.givenAtMillis = givenAtMillis;
            this.encounterOrder = encounterOrder;
            this.encounterTitle = encounterTitle;
            this.encounterProgramme = encounterProgramme;
            this.completed = completed;
        }

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDueSummaryLine() {
            return dueSummaryLine;
        }

        public String getAdministeredSummaryLine() {
            return administeredSummaryLine;
        }

        public String getRouteSiteLine() {
            return routeSiteLine;
        }

        public long getDueDateMillis() {
            return dueDateMillis;
        }

        public long getGivenAtMillis() {
            return givenAtMillis;
        }

        public int getEncounterOrder() {
            return encounterOrder;
        }

        public String getEncounterTitle() {
            return encounterTitle;
        }

        public String getEncounterProgramme() {
            return encounterProgramme;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}
