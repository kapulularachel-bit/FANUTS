package com.example.fanutsystem;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {

    public static List<DangerSignAdapter.DangerSign> getCriticalDangerSigns() {
        List<DangerSignAdapter.DangerSign> signs = new ArrayList<>();
        
        // General Danger Signs (WHO IMCI)
        signs.add(new DangerSignAdapter.DangerSign("Child unable to eat or breastfeed"));
        signs.add(new DangerSignAdapter.DangerSign("Persistent vomiting (vomits everything)"));
        signs.add(new DangerSignAdapter.DangerSign("Convulsions (Fits)"));
        signs.add(new DangerSignAdapter.DangerSign("Lethargic or unconscious"));
        
        // Respiratory Signs
        signs.add(new DangerSignAdapter.DangerSign("Difficulty breathing (Fast breathing)"));
        signs.add(new DangerSignAdapter.DangerSign("Chest indrawing"));
        signs.add(new DangerSignAdapter.DangerSign("Stridor (Noise when breathing in)"));

        // Nutritional & Physical Signs
        signs.add(new DangerSignAdapter.DangerSign("Severe wasting (Very low weight)"));
        signs.add(new DangerSignAdapter.DangerSign("Very low MUAC (< 11.5cm)"));
        signs.add(new DangerSignAdapter.DangerSign("Swelling of both feet (Kwashiorkor sign)"));
        
        // Other Critical Signs
        signs.add(new DangerSignAdapter.DangerSign("High fever (> 38.5°C)"));
        signs.add(new DangerSignAdapter.DangerSign("Stiff neck (Meningitis sign)"));
        signs.add(new DangerSignAdapter.DangerSign("Very pale palms (Severe anemia)"));

        return signs;
    }

    public static List<HealthFacility> getNearbyHealthFacilities() {
        List<HealthFacility> facilities = new ArrayList<>();
        // Predefined facilities in Malawi with coordinates, districts, and services
        facilities.add(new HealthFacility("Kamuzu Central Hospital", "+265 1 753 555", -13.9744, 33.7821, "Lilongwe", "Tertiary care, Emergency, Pediatrics, Nutrition Rehab"));
        facilities.add(new HealthFacility("Bwaila Hospital", "+265 1 751 622", -13.9833, 33.7833, "Lilongwe", "Maternity, Under-5 Clinic, Outpatient"));
        facilities.add(new HealthFacility("Queen Elizabeth Central Hospital", "+265 1 874 333", -15.8000, 35.0167, "Blantyre", "Specialized Pediatric Care, ICU, Nutrition Center"));
        facilities.add(new HealthFacility("Zomba Central Hospital", "+265 1 526 222", -15.3833, 35.3333, "Zomba", "General Surgery, Pediatrics, Maternal Health"));
        facilities.add(new HealthFacility("Mzuzu Central Hospital", "+265 1 320 633", -11.4583, 34.0150, "Mzuzu", "Regional Referral, Emergency, OPD"));
        facilities.add(new HealthFacility("Area 18 Health Centre", "+265 1 794 111", -13.9500, 33.8000, "Lilongwe", "Primary Care, Immunization, Outpatient"));
        facilities.add(new HealthFacility("Kawale Health Centre", "+265 1 721 444", -14.0000, 33.7667, "Lilongwe", "Primary Care, Maternity, Under-5 Clinic"));
        return facilities;
    }

    public static List<HealthFacility> getEmergencyContacts() {
        List<HealthFacility> contacts = new ArrayList<>();
        contacts.add(new HealthFacility("Ambulance (National)", "998", 0, 0, "National", "Emergency Medical Transport"));
        contacts.add(new HealthFacility("Local Health Worker Helpline", "+265 888 123 456", 0, 0, "Local", "General Health Advice, Referrals"));
        contacts.add(new HealthFacility("Nutrition Crisis Hotline", "+265 999 789 012", 0, 0, "National", "Support for Malnutrition cases"));
        return contacts;
    }

    public static List<CommunityTip> getPreloadedTips() {
        List<CommunityTip> tips = new ArrayList<>();
        tips.add(new CommunityTip(
            "Give children porridge with groundnuts for better nutrition.",
            "Patsani ana phala losakaniza ndi mtedza kuti akhale ndi thanzi labwino.",
            "Nutrition"
        ));
        tips.add(new CommunityTip(
            "Wash hands with soap before feeding your child to prevent diarrhea.",
            "Sambani m'manja ndi sopo musanadyetse mwana pofuna kupewa kutsegula m'mimba.",
            "Hygiene"
        ));
        tips.add(new CommunityTip(
            "Ensure your child completes all vaccinations by 15 months.",
            "Onetsetsani kuti mwana wanu walandira katemera yense asanafike miyezi 15.",
            "Vaccination"
        ));
        tips.add(new CommunityTip(
            "If your child has a fever, take them to the clinic immediately.",
            "Ngati mwana wanu akutentha thupi, muperekeni ku chipatala mwansanga.",
            "Illness care"
        ));
        tips.add(new CommunityTip(
            "Exclusive breastfeeding for the first 6 months is best for baby's growth.",
            "Kuyamwitsa mwana mkaka wa m'mawere wokha kwa miyezi 6 yoyambirira ndikofunika kwambiri.",
            "Nutrition"
        ));
        return tips;
    }

    public static class HealthFacility {
        private String name;
        private String contact;
        private double latitude;
        private double longitude;
        private String district;
        private String services;

        public HealthFacility(String name, String contact, double latitude, double longitude, String district, String services) {
            this.name = name;
            this.contact = contact;
            this.latitude = latitude;
            this.longitude = longitude;
            this.district = district;
            this.services = services;
        }

        public String getName() { return name; }
        public String getContact() { return contact; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getDistrict() { return district; }
        public String getServices() { return services; }
        
        @Override
        public String toString() {
            return name + " (" + district + ")";
        }

        public String getFullDetails() {
            return name + "\nDistrict: " + district + "\nContact: " + contact + (services != null ? "\nServices: " + services : "");
        }
    }
}
