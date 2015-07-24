package com.fruitsandwich.zincer;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by nakac on 15/07/21.
 */
public class ZincDetail implements Serializable {
    private Long zincId;
    private String commonName;
    private String molecularWeight;
    private String rotatableBonds;
    private List<ChEMBLActivity> activities;
    private String imageUrl;

    public Long getZincId() {
        return zincId;
    }

    public void setZincId(Long zincId) {
        this.zincId = zincId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getMolecularWeight() {
        return molecularWeight;
    }

    public void setMolecularWeight(String molecularWeight) {
        this.molecularWeight = molecularWeight;
    }

    public String getRotatableBonds() {
        return rotatableBonds;
    }

    public void setRotatableBonds(String rotatableBonds) {
        this.rotatableBonds = rotatableBonds;
    }

    public List<ChEMBLActivity> getActivities() {
        return activities;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setActivities(List<ChEMBLActivity> activities) {
        this.activities = activities;
    }
}
