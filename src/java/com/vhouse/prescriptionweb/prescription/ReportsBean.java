/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.advise.AdviseManagerLocal;
import com.vhouse.prescription.advise.DTOMedicineSummary;
import com.vhouse.prescription.entity.MedicineSaleEntity;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author arahman
 */
@ManagedBean
@RequestScoped
public class ReportsBean implements Serializable {

    @EJB
    private AdviseManagerLocal adviseManager;
    private List<DTOMedicineSummary> report;
    private List<MedicineSaleEntity> medicineSaleReportByDay;
    public ReportsBean() {
        //System.out.println("Constructor called");

    }

    @PostConstruct
    public void init() {
        System.out.println("Backing beans init");
        
        
        
    }

    public void reloadMedicine() {
//        try {
        this.init();
//            this.medicineList = this.medicineManager.getAllMedicine();
//        } catch (MedicineNotFoundException ex) {
//            Logger.getLogger(MedicineBean.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private void addMessage(String msg, String details) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg, details));

    }

    /**
     * @return the report
     */
    public List<DTOMedicineSummary> getReport() {
        return adviseManager.getMedicineReport();
    }

    /**
     * @param report the report to set
     */
    public void setReport(List<DTOMedicineSummary> report) {
        this.report = report;
    }

    public AdviseManagerLocal getAdviseManager() {
        return adviseManager;
    }

    public void setAdviseManager(AdviseManagerLocal adviseManager) {
        this.adviseManager = adviseManager;
    }

    public List<MedicineSaleEntity> getMedicineSaleReportByDay() {
        return medicineSaleReportByDay;
    }

    public void setMedicineSaleReportByDay(List<MedicineSaleEntity> medicineSaleReportByDay) {
        this.medicineSaleReportByDay = medicineSaleReportByDay;
    }

 
}
