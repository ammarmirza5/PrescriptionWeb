/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.config.ConfigManagerRemote;
import com.vhouse.prescription.purchase.PurchaseManagerRemote;
import com.vhouse.prescription.sale.SaleManagerRemote;
import com.vhouse.prescription.sale.SaleReportDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class SaleReportBean implements Serializable {

    @EJB
    private SaleManagerRemote saleManager;
    @EJB
    private PurchaseManagerRemote purchaseManager;
    @EJB
    private ConfigManagerRemote configManager;

    private Date startDate;
    private Date endDate;
    private List<SaleReportDTO> saleReportDTOList = new ArrayList<>();
    private List<SaleReportDTO> billReportDTOList = new ArrayList<>();
    private SaleReportDTO selectedDate;

    public SaleReportBean() {

    }

    @PostConstruct
    public void init() {
        this.saleReportDTOList = saleManager.getDailySaleReport(startDate, endDate);
    }

    public void onDateSelect() {
        this.saleReportDTOList = saleManager.getDailySaleReport(startDate, endDate);

    }

    public void onRowSelect() {
        System.out.println("Date : "+selectedDate.getSaleDate());
        this.billReportDTOList = saleManager.getSaleReportByDate(selectedDate.getSaleDate());
//        System.out.println("total : "+billReportDTOList.get(0).getTotalSale());
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

    public List<SaleReportDTO> getSaleReportDTOList() {
        return saleReportDTOList;
    }

    public void setSaleReportDTOList(List<SaleReportDTO> saleReportDTOList) {
        this.saleReportDTOList = saleReportDTOList;
    }

    public SaleReportDTO getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(SaleReportDTO selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List<SaleReportDTO> getBillReportDTOList() {
        return billReportDTOList;
    }

    public void setBillReportDTOList(List<SaleReportDTO> billReportDTOList) {
        this.billReportDTOList = billReportDTOList;
    }

}
