package com.chetiwen.object.insurance;

public class InsResult {
    private String status;
    private String claimDate;
    private String description;
    private String totalFee;
    private String laborFee;
    private String materialFee;
    private String repairDetail;
    private Enum material;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(String claimDate) {
        this.claimDate = claimDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getLaborFee() {
        return laborFee;
    }

    public void setLaborFee(String laborFee) {
        this.laborFee = laborFee;
    }

    public String getMaterialFee() {
        return materialFee;
    }

    public void setMaterialFee(String materialFee) {
        this.materialFee = materialFee;
    }

    public String getRepairDetail() {
        return repairDetail;
    }

    public void setRepairDetail(String repairDetail) {
        this.repairDetail = repairDetail;
    }

    public Enum getMaterial() {
        return material;
    }

    public void setMaterial(Enum material) {
        this.material = material;
    }
}
