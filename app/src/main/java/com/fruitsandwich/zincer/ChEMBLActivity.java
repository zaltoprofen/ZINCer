package com.fruitsandwich.zincer;

import java.io.Serializable;

/**
 * Created by nakac on 15/07/21.
 */
public class ChEMBLActivity implements Serializable {
    private String uniprotId;
    private String swissprotId;
    private String description;
    private String affinity;
    private String ligandEfficiency;

    public String getUniprotId() {
        return uniprotId;
    }

    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId;
    }

    public String getSwissprotId() {
        return swissprotId;
    }

    public void setSwissprotId(String swissprotId) {
        this.swissprotId = swissprotId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAffinity() {
        return affinity;
    }

    public void setAffinity(String affinity) {
        this.affinity = affinity;
    }

    public String getLigandEfficiency() {
        return ligandEfficiency;
    }

    public void setLigandEfficiency(String ligandEfficiency) {
        this.ligandEfficiency = ligandEfficiency;
    }
}
