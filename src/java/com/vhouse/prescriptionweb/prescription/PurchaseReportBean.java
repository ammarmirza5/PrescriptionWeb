/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.purchase.PurchaseManagerRemote;
import com.vhouse.prescription.purchase.PurchaseNotFoundException;
import com.vhouse.prescription.purchase.PurchaseReportDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author ammar
 */
@ManagedBean
@ViewScoped
public class PurchaseReportBean implements Serializable {

    @EJB
    PurchaseManagerRemote purchaseManager;
    private Date startDate;
    private Date endDate;

    private List<PurchaseReportDTO> purchaseReportDTOList = new ArrayList<>();
    private List<PurchaseReportDTO> billReportDTOList = new ArrayList<>();
    private PurchaseReportDTO selectedDTO;

    public PurchaseReportBean() {

    }

    @PostConstruct
    public void init() {
        try {
            this.purchaseReportDTOList = purchaseManager.getPurchaseReportBetweenDates(startDate, endDate);
        } catch (PurchaseNotFoundException ex) {
            Logger.getLogger(PurchaseReportBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onDateSelect() {
        try {
            this.purchaseReportDTOList = purchaseManager.getPurchaseReportBetweenDates(startDate, endDate);
        } catch (PurchaseNotFoundException ex) {
        }
    }

    public void onRowSelect() {
        this.billReportDTOList = purchaseManager.getPurchaseReportByDate(selectedDTO.getPurchaseDate());
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<PurchaseReportDTO> getPurchaseReportDTOList() {
        return purchaseReportDTOList;
    }

    public void setPurchaseReportDTOList(List<PurchaseReportDTO> purchaseReportDTOList) {
        this.purchaseReportDTOList = purchaseReportDTOList;
    }

    public PurchaseReportDTO getSelectedDTO() {
        return selectedDTO;
    }

    public void setSelectedDTO(PurchaseReportDTO selectedDTO) {
        this.selectedDTO = selectedDTO;
    }

    public List<PurchaseReportDTO> getBillReportDTOList() {
        return billReportDTOList;
    }

    public void setBillReportDTOList(List<PurchaseReportDTO> billReportDTOList) {
        this.billReportDTOList = billReportDTOList;
    }
}
