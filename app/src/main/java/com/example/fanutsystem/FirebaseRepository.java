package com.example.fanutsystem;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FirebaseRepository {

    public interface TipsCallback {
        void onSuccess(List<CommunityTip> tips);
        void onError(@NonNull Exception exception);
    }

    public interface ChildrenCallback {
        void onSuccess(List<Child> children);
        void onError(@NonNull Exception exception);
    }

    private static final String COLLECTION_CHILDREN = "children";
    private static final String COLLECTION_COMMUNITY_TIPS = "community_tips";

    private static FirebaseRepository instance;

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    private FirebaseRepository() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ensureSignedIn();
    }

    public static synchronized FirebaseRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseRepository();
        }
        return instance;
    }

    public void saveChild(@NonNull Child child) {
        ensureSignedIn();
        firestore.collection(COLLECTION_CHILDREN)
                .document(child.getId())
                .set(toChildMap(child));
    }

    public void fetchChildren(@NonNull ChildrenCallback callback) {
        ensureSignedIn();
        firestore.collection(COLLECTION_CHILDREN)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(result -> {
                    List<Child> children = new ArrayList<>();
                    for (DocumentSnapshot doc : result.getDocuments()) {
                        String id = nonEmpty(doc.getString("id"), doc.getId());
                        String name = nonEmpty(doc.getString("name"), "");
                        String dob = nonEmpty(doc.getString("dob"), "");
                        String gender = nonEmpty(doc.getString("gender"), "");
                        String muac = nonEmpty(doc.getString("muac"), "");
                        String weight = nonEmpty(doc.getString("weight"), "");
                        String height = nonEmpty(doc.getString("height"), "");
                        if (!name.isEmpty() && !dob.isEmpty()) {
                            children.add(new Child(id, name, dob, gender, muac, weight, height));
                        }
                    }
                    callback.onSuccess(children);
                })
                .addOnFailureListener(callback::onError);
    }

    public void saveCommunityTip(@NonNull CommunityTip tip) {
        ensureSignedIn();
        firestore.collection(COLLECTION_COMMUNITY_TIPS)
                .add(toTipMap(tip));
    }

    public void fetchCommunityTips(@NonNull TipsCallback callback) {
        ensureSignedIn();
        firestore.collection(COLLECTION_COMMUNITY_TIPS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(result -> {
                    List<CommunityTip> tips = new ArrayList<>();
                    for (DocumentSnapshot doc : result.getDocuments()) {
                        String english = nonEmpty(doc.getString("englishText"), "");
                        String chichewa = nonEmpty(doc.getString("chichewaText"), "");
                        String category = nonEmpty(doc.getString("category"), "Nutrition");
                        if (!english.isEmpty()) {
                            tips.add(new CommunityTip(english, chichewa, category));
                        }
                    }
                    callback.onSuccess(tips);
                })
                .addOnFailureListener(callback::onError);
    }

    private void ensureSignedIn() {
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously();
        }
    }

    @NonNull
    private Map<String, Object> toChildMap(@NonNull Child child) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", child.getId());
        map.put("name", child.getName());
        map.put("dob", child.getDob());
        map.put("gender", child.getGender());
        map.put("muac", child.getMuac());
        map.put("weight", child.getWeight());
        map.put("height", child.getHeight());
        map.put("createdAt", System.currentTimeMillis());
        return map;
    }

    @NonNull
    private Map<String, Object> toTipMap(@NonNull CommunityTip tip) {
        Map<String, Object> map = new HashMap<>();
        map.put("englishText", tip.getEnglishText());
        map.put("chichewaText", tip.getChichewaText());
        map.put("category", tip.getCategory());
        map.put("createdAt", System.currentTimeMillis());
        return map;
    }

    @NonNull
    private String nonEmpty(String value, String fallback) {
        return value != null ? value : fallback;
    }
}
