/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vhouse.prescriptionweb.prescription;

import static com.sun.istack.tools.DefaultAuthenticator.reset;
import com.vhouse.prescription.config.ConfigManagerRemote;
import com.vhouse.prescription.config.PropertyNotAvailbleException;
import com.vhouse.prescription.entity.ConfigEntity;
import com.vhouse.prescription.entity.MedicineEntity;
import com.vhouse.prescription.entity.MedicineSaleEntity;
import com.vhouse.prescription.entity.PatientEntity;
import com.vhouse.prescription.medicine.MedicineManagerLocal;
import com.vhouse.prescription.medicine.MedicineNotFoundException;
import com.vhouse.prescription.patient.PatientManagerLocal;
import com.vhouse.prescription.patient.PatientNotFoundException;
import com.vhouse.prescription.purchase.PurchaseDTO;
import com.vhouse.prescription.purchase.PurchaseManagerRemote;
import com.vhouse.prescription.purchase.PurchaseDetailsDTO;
import com.vhouse.prescription.purchase.PurchaseNotFoundException;
import com.vhouse.prescription.purchase.VendorNotFoundException;
import com.vhouse.prescription.sale.SaleDTO;
import com.vhouse.prescription.sale.SaleDetailsDTO;
import com.vhouse.prescription.sale.SaleManagerRemote;
import com.vhouse.prescriptionweb.reports.JasperReportDriver;
import com.vhouse.prescriptionweb.reports.SaleReportParam;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author ammar
 */
@ManagedBean
@ViewScoped
public class SaleBean implements Serializable {

    @EJB
    private PatientManagerLocal patientManager;
    @EJB
    private MedicineManagerLocal medicineManager;
    @EJB
    private SaleManagerRemote saleManager;
    @EJB
    private PurchaseManagerRemote purchaseManager;
    @EJB
    ConfigManagerRemote configManager;

  

    private int medicineId;
    private float saleQuantity;
    private float salePrice;
    private Float totalBill;
    private Float saleStock;
    private Date saleDate;

    private int purchaseMedicineId;
    private float purchaseQuantity;
    private float purchasePrice;
    private Float totalPurchaseBill;
    private Date purchaseDate = new Date();

//    private List<SaleDTO> saleDTOList;
    private List<SaleDetailsDTO> saleDetailsDTOList = new ArrayList<>();
    List<PurchaseDetailsDTO> purchaseDetailsDTOList;

    private List<MedicineEntity> medicineList;
    private List<MedicineEntity> purchaseMedicineList;

    public SaleBean() {
//        saleDTOList = new ArrayList<>();

        medicineList = new ArrayList<>();
        purchaseMedicineList = new ArrayList<>();
        purchaseDetailsDTOList = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
       
        saleQuantity = 0f;
        salePrice = 0f;
        totalBill = 0f;
        saleDate = new Date();

        purchasePrice = 0f;
        purchaseQuantity = 0f;
        totalPurchaseBill = 0f;

        try {
            this.medicineList = medicineManager.getAllActiveMedicine();
            this.purchaseMedicineList = medicineManager.getAllActiveMedicine();
        } catch (MedicineNotFoundException ex) {
            this.addMessage("Error", ex.getMessage());
        }
    }

    

    public void onMedicineMenuSelect(MedicineEntity medicine) {
        this.saleQuantity = 1;
//        this.salePrice = medicine.getMedicinePrice();
        System.out.println(medicine.getMedicinePrice());
    }

  
    public void addMedicineToTable() {
        try {
            MedicineEntity medicine = medicineManager.getMedicineById(medicineId);
            SaleDetailsDTO saleDetailsDTO = new SaleDetailsDTO(medicineId, medicine.getMedicineName(), this.saleQuantity, this.salePrice);
//            saleDto.setPatientId(patientId);
//            Float medicineTotal = saleDto.getSalePrice() * saleDto.getSaleQuantity();
//            saleDto.setMedicineTotal(medicineTotal);
            boolean exists = false;
            if (saleDetailsDTO.getSaleQuantity() > medicine.getMedicineStock()) {
                this.addMessage("Error", "This stock remains just : " + medicine.getMedicineStock());
            } else if (saleDetailsDTO.getSaleQuantity() == 0 || saleDetailsDTO.getSaleQuantity() < 0) {
                this.addMessage("Error", "Quantity Cannot be zero");
            } else {
                for (SaleDetailsDTO sdto : saleDetailsDTOList) {
                    if (sdto.getMedicineId() == saleDetailsDTO.getMedicineId()) {
                        exists = true;
                        sdto.setSaleQuantity(sdto.getSaleQuantity() + saleDetailsDTO.getSaleQuantity());
                        float sum = (float) saleDetailsDTOList.stream().mapToDouble(o -> o.getMedicineTotal().doubleValue()).sum();
                        this.totalBill = sum;
                    }
                }
                if (!exists) {

                    saleDetailsDTOList.add(saleDetailsDTO);
                }
                float sum = (float) saleDetailsDTOList.stream().mapToDouble(o -> o.getMedicineTotal().doubleValue()).sum();
                this.totalBill = sum;
                this.saleQuantity = 1;

            }
        } catch (MedicineNotFoundException ex) {
            Logger.getLogger(SaleBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void addPurchaseToTable() {
        try {
            MedicineEntity medicine = medicineManager.getMedicineById(purchaseMedicineId);
            PurchaseDetailsDTO purchaseDto = new PurchaseDetailsDTO(purchaseMedicineId, medicine.getMedicineName(), this.purchaseQuantity, this.purchasePrice);
            boolean exists = false;
            if (purchaseDto.getPurchaseQuantity() == 0 || purchaseDto.getPurchaseQuantity() < 0) {
                this.addMessage("Error", "Quantity Cannot be zero");
            } else {
                for (PurchaseDetailsDTO pDto : purchaseDetailsDTOList) {
                    if (pDto.getMedicineId() == purchaseDto.getMedicineId()) {
                        exists = true;
                        pDto.setPurchaseQuantity(pDto.getPurchaseQuantity() + purchaseDto.getPurchaseQuantity());
                        float sum = (float) purchaseDetailsDTOList.stream().mapToDouble(o -> o.getMedicineTotal().doubleValue()).sum();
                        this.totalPurchaseBill = sum;
                    }
                }
            }
            if (!exists) {

                purchaseDetailsDTOList.add(purchaseDto);
            }
            float sum = (float) purchaseDetailsDTOList.stream().mapToDouble(o -> o.getMedicineTotal().doubleValue()).sum();
            this.totalPurchaseBill = sum;
            this.purchaseQuantity = 1;

        } catch (MedicineNotFoundException ex) {
            Logger.getLogger(SaleBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void deleteMedicine(SaleDetailsDTO saleDTO) {
        saleDetailsDTOList.remove(saleDTO);
        float sum = (float) saleDetailsDTOList.stream().mapToDouble(o -> o.getMedicineTotal().doubleValue()).sum();
        this.totalBill = sum;
    }

    public void deletePurchaseMedicine(PurchaseDetailsDTO purchaseDTO) {
        purchaseDetailsDTOList.remove(purchaseDTO);
        float sum = (float) purchaseDetailsDTOList.stream().mapToDouble(o -> o.getMedicineTotal().doubleValue()).sum();
        this.totalPurchaseBill = sum;
    }

    public String addMedicineSale() {
        if (saleDetailsDTOList.isEmpty() || saleDetailsDTOList == null) {
            this.addMessage("Error", "Medicine List Cannot be empty");
        } else {

            SaleDTO saleDTO = new SaleDTO(saleDate);
            try {
                MedicineSaleEntity medSale = saleManager.addMedicineSale(saleDTO, saleDetailsDTOList);
                printSaleBill(medSale);
            } catch (MedicineNotFoundException | PatientNotFoundException ex) {
                this.addMessage("Error", ex.getMessage());
            }

            this.addMessage("SUCCESS!", "Sale Added Successfully");
            this.init();
        }
        return "Sale";
    }

    private void printSaleBill(MedicineSaleEntity sale) {
        try {
            JasperReportDriver driver = new JasperReportDriver();
            Class.forName("com.mysql.jdbc.Driver");
            ConfigEntity config = new ConfigEntity();

            Connection con = null;

            con = DriverManager.getConnection("jdbc:mysql://" + configManager.getConfigProperty("DB_HOST") + ":" + configManager.getConfigProperty("DB_PORT") + "/prescription?useSSL=false", configManager.getConfigProperty("DB_USER"), configManager.getConfigProperty("DB_PASSWORD"));

            SaleReportParam param = new SaleReportParam();

            InputStream inStreamAdvises = null;
            try {
                inStreamAdvises = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(param.getJasperFilePath());
            } catch (Exception e) {
                System.out.println("Error Getting Jasper File InputStream :" + e.getMessage());
            }
            Date utilDate = sale.getMedicineSaleDate();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            param.getParametersMap().put("sale_id", sale.getMedicineSaleId());
            param.getParametersMap().put("sale_date", sqlDate);
//            param.getParametersMap().put("sale_date", sale.getMedicineSaleDate());
            driver.printPOSReport(param, con);
//            driver.PDF(param, con);
            reset();
        } catch (SQLException ex) {
            //System.out.println("Error getting connection : " + ex.getMessage());
            Logger
                    .getLogger(AddPrescriptionBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyNotAvailbleException ex) {
            Logger.getLogger(AddPrescriptionBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String addMedicinePurchase() {
        if (purchaseDetailsDTOList.isEmpty() || purchaseDetailsDTOList == null) {
            this.addMessage("Error", "Purchase List cannot be Empty;");
        } else {
            PurchaseDTO purchase = new PurchaseDTO(purchaseDate, 1);

            try {
                purchaseManager.addPurchase(purchase, purchaseDetailsDTOList);
                this.addMessage("SUCCESS", "Purchase Addedd Sccessfully");
            } catch (MedicineNotFoundException | VendorNotFoundException | PurchaseNotFoundException ex) {
                this.addMessage("Error", ex.getMessage());
            }

            this.addMessage("SUCCESS!", "Purchase Added Successfully");
            this.init();
        }
        return "Sale.xhtml?faces-redirect=true";
    }

    private void addMessage(String msg, String details) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg, details));

    }

 
    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
        try {
            MedicineEntity medicine = medicineManager.getMedicineById(medicineId);
            this.salePrice = medicine.getMedicinePrice();
            this.saleQuantity = 1;
            this.saleStock = medicine.getMedicineStock();
        } catch (MedicineNotFoundException ex) {
            this.addMessage("Exception", ex.getMessage());
        }

    }

    public float getSaleQuantity() {
        return saleQuantity;
    }

    public void setSaleQuantity(float saleQuantity) {
        this.saleQuantity = saleQuantity;
    }

    public float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public Float getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(Float totalBill) {
        this.totalBill = totalBill;
    }

    public Float getSaleStock() {
        return saleStock;
    }

    public void setSaleStock(Float saleStock) {
        this.saleStock = saleStock;
    }

    public List<MedicineEntity> getMedicineList() {
        return medicineList;
    }

    public void setMedicineList(List<MedicineEntity> medicineList) {
        this.medicineList = medicineList;
    }

    public Float getTotalPurchaseBill() {
        return totalPurchaseBill;
    }

    public void setTotalPurchaseBill(Float totalPurchaseBill) {
        this.totalPurchaseBill = totalPurchaseBill;
    }

    public List<MedicineEntity> getPurchaseMedicineList() {
        return purchaseMedicineList;
    }

    public void setPurchaseMedicineList(List<MedicineEntity> purchaseMedicineList) {
        this.purchaseMedicineList = purchaseMedicineList;
    }

    public int getPurchaseMedicineId() {
        return purchaseMedicineId;
    }

    public void setPurchaseMedicineId(int purchaseMedicineId) {
        this.purchaseMedicineId = purchaseMedicineId;
        this.purchaseQuantity = 1;
        this.purchasePrice = 100;
    }

    public float getPurchaseQuantity() {
        return purchaseQuantity;
    }

    public void setPurchaseQuantity(float purchaseQuantity) {
        this.purchaseQuantity = purchaseQuantity;
    }

    public float getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(float purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public List<PurchaseDetailsDTO> getPurchaseDTOList() {
        return purchaseDetailsDTOList;
    }

    public void setPurchaseDTOList(List<PurchaseDetailsDTO> purchaseDTOList) {
        this.purchaseDetailsDTOList = purchaseDTOList;
    }

    public PatientManagerLocal getPatientManager() {
        return patientManager;
    }

    public void setPatientManager(PatientManagerLocal patientManager) {
        this.patientManager = patientManager;
    }

    public MedicineManagerLocal getMedicineManager() {
        return medicineManager;
    }

    public void setMedicineManager(MedicineManagerLocal medicineManager) {
        this.medicineManager = medicineManager;
    }

    public SaleManagerRemote getSaleManager() {
        return saleManager;
    }

    public void setSaleManager(SaleManagerRemote saleManager) {
        this.saleManager = saleManager;
    }

    public PurchaseManagerRemote getPurchaseManager() {
        return purchaseManager;
    }

    public void setPurchaseManager(PurchaseManagerRemote purchaseManager) {
        this.purchaseManager = purchaseManager;
    }



    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<SaleDetailsDTO> getSaleDetailsDTOList() {
        return saleDetailsDTOList;
    }

    public void setSaleDetailsDTOList(List<SaleDetailsDTO> saleDetailsDTOList) {
        this.saleDetailsDTOList = saleDetailsDTOList;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

}
