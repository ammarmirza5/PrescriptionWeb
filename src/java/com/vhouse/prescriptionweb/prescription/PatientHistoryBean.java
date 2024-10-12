/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.advise.DTOAdvise;
import com.vhouse.prescription.config.ConfigManagerRemote;
import com.vhouse.prescription.config.PropertyNotAvailbleException;
import com.vhouse.prescription.entity.ConfigEntity;
import com.vhouse.prescription.medicine.MedicineManagerLocal;
import com.vhouse.prescription.entity.MedicineCategoryEntity;
import com.vhouse.prescription.entity.PatientEntity;
import com.vhouse.prescription.entity.PatientVisitEntity;
import com.vhouse.prescription.patient.PatientManagerLocal;
import com.vhouse.prescription.patient.PatientNotFoundException;
import com.vhouse.prescription.user.UserNotFoundException;
import com.vhouse.prescription.visit.PatientVisitNotFoundException;
import com.vhouse.prescription.visit.VisitManagerLocal;
import com.vhouse.prescriptionweb.reports.JasperReportDriver;
import com.vhouse.prescriptionweb.reports.PrescriptionReportParam;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author arahman
 */
@ManagedBean
@ViewScoped
public class PatientHistoryBean implements Serializable {

    @EJB
    private PatientManagerLocal patientManager;
    @EJB
    private MedicineManagerLocal medicineManager;
    @EJB
    private VisitManagerLocal visitManager;
    @EJB
    private ConfigManagerRemote configManager;
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    private String patientId = "24";
    private String patientName;
    private String patientAddress;
    private String patientPhone;
    private Integer patientAge;
    private String patientBp;
    private Integer patientTemperature;
    private Float patientWeight;
    private Integer patientPulse;
    private String primaryDiagnosis;
    private String secondaryDiagnosis;
    private String tertiaryDiagnosis;
    private boolean patientGender = true;
    private List<PatientEntity> patientList;
    private List<PatientVisitEntity> patientVisits;
    private List<DTOAdvise> patientAdvises;

    public PatientHistoryBean() {

    }

    @PostConstruct
    public void init() {
        try {
            this.patientVisits = visitManager.getPatientVisits(patientManager.getPatientById(24));
        } catch (PatientVisitNotFoundException ex) {
            Logger.getLogger(PatientHistoryBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PatientNotFoundException ex) {
            Logger.getLogger(PatientHistoryBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the patientList
     */
    public List<PatientEntity> getPatientList() {

        try {
            this.patientList = patientManager.getAllActivePatients(getLoginBean().getUser().getUserId());

        } catch (PatientNotFoundException ex) {
            Logger.getLogger(AddPrescriptionBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return this.patientList;
    }

    private void addMessage(String msg, String details) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg, details));

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
    public List<MedicineCategoryEntity> getMedicineCategories() {
        List<MedicineCategoryEntity> categories = this.medicineManager.getAllMedicineCategories();
//        MedicineCategoryEntity all = new MedicineCategoryEntity();
//        all.setCategoryDescription("All Medicine");
//        all.setCategoryId(0);
//        categories.add(0, all);
        return categories;
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

    private void reset() {
        patientName = "";
        patientAddress = "";
        patientPhone = "";
        patientAge = 30;
        patientBp = "";
        patientTemperature = 98;
        patientWeight = 80f;
        patientPulse = 80;
        primaryDiagnosis = "";
        secondaryDiagnosis = "";
        tertiaryDiagnosis = "";
        patientGender = true;
        this.patientAdvises = new ArrayList();
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
            param.getParametersMap().put("patient_visit_id", visit.getPatientVisitId());
            param.getParametersMap().put("subReport", "/reports/PrescriptionDeetails.jasper");
            driver.printReport(param, con);
        } catch (SQLException ex) {
            System.out.println("Error getting connection : " + ex.getMessage());
            Logger
                    .getLogger(PatientHistoryBean.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PatientHistoryBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyNotAvailbleException ex) {
            Logger.getLogger(PatientHistoryBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    public Float getPatientWeight() {
        return patientWeight;
    }

    /**
     * @param patientWeight the patientWeight to set
     */
    public void setPatientWeight(Float patientWeight) {
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
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(String patientId) {

        this.patientId = patientId;
        try {
            PatientEntity patient = this.patientManager.getPatientById(Integer.parseInt(this.patientId));
            this.patientName = patient.getPatientName();
            this.patientAddress = patient.getPatientAddress();
            this.patientGender = patient.getPatientGender();
            this.patientPhone = patient.getPatientPhoneNumber();
            try {
                this.patientVisits = visitManager.getPatientVisits(patient);
            } catch (PatientVisitNotFoundException ex) {
                this.addMessage("Error Getting Visits", ex.getMessage());
                Logger.getLogger(PatientHistoryBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (PatientNotFoundException ex) {
            System.out.println(ex.getMessage());
            Logger
                    .getLogger(PatientHistoryBean.class
                            .getName()).log(Level.SEVERE, null, ex);
            this.patientName = "";
            this.patientAddress = "";
            this.patientGender = true;
            this.patientPhone = "";
        } catch (NullPointerException e) {

        }
    }

    /**
     * @return the patientVisits
     */
    public List<PatientVisitEntity> getPatientVisits() {
        if (patientVisits != null) {
            System.out.println("returning " + this.patientVisits.size() + " from backing bean");
        }
        return patientVisits;
    }

    /**
     * @param patientVisits the patientVisits to set
     */
    public void setPatientVisits(List<PatientVisitEntity> patientVisits) {
        this.patientVisits = patientVisits;
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

}
