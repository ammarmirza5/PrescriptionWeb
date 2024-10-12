/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.entity.LabTestEntity;
import com.vhouse.prescription.medicine.MedicineNotFoundException;
import com.vhouse.prescription.medicine.MedicineManagerLocal;
import com.vhouse.prescription.entity.MedicineCategoryEntity;
import com.vhouse.prescription.entity.MedicineEntity;
import com.vhouse.prescription.entity.PatientEntity;
import com.vhouse.prescription.labtest.LabTestAlreadyExistsException;
import com.vhouse.prescription.labtest.LabTestNotFoundException;
import com.vhouse.prescription.labtest.LabTestSessionBean;
import com.vhouse.prescription.labtest.LabTestSessionRemote;
import com.vhouse.prescription.medicine.CategoryNotFoundException;
import com.vhouse.prescription.medicine.InvalidMedicineNameException;
import com.vhouse.prescription.medicine.MedicineAlreadyExsistsException;
import com.vhouse.prescription.medicine.MedicineCategoryAlreadyExistsException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author arahman
 */
@ManagedBean
@ViewScoped
public class MedicineBean implements Serializable {

    @EJB
    private MedicineManagerLocal medicineManager;
    @EJB
    private LabTestSessionRemote testManager;
    private List<MedicineCategoryEntity> medicineCategories;
    private String medicineCategory;
    private String medicineName;
    private String medicineId;
    private String medicineDosage;
    private String medicineDays;
    private List<MedicineEntity> medicineList;
    private List<LabTestEntity> testList;
    private List<PatientEntity> patientList;
    private String newMedicineCategory;
    private String newMedicineName;
    private String newMedicineDosage;
    private String newMedicineDays;
    private String newCategoryAdd;
    
    private String newTestName;
    private String newTestUnit;

    private boolean isMedicineDeleted;

    public MedicineBean() {
        //System.out.println("Constructor called");

    }

    @PostConstruct
    public void init() {
        System.out.println("Backing beans init");
        try {
            this.medicineList = this.medicineManager.getAllMedicine();
//            this.testList = testManager.getAllLabTestsForTable();
            this.newTestName = "";
            this.newTestUnit = "";

        } catch (MedicineNotFoundException ex) {
            Logger.getLogger(MedicineBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        List<MedicineCategoryEntity> categories = this.getMedicineCategories();
        this.newMedicineCategory = categories.get(0).getCategoryDescription();
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

    public void addNewMedicine() {
        try {
            medicineManager.addMedicine(newMedicineName, newMedicineDosage, newMedicineDays, newMedicineCategory);
            this.addMessage("Add Medicine", "Medicine Added Successfully\n" + newMedicineName);
            this.newMedicineName = "";
            this.newMedicineDosage = "";
            this.newMedicineDays = "";
        } catch (MedicineAlreadyExsistsException | InvalidMedicineNameException | CategoryNotFoundException ex) {
            this.addMessage("Add Medicine Error", ex.getMessage());
        }
    }
    public void addNewTest(){
        try {
            testManager.addLabTest(newTestName, newTestUnit);
            this.addMessage("Add Test", "Test Added Successfully\n" + newTestName);
            this.testList = testManager.getAllLabTestsForTable();
        } catch (LabTestAlreadyExistsException ex) {
            this.addMessage("Error", ex.getMessage());
        }
        
    }

    public void onCheckBoxClick(MedicineEntity medicine) {

        if (medicine.getMedicineDeleted()) {
//            System.out.println("true");
            this.deleteMedicine(medicine);

        } else if (medicine.getMedicineDeleted() == false) {
//            System.out.println("false");
            this.unDeleteMedicine(medicine);

        }

    }
    public void onTestCheckBoxClick(LabTestEntity test) {

        if (test.getTestDeleted()) {
//            System.out.println("true");
            this.deleteTest(test);

        } else if (test.getTestDeleted()== false) {
//            System.out.println("false");
            this.unDeleteTest(test);

        }

    }

    /**
     * @return the medicineCategories
     */
    public List<MedicineCategoryEntity> getMedicineCategories() {
        List<MedicineCategoryEntity> categories = this.medicineManager.getAllMedicineCategories();
        return categories;
    }

    /**
     * @return the medicineList
     */
    public List<MedicineEntity> getMedicineList() throws MedicineNotFoundException {
        return this.medicineList;
    }

    /**
     * @return the medicineCategory
     */
    public String getMedicineCategory() {
        return medicineCategory;
    }

    /**
     * @param medicineCategory the medicineCategory to set
     */
    public void setMedicineCategory(String medicineCategory) {
        this.medicineCategory = medicineCategory;
    }

    /**
     * @return the medicineDosage
     */
    public String getMedicineDosage() {
        return medicineDosage;
    }

    /**
     * @param medicineDosage the medicineDosage to set
     */
    public void setMedicineDosage(String medicineDosage) {
        this.medicineDosage = medicineDosage;
    }

    /**
     * @return the medicineDays
     */
    public String getMedicineDays() {
        return medicineDays;
    }

    /**
     * @param medicineDays the medicineDays to set
     */
    public void setMedicineDays(String medicineDays) {
        this.medicineDays = medicineDays;
    }

    /**
     * @return the medicineName
     */
    public String getMedicineName() {
        return medicineName;
    }

    /**
     * @param medicineName the medicineName to set
     */
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    /**
     * @return the medicineId
     */
    public String getMedicineId() {
        return medicineId;
    }

    /**
     * @param medicineId the medicineId to set
     */
    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
        try {
            MedicineEntity medicine = medicineManager.getMedicineById(Integer.parseInt(medicineId));
            this.medicineDosage = medicine.getDefatultDosage();
            this.setMedicineDays(medicine.getDefaultDays());

        } catch (MedicineNotFoundException ex) {
            Logger.getLogger(MedicineBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the newMedicineCategory
     */
    public String getNewMedicineCategory() {
        return newMedicineCategory;
    }

    /**
     * @param newMedicineCategory the newMedicineCategory to set
     */
    public void setNewMedicineCategory(String newMedicineCategory) {
        this.newMedicineCategory = newMedicineCategory;
    }

    /**
     * @return the newMedicineName
     */
    public String getNewMedicineName() {
        return newMedicineName;
    }

    /**
     * @param newMedicineName the newMedicineName to set
     */
    public void setNewMedicineName(String newMedicineName) {
        this.newMedicineName = newMedicineName;
    }

    /**
     * @return the newMedicineDosage
     */
    public String getNewMedicineDosage() {
        return newMedicineDosage;
    }

    /**
     * @param newMedicineDosage the newMedicineDosage to set
     */
    public void setNewMedicineDosage(String newMedicineDosage) {
        this.newMedicineDosage = newMedicineDosage;
    }

    /**
     * @return the newMedicineDays
     */
    public String getNewMedicineDays() {
        return newMedicineDays;
    }

    /**
     * @param newMedicineDays the newMedicineDays to set
     */
    public void setNewMedicineDays(String newMedicineDays) {
        this.newMedicineDays = newMedicineDays;
    }

    public void addNewCategory() {
        try {
            medicineManager.addNewCategory(newCategoryAdd);
            this.addMessage("New Category", "Category " + newCategoryAdd + " Added Successfully");
        } catch (MedicineCategoryAlreadyExistsException ex) {
            this.addMessage("New Category Error", ex.getMessage());
        }
    }

    /**
     * @return the addNewCategory
     */
    public String getNewCategoryAdd() {
        return newCategoryAdd;
    }

    /**
     * @param newCategoryAdd the addNewCategory to set
     */
    public void setNewCategoryAdd(String newCategoryAdd) {
        this.newCategoryAdd = newCategoryAdd;
    }

    public void onRowSelect(SelectEvent event) {

    }

    public void onRowEdit(RowEditEvent event) {
        System.out.println("OnRowEdit called...");
        MedicineEntity medicine = (MedicineEntity) event.getObject();
        System.out.println("Editint Medicine:" + medicine.getMedicineName() + ",ID:" + medicine.getMedicineId()+",Dosage:"+medicine.getDefatultDosage());
        try {
//                    MedicineEntity newMedicine =(MedicineEntity) event.getSource();
            medicineManager.updateMedicine(medicine.getMedicineId(), medicine.getMedicineName(),medicine.getDefatultDosage(),medicine.getDefaultDays(),medicine.getMedicineStock(),medicine.getMedicinePrice());
        } catch (MedicineNotFoundException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(MedicineBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.addMessage("Update", "Medicine Updated");

    }

    public void onRowEditCancel(RowEditEvent event) {
        MedicineEntity medicine = (MedicineEntity) event.getObject();
        this.addMessage("Update Calcelled", "Medicine Updated");

    }
    
     public void onRowTestEdit(RowEditEvent event) {
        System.out.println("OnRowEdit called...");
        LabTestEntity test = (LabTestEntity) event.getObject();
        try {
            //        System.out.println("Editint Medicine:" + test.getMedicineName() + ",ID:" + test.getMedicineId()+",Dosage:"+test.getDefatultDosage());
            testManager.updateTest(test.getTestId(), test.getTestDescription(), test.getUnits());
        } catch (LabTestNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        this.addMessage("Update", "Medicine Updated");

    }

   
    public void onCellEdit(CellEditEvent event) {

    }

    public void deleteMedicine(MedicineEntity medicine) {
        try {
            medicineManager.markMedicineDeleted(medicine);
            this.addMessage("Medicine Deleted", medicine.getMedicineName());
        } catch (MedicineNotFoundException ex) {
            Logger.getLogger(MedicineBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void unDeleteMedicine(MedicineEntity medicine) {
        try {
            medicineManager.markMedicineUnDeleted(medicine);
            this.addMessage("Medicine Restored", medicine.getMedicineName());
        } catch (MedicineNotFoundException ex) {
            Logger.getLogger(MedicineBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     public void deleteTest(LabTestEntity test) {
            testManager.markTestDeleted(test);
            this.addMessage("Medicine Deleted", test.getTestDescription());
    }

    public void unDeleteTest(LabTestEntity test) {
            testManager.markTestUnDeleted(test);
            this.addMessage("Test Restored", test.getTestDescription());
     
    }

    /**
     * @return the isMedicineDeleted
     */
    public boolean isIsMedicineDeleted() {
        return isMedicineDeleted;
    }

    /**
     * @param isMedicineDeleted the isMedicineDeleted to set
     */
    public void setIsMedicineDeleted(boolean isMedicineDeleted) {
        this.isMedicineDeleted = isMedicineDeleted;
    }

    /**
     * @return the testList
     */
    public List<LabTestEntity> getTestList() {
        return testList;
    }

    /**
     * @param testList the testList to set
     */
    public void setTestList(List<LabTestEntity> testList) {
        this.testList = testList;
    }

    /**
     * @return the newTestName
     */
    public String getNewTestName() {
        return newTestName;
    }

    /**
     * @param newTestName the newTestName to set
     */
    public void setNewTestName(String newTestName) {
        this.newTestName = newTestName;
    }

    /**
     * @return the newTestUnit
     */
    public String getNewTestUnit() {
        return newTestUnit;
    }

    /**
     * @param newTestUnit the newTestUnit to set
     */
    public void setNewTestUnit(String newTestUnit) {
        this.newTestUnit = newTestUnit;
    }
}
