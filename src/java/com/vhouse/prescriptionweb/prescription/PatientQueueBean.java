/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.advise.DTOPatient;
import com.vhouse.prescription.config.ConfigManagerRemote;
import com.vhouse.prescription.config.PropertyNotAvailbleException;
import com.vhouse.prescription.entity.PatientQueueEntity;
import com.vhouse.prescription.patient.InvalidQueuePatientStatusException;
import com.vhouse.prescription.patient.PatientQueueManager;
import com.vhouse.prescription.patient.PatientQueueManagerRemote;
import com.vhouse.prescription.patient.QueuePatientNotFoundException;
import com.vhouse.prescription.sms.InvalidPhoneNumberException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.management.ServiceNotFoundException;

/**
 *
 * @author arahman
 */
@ManagedBean
@ViewScoped
public class PatientQueueBean implements Serializable {

    @EJB
    private PatientQueueManagerRemote queueManager;
    @EJB
    private ConfigManagerRemote configManager;

    private String patientName;
    private String patientAddress;
    private String patientPhone;
    private boolean patientGender;
    private int patientAge;
    private String patientBp;
    private int patientPulse;
    private Float patientWeight;
    private int patientTemperature;
    private int subscriptionId;
    private PatientQueueEntity queuePatient;
    private List<PatientQueueEntity> subscriptions;

    public List<PatientQueueEntity> getSubscriptions() {
        try {
            System.out.println("Getting subscriptions from queueManager");
            this.subscriptions = queueManager.getAllQueuePatientListByDate(new Date());
            System.out.println("Got : " + subscriptions.size() + " subscriptions");
        } catch (QueuePatientNotFoundException e) {
            this.subscriptions = new ArrayList();
            Logger.getLogger(PatientQueueBean.class.getName()).log(Level.SEVERE, null, e);
        }
        return this.subscriptions;
    }

    public List<PatientQueueEntity> getSubscriptionsByStatus() {
        try {
            System.out.println("Getting subscriptions from queueManager");
            this.subscriptions = queueManager.getQueuePatientListByDate(new Date());
            System.out.println("Got : " + subscriptions.size() + " subscriptions");
        } catch (QueuePatientNotFoundException e) {
            this.subscriptions = new ArrayList();
            Logger.getLogger(PatientQueueBean.class.getName()).log(Level.SEVERE, null, e);
        }
        return this.subscriptions;
    }

    @PostConstruct
    public void init() {
//        this.getSubscriptions();
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
     * @return the serviceId
     */
    public int getServiceId() throws PropertyNotAvailbleException {

        return Integer.parseInt(this.configManager.getConfigProperty("SERVICE_ID"));
    }

    /**
     * @return the subscriptionId
     */
    public int getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * @param subscriptionId the subscriptionId to set
     */
    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void servedBtnActionListener(PatientQueueEntity patient) {
        try {
            queueManager.servePatient(patient.getPatientQueueId());
        } catch (QueuePatientNotFoundException ex) {
            Logger.getLogger(PatientQueueBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void skipBtnActionListener(PatientQueueEntity patient) {
        try {
            queueManager.setQueuePatientStatus(patient.getPatientQueueId(), PatientQueueEntity.SUBSCRIPTION_STATUS_SKIPPED);
        } catch (QueuePatientNotFoundException ex) {
            Logger.getLogger(PatientQueueBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidQueuePatientStatusException ex) {
            Logger.getLogger(PatientQueueBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void cancelBtnActionListener(PatientQueueEntity patient) {
        try {
            queueManager.setQueuePatientStatus(patient.getPatientQueueId(), PatientQueueEntity.SUBSCRIPTION_STATUS_CANCELLED);
        } catch (QueuePatientNotFoundException ex) {
            Logger.getLogger(PatientQueueBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidQueuePatientStatusException ex) {
            Logger.getLogger(PatientQueueBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());

        }

    }

    public void unSkipBtnActionListener(PatientQueueEntity patient) {
        try {
            queueManager.setQueuePatientStatus(patient.getPatientQueueId(), PatientQueueEntity.SUBSCRIPTION_STATUS_WAITING);
        } catch (QueuePatientNotFoundException | InvalidQueuePatientStatusException ex) {
            Logger.getLogger(PatientQueueBean.class.getName()).log(Level.SEVERE, null, ex);
        }

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
    public int getPatientAge() {
        return patientAge;
    }

    /**
     * @param patientAge the patientAge to set
     */
    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
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
     * @return the patientPulse
     */
    public int getPatientPulse() {
        return patientPulse;
    }

    /**
     * @param patientPulse the patientPulse to set
     */
    public void setPatientPulse(int patientPulse) {
        this.patientPulse = patientPulse;
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
     * @return the queuePatient
     */
    public PatientQueueEntity getQueuePatient() {
        return queuePatient;
    }

    /**
     * @param queuePatient the queuePatient to set
     */
    public void setQueuePatient(PatientQueueEntity queuePatient) {
        this.queuePatient = queuePatient;
    }

    /**
     * @param subscriptions the subscriptions to set
     */
    public void setSubscriptions(List<PatientQueueEntity> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String addSubscriber() {
        try {
            System.out.println("Adding subscriber in bean " + patientName);
//            SubscriberEntity subscriber = subscriberManager.addNewSubscriber(patientName, phoneNumber, address);
            DTOPatient dtoPatient = new DTOPatient(0, patientName, patientAddress, isPatientGender(), patientAge, patientPhone, patientBp, patientTemperature, patientPulse, patientWeight.intValue(), "", "", "", "", "", "");

            queueManager.addPatientInQueue(dtoPatient);

            FacesContext.getCurrentInstance().addMessage("Prescription",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Subscriber Added", "Subscriber Added"));
            this.patientName = "";
            this.patientPhone = "";
            this.patientAddress = "";
            this.patientAge = 0;
            this.patientWeight = 0f;
//            switchTimings();
        } catch (InvalidPhoneNumberException ex) {
            System.out.println(ex.getMessage());
            FacesContext.getCurrentInstance().addMessage("Prescription", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
        }
        return null;
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
}
