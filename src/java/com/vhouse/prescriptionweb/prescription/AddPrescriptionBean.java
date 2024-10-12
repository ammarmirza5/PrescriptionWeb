/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.advise.AdviseManagerLocal;
import com.vhouse.prescription.advise.DTOAdvise;
import com.vhouse.prescription.advise.DTOPatient;
import com.vhouse.prescription.config.ConfigManagerRemote;
import com.vhouse.prescription.config.PropertyNotAvailbleException;
import com.vhouse.prescription.entity.AdviseEntity;
import com.vhouse.prescription.entity.ConfigEntity;
import com.vhouse.prescription.entity.LabTestEntity;
import com.vhouse.prescription.entity.LabTestResultEntity;
import com.vhouse.prescription.medicine.MedicineNotFoundException;
import com.vhouse.prescription.medicine.MedicineManagerLocal;
import com.vhouse.prescription.entity.MedicineCategoryEntity;
import com.vhouse.prescription.entity.MedicineEntity;
import com.vhouse.prescription.entity.PatientEntity;
import com.vhouse.prescription.entity.PatientVisitEntity;
import com.vhouse.prescription.labtest.DTOTestResult;
import com.vhouse.prescription.labtest.LabTestNotFoundException;
import com.vhouse.prescription.labtest.LabTestResultNotFoundException;
import com.vhouse.prescription.labtest.LabTestSessionRemote;
import com.vhouse.prescription.medicine.CategoryNotFoundException;
import com.vhouse.prescription.medicine.InvalidMedicineNameException;
import com.vhouse.prescription.medicine.MedicineAlreadyExsistsException;
import com.vhouse.prescription.medicine.MedicineCategoryAlreadyExistsException;
import com.vhouse.prescription.patient.InvalidPatientAddressException;
import com.vhouse.prescription.patient.InvalidPatientNameException;
import com.vhouse.prescription.patient.PatientManagerLocal;
import com.vhouse.prescription.patient.PatientNotFoundException;
import com.vhouse.prescription.user.UserNotFoundException;
import com.vhouse.prescription.visit.PatientVisitNotFoundException;
import com.vhouse.prescription.visit.VisitManagerLocal;
import com.vhouse.prescriptionweb.reports.JasperReportDriver;
import com.vhouse.prescriptionweb.reports.PrescriptionReportParam;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author arahman
 */
@ManagedBean
@ViewScoped
public class AddPrescriptionBean implements Serializable {

    @EJB
    private PatientManagerLocal patientManager;
    @EJB
    private MedicineManagerLocal medicineManager;
    @EJB
    private VisitManagerLocal visitManager;
    @EJB
    private ConfigManagerRemote configManager;
    @EJB
    private AdviseManagerLocal adviseManager;
    @EJB
    LabTestSessionRemote labTestManager;

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    private boolean patientType;
    private String patientId;
    private String patientString;
    private String patientName;
    private String patientAddress;
    private String patientPhone;
    private Integer patientAge;
    private String patientBp;
    private Integer patientTemperature;
    private Integer patientWeight;
    private Integer patientPulse;
    private String primaryDiagnosis;
    private String secondaryDiagnosis;
    private String tertiaryDiagnosis;
    private boolean patientGender = false;
    private List<MedicineCategoryEntity> medicineCategories = new ArrayList();
    private String medicineCategory;
    private String medicineName;
    private String medicineId;
    private String medicineDosage;
    private String medicineDays;
    private List<MedicineEntity> medicineList;
    private String testDescription;
    private Float testValue;
    private String testUnit;
    private String testId;
    private List<LabTestEntity> testsList;
    private List<DTOTestResult> testResultList;
    private List<PatientEntity> patientList;
    private List<DTOAdvise> patientAdvises;
    private String newMedicineCategory;
    private String newMedicineName;
    private String newMedicineDosage;
    private String newMedicineDays;
    private String newCategoryAdd;

    public AddPrescriptionBean() {
//        //System.out.println("Constructor called");
        patientType = true;
        patientAge = 30;
        patientBp = "120/80";
        patientTemperature = 98;
        patientWeight = 70;
        patientPulse = 72;
        medicineCategories = new ArrayList();
        medicineList = new ArrayList();
        testsList = new ArrayList();
        testResultList = new ArrayList();
        patientList = new ArrayList();
        patientAdvises = new ArrayList();
    }

    @PostConstruct
    public void init() {
        //System.out.println("Backing beans init");
        try {
            List<MedicineEntity> allMedicine = this.medicineManager.getAllMedicine();
            this.medicineId = allMedicine.get(0).getMedicineId().toString();
            this.medicineDosage = allMedicine.get(0).getDefatultDosage();
            this.medicineDays = allMedicine.get(0).getDefaultDays();

        } catch (MedicineNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        List<MedicineCategoryEntity> categories = this.getMedicineCategories();
        this.newMedicineCategory = categories.get(0).getCategoryDescription();
        List<LabTestEntity> allTests = this.labTestManager.getAllLabTests();
        this.testDescription = allTests.get(0).getTestDescription();
        this.testUnit = allTests.get(0).getUnits();
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

    public void addPatient() {
        try {
            this.patientManager.addPatient(getPatientName(), getPatientAddress(), isPatientGender(), getPatientPhone(), null, getLoginBean().getSubscriberPhoneNumber());
        } catch (InvalidPatientAddressException | InvalidPatientNameException | UserNotFoundException ex) {
            this.addMessage("Add Patient", ex.getMessage());
        }
    }

    /**
     * @return the patientName
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * @param patientName the patientName to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * @return the patientAddress
     */
    public String getPatientAddress() {
        return patientAddress;
    }

    /**
     * @param patientAddress the patientAddress to set
     */
    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    /**
     * @return the patientPhone
     */
    public String getPatientPhone() {
        return patientPhone;
    }

    /**
     * @param patientPhone the patientPhone to set
     */
    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    /**
     * @return the patientAge
     */
    public Integer getPatientAge() {
        return patientAge;
    }

    /**
     * @param patientAge the patientAge to set
     */
    public void setPatientAge(Integer patientAge) {
        this.patientAge = patientAge;
    }

    /**
     * @return the patientGender
     */
    public boolean isPatientGender() {
        return patientGender;
    }

    /**
     * @param patientGender the patientGender to set
     */
    public void setPatientGender(boolean patientGender) {
        this.patientGender = patientGender;
    }

    /**
     * @return the medicineCategories
     */
    public List<MedicineCategoryEntity> getMedicineCategories(Boolean allCategories) {
        List<MedicineCategoryEntity> categories = this.getMedicineCategories();
        MedicineCategoryEntity all = new MedicineCategoryEntity();
        all.setCategoryDescription("All Medicine");
        all.setCategoryId(0);
        //System.out.println("All Categories : "+allCategories);
        if (allCategories) {
            //System.out.println("All Categories added...");
            categories.add(0, all);
        }
        return categories;

    }

    public List<MedicineCategoryEntity> getMedicineCategories() {
        return this.medicineManager.getAllMedicineCategories();
    }

    /**
     * @return the medicineList
     */
    public List<MedicineEntity> getMedicineList() throws MedicineNotFoundException {
//        //System.out.println("Getting medicine");
        if (this.medicineCategory == null || this.medicineCategory.isEmpty() || this.medicineCategory.startsWith("All")) {
//            //System.out.println("getting all medicine in backing bean");
            return medicineManager.getAllActiveMedicine();
        }
//        //System.out.println("getting medicine for category " + this.medicineCategory + " in backing bean");
        return medicineManager.getMedicineByCategoryName(this.medicineCategory);
    }

    /**
     * @return the medicineCategory
     */
    public String getMedicineCategory() {
//        //System.out.println("Getting medcine category");
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
     * @return the patientAdvises
     */
    public List<DTOAdvise> getPatientAdvises() {
        return patientAdvises;
    }

    /**
     * @param patientAdvises the patientAdvises to set
     */
    public void setPatientAdvises(List<DTOAdvise> patientAdvises) {
        this.patientAdvises = patientAdvises;
    }

    public void addAdvise() {
        DTOAdvise advise = new DTOAdvise();
        advise.setMedicineId(Integer.parseInt(this.medicineId));
        try {
            advise.setMedicineName(medicineManager.getMedicineById(Integer.parseInt(this.medicineId)).getMedicineName());

        } catch (MedicineNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        advise.setMedicineDosage(medicineDosage);
        advise.setMedicineDays(medicineDays);
//        advise.setMedicineId(0);
        if (this.patientAdvises == null) {
            this.patientAdvises = new ArrayList();
        }
        this.patientAdvises.add(advise);
    }

    public void clearAdvise() {
        this.patientAdvises.clear();
    }

    private void reset() {
        patientType = true;
        patientName = "";
        patientAddress = "";
        patientPhone = "";
        patientAge = 30;
        patientBp = "";
        patientTemperature = 98;
        patientWeight = 70;
        patientPulse = 72;
        primaryDiagnosis = "";
        secondaryDiagnosis = "";
        tertiaryDiagnosis = "";
        patientGender = false;
        medicineDays = 5 + "";
        this.patientAdvises = new ArrayList();
        medicineCategories = new ArrayList();
        medicineList = new ArrayList();
        testsList = new ArrayList();
        testResultList = new ArrayList();
        patientList = new ArrayList();

    }

    public String addPrescription() {
        //System.out.println("Adding prescription...");
        DTOPatient patient = null;
        if (this.patientType) {
            patient = new DTOPatient(0, patientName, patientAddress, patientGender, patientAge, patientPhone, patientBp, (patientTemperature),
                    patientPulse, (patientWeight), primaryDiagnosis, secondaryDiagnosis, tertiaryDiagnosis, null, null, this.getLoginBean().getUserPhoneNumber());
        } else {
            //System.out.println("patient Id : " + patientId);
            patient = new DTOPatient(Integer.parseInt(getPatientId()), patientName, patientAddress, patientGender, patientAge, patientPhone, patientBp, patientTemperature,
                    patientPulse, patientWeight, primaryDiagnosis, secondaryDiagnosis, tertiaryDiagnosis, null, null, this.getLoginBean().getUserPhoneNumber());

        }

        try {
            if (patientAdvises != null && !patientAdvises.isEmpty()) {
                PatientVisitEntity visit = visitManager.addPrescription(patient, patientAdvises, testResultList, patientType);
                //System.out.println("Printing prescription");
                printPrescription(visit);
                reset();
            } else {
                this.addMessage("No Advises", "No medicine advised");
            }
        } catch (MedicineNotFoundException | InvalidPatientNameException | InvalidPatientAddressException
                | PatientNotFoundException | UserNotFoundException ex) {
            this.addMessage("New Prescription", ex.getMessage());
        }
        return "/AddPrescription.xhtml?faces-redirect=false";
    }

    private void printPrescription(PatientVisitEntity visit) {
        try {
            JasperReportDriver driver = new JasperReportDriver();
//            Connection con = null;
//            try {
//                InitialContext initContext = new InitialContext();
//                DataSource dataSource = (DataSource) initContext.lookup("PrescriptionDS");
//                con = dataSource.getConnection();
//            } catch (NamingException ex) {
//                Logger.getLogger(EditPrescriptionBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
            Class.forName("com.mysql.jdbc.Driver");
            ConfigEntity config = new ConfigEntity();

            Connection con = null;

            con = DriverManager.getConnection("jdbc:mysql://" + configManager.getConfigProperty("DB_HOST") + ":" + configManager.getConfigProperty("DB_PORT") + "/prescription?useSSL=false", configManager.getConfigProperty("DB_USER"), configManager.getConfigProperty("DB_PASSWORD"));

            PrescriptionReportParam param = new PrescriptionReportParam();
            param.getParametersMap().put("dr_name", loginBean.getUser().getUserName());
            param.getParametersMap().put("patient_visit_id", visit.getPatientVisitId());
            try {
                List<String> results = labTestManager.getEmptyLabTestByVisit(visit);
                for (String result : results) {
                    System.out.println("Result : " + result);

                }
                StringBuilder bldr = new StringBuilder();
                for (String result : results) {
                    bldr.append(result + "\r");
                }
                System.out.println("Exam Advvised : " + bldr.toString());
                param.getParametersMap().put("exam_advised", bldr.toString());
            } catch (LabTestResultNotFoundException ex) {
            }
            InputStream inStreamAdvises = null;
            InputStream inStreamTests = null;
            try {
                inStreamAdvises = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/reports/PrescriptionDetails.jasper");
                inStreamTests = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/reports/LabTestResults.jasper");
            } catch (Exception e) {
                System.out.println("Error Getting Jasper File InputStream :" + e.getMessage());
            }
            param.getParametersMap().put("subReport", inStreamAdvises);
            param.getParametersMap().put("subReportLabTest", inStreamTests);
            driver.printReport(param, con);
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
//    public void printPrescription(PatientVisitEntity visit) {
//        try {
//            // Database connection
//            Class.forName("com.mysql.jdbc.Driver");
//            ConfigEntity config = new ConfigEntity();
//
//            Connection con = DriverManager.getConnection(
//                    "jdbc:mysql://" + configManager.getConfigProperty("DB_HOST") + ":"
//                    + configManager.getConfigProperty("DB_PORT")
//                    + "/prescription?useSSL=false",
//                    configManager.getConfigProperty("DB_USER"),
//                    configManager.getConfigProperty("DB_PASSWORD")
//            );
//
//            // Report parameters
//            Map<String, Object> parameters = new HashMap<>();
//            parameters.put("dr_name", loginBean.getUser().getUserName());
//            parameters.put("patient_visit_id", visit.getPatientVisitId());
//
//            try {
//                List<String> results = labTestManager.getEmptyLabTestByVisit(visit);
//                StringBuilder bldr = new StringBuilder();
//                for (String result : results) {
//                    bldr.append(result).append("\r");
//                }
//                parameters.put("exam_advised", bldr.toString());
//            } catch (LabTestResultNotFoundException ex) {
//                Logger.getLogger(EditPrescriptionBean.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            // Load Jasper report
//            InputStream inStreamAdvises = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/reports/PrescriptionDetails.jasper");
//            InputStream inStreamTests = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/reports/LabTestResults.jasper");
//            parameters.put("subReport", inStreamAdvises);
//            parameters.put("subReportLabTest", inStreamTests);
//
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inStreamAdvises);
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);
//
//            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
//            showPDFInNewTab(pdfBytes);
////
////            JasperPrint a4JasperPrint = new JasperPrint();
////            a4JasperPrint.setName(jasperPrint.getName());
////            a4JasperPrint.setPageWidth(595);
////            a4JasperPrint.setPageHeight(842);
////
////            // Copy content from existing JasperPrint to the new one
////            for (int i = 0; i < jasperPrint.getPages().size(); i++) {
////                a4JasperPrint.addPage(jasperPrint.getPages().get(i));
////            }
////
////            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
////            session.setAttribute("jasperPrint", a4JasperPrint);
//            reset();
//        } catch (Exception ex) {
//            Logger.getLogger(EditPrescriptionBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    private void showPDFInNewTab(byte[] pdfBytes) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        // Set response headers
        externalContext.responseReset();
        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseContentLength(pdfBytes.length);
        externalContext.setResponseHeader("Content-Disposition", "inline; filename=\"report.pdf\"");

        // Write PDF content to response
        externalContext.getResponseOutputStream().write(pdfBytes);
        facesContext.responseComplete();
    }

    /**
     * @return the patientBp
     */
    public String getPatientBp() {
        return patientBp;
    }

    /**
     * @param patientBp the patientBp to set
     */
    public void setPatientBp(String patientBp) {
        this.patientBp = patientBp;
    }

    /**
     * @return the patientTemperature
     */
    public Integer getPatientTemperature() {
        return patientTemperature;
    }

    /**
     * @param patientTemperature the patientTemperature to set
     */
    public void setPatientTemperature(Integer patientTemperature) {
        this.patientTemperature = patientTemperature;
    }

    /**
     * @return the patientWeight
     */
    public Integer getPatientWeight() {
        return patientWeight;
    }

    /**
     * @param patientWeight the patientWeight to set
     */
    public void setPatientWeight(Integer patientWeight) {
        this.patientWeight = patientWeight;
    }

    /**
     * @return the patientPulse
     */
    public Integer getPatientPulse() {
        return patientPulse;
    }

    /**
     * @param patientPulse the patientPulse to set
     */
    public void setPatientPulse(Integer patientPulse) {
        this.patientPulse = patientPulse;
    }

    /**
     * @return the primaryDiagnosis
     */
    public String getPrimaryDiagnosis() {
        return primaryDiagnosis;
    }

    /**
     * @param primaryDiagnosis the primaryDiagnosis to set
     */
    public void setPrimaryDiagnosis(String primaryDiagnosis) {
        this.primaryDiagnosis = primaryDiagnosis;
    }

    /**
     * @return the secondaryDiagnosis
     */
    public String getSecondaryDiagnosis() {
        return secondaryDiagnosis;
    }

    /**
     * @param secondaryDiagnosis the secondaryDiagnosis to set
     */
    public void setSecondaryDiagnosis(String secondaryDiagnosis) {
        this.secondaryDiagnosis = secondaryDiagnosis;
    }

    /**
     * @return the tertiaryDiagnosis
     */
    public String getTertiaryDiagnosis() {
        return tertiaryDiagnosis;
    }

    /**
     * @param tertiaryDiagnosis the tertiaryDiagnosis to set
     */
    public void setTertiaryDiagnosis(String tertiaryDiagnosis) {
        this.tertiaryDiagnosis = tertiaryDiagnosis;
    }

    /**
     * @return the patientId
     */
    public String getPatientString() {
        return this.patientString;
    }

    /**
     * @param patientString the patientString to set
     */
    public void setPatientString(String patientString) {
        this.patientString = patientString;
        StringTokenizer tkn = new StringTokenizer(patientString, " ");
        this.patientId = tkn.nextToken();

        try {
            PatientEntity patient = this.patientManager.getPatientById(Integer.parseInt(this.patientId));
            this.patientName = patient.getPatientName();
            this.patientAddress = patient.getPatientAddress();
            this.patientGender = patient.getPatientGender();
            this.patientPhone = patient.getPatientPhoneNumber();

        } catch (PatientNotFoundException ex) {
            //System.out.println(ex.getMessage());
            Logger
                    .getLogger(AddPrescriptionBean.class
                            .getName()).log(Level.SEVERE, null, ex);
            this.patientName = "";
            this.patientAddress = "";
            this.patientGender = true;
            this.patientPhone = "";

        } catch (NullPointerException e) {

        }
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
            Logger.getLogger(AddPrescriptionBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the patientType
     */
    public boolean isPatientType() {
        return patientType;
    }

    /**
     * @param patientType the patientType to set
     */
    public void setPatientType(boolean patientType) {
        //System.out.println("Setting patient type  : " + patientType);
        this.patientType = patientType;
        if (this.patientType) {
            //System.out.println("Resetting for new patient");
//            this.patientName = "";
//            this.patientAddress = "";
//            this.patientPhone = "";
//            this.patientGender = true;
            this.reset();
        } else {
            try {
                PatientEntity patient = this.patientList.get(0);
                this.patientName = patient.getPatientName();
                this.patientAddress = patient.getPatientAddress();
                this.patientPhone = patient.getPatientPhoneNumber();
                this.patientGender = patient.getPatientGender();
            } catch (Exception e) {

            }

        }
    }

    /**
     * @return the patientList
     */
    public List<PatientEntity> getPatientList() {
        if (getLoginBean() == null) {
            System.out.println("Login bean is null");
        }
        try {
            this.patientList = patientManager.getAllPatients(getLoginBean().getUser().getUserId());

        } catch (PatientNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return this.patientList;
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

    public void deleteMedicine(DTOAdvise advise) {
        this.patientAdvises.remove(advise);
    }

    public void loadAdvise(List<AdviseEntity> advises) {
        this.patientAdvises = new ArrayList();
        for (AdviseEntity advise : advises) {

            DTOAdvise adviseDto = new DTOAdvise();
            adviseDto.setMedicineId(advise.getMedicineId().getMedicineId());
//            try {
            adviseDto.setMedicineName(advise.getMedicineId().getMedicineName());

//            } catch (MedicineNotFoundException ex) {
//                Logger.getLogger(AddPrescriptionBean.class
//                        .getName()).log(Level.SEVERE, null, ex);
//            }
            adviseDto.setMedicineDosage(advise.getDosage());
            adviseDto.setMedicineDays(advise.getDays());
//        advise.setMedicineId(0);
            this.patientAdvises.add(adviseDto);
        }
    }

    public void loadLastAdvise() {
//        System.out.println("listener called...");

        try {
            PatientVisitEntity visit = visitManager.getLastVisit(Integer.parseInt(this.getPatientId()));
            this.patientAge = visit.getPatientAge();
            this.patientWeight = visit.getPatientWeight().intValue();
            System.out.println("Patient Weight in load last advise : " + patientWeight);
            this.primaryDiagnosis = visit.getDiagnosis1();
            this.secondaryDiagnosis = visit.getDiagnosis2();
            this.tertiaryDiagnosis = visit.getDiagnosis3();

            List<AdviseEntity> advises = adviseManager.getLastAdvise(Integer.parseInt(this.getPatientId()));
            this.loadAdvise(advises);
            List<LabTestResultEntity> tests = visit.getLabTestResultEntityList();
            List<LabTestResultEntity> results = this.labTestManager.getLabTestResultsByVisit(visit);
//            System.out.println("Calling test "+ results.size());
            this.testResultList = new ArrayList<DTOTestResult>();
            for (LabTestResultEntity result : tests) {
//                System.out.println("Test : "+result.getTestId().getTestDescription());
                DTOTestResult test = new DTOTestResult();
                test.setTestDescripton(result.getTestId().getTestDescription());
                test.setTestUnit(result.getTestId().getUnits());
                test.setTestValue(result.getTestResultValue());
                testResultList.add(test);
            }

//            this.loadTests(tests);
        } catch (PatientVisitNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LabTestResultNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the loginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * @param loginBean the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    /**
     * @return the testDescription
     */
    public String getTestDescription() {
        return testDescription;
    }

    /**
     * @param testDescription the testDescription to set
     */
    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    /**
     * @return the testValue
     */
    public Float getTestValue() {
        return testValue;
    }

    /**
     * @param testValue the testValue to set
     */
    public void setTestValue(Float testValue) {
        this.testValue = testValue;
    }

    /**
     * @return the testUnit
     */
    public String getTestUnit() {
        return testUnit;
    }

    /**
     * @param testUnit the testUnit to set
     */
    public void setTestUnit(String testUnit) {
        this.testUnit = testUnit;
    }

    /**
     * @return the testId
     */
    public String getTestId() {
        return testId;
    }

    /**
     * @param testId the testId to set
     */
    public void setTestId(String testId) {
        this.testId = testId;
        try {
            LabTestEntity test = labTestManager.getLabTestById(Integer.parseInt(testId));
            this.setTestUnit(test.getUnits());
        } catch (LabTestNotFoundException ex) {
            addMessage("Not Found", ex.getMessage());
        }
    }

    /**
     * @return the testsList
     */
    public List<LabTestEntity> getTestsList() {
        return this.labTestManager.getAllLabTests();
    }

    /**
     * @param testsList the testsList to set
     */
    public void setTestsList(List<LabTestEntity> testsList) {
        this.testsList = testsList;
    }

    /**
     * @return the testResultList
     */
    public List<DTOTestResult> getTestResultList() {
        return testResultList;
    }

    /**
     * @param testResultList the testResultList to set
     */
    public void setTestResultList(List<DTOTestResult> testResultList) {
        this.testResultList = testResultList;
    }

    public void addTest() {
        System.out.println("Add test called...");
        DTOTestResult result = new DTOTestResult();
        result.setTestId(Integer.parseInt(this.testId));
        try {
            result.setTestDescripton(labTestManager.getLabTestById(Integer.parseInt(this.testId)).getTestDescription());

        } catch (LabTestNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        result.setTestValue(this.testValue);
        result.setTestUnit(this.testUnit);

//        advise.setMedicineId(0);
        if (this.testResultList == null) {
            this.testResultList = new ArrayList();
        }
        this.testResultList.add(result);
        this.testValue = 0f;
    }

    public List<PatientEntity> filterPatients(String qryString) {
        List<PatientEntity> patients = null;
        try {
            patients = this.patientManager.filterPatientsByIdNameAddress(qryString);
        } catch (PatientNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patients;
    }

    /**
     * @return the patientId
     */
    public String getPatientId() {
        return patientId;
    }

}
