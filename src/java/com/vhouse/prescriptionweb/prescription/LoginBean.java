/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.entity.UserEntity;
import com.vhouse.prescription.sms.InvalidPhoneNumberException;
import com.vhouse.prescription.user.UserManagerRemote;
import com.vhouse.prescription.user.UserNotFoundException;
import java.io.Serializable;
import java.util.Random;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author arahman
 */
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable{

    @EJB
    UserManagerRemote userManager;
    private String subscriberPhoneNumber;
    private String subscriberPassword;
    private String userPhoneNumber;
    private String userPassword;
    private UserEntity user;
    private int serviceId;

    public String authenticateUser() {
        System.out.println("Calling authenticate user");
        try {
            UserEntity u = userManager.getUserByPhoneNumber(this.userPhoneNumber);
            if (this.userPassword.equals(u.getUserPassword())) {
                this.setUser(u);
                System.out.println("Authenticated");
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage("Wrong password."));
                return null;
            }
        } catch (InvalidPhoneNumberException | UserNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(ex.getMessage()));
            return null;
        }
        System.out.println("Redirecting to Dashboard");
        return "AddPrescription.xhtml?faces-redirect=true";
    }

    public void generatePassword() {
        try {
            System.out.println("Generating password");
            this.generatePassword(this.subscriberPhoneNumber);
        } catch (UserNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage("You are not registered. Please register first"));
        }
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Your password is sent via SMS. Please enter password to login."));
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "Login.xhtml?faces-redirect=true";
    }

    /**
     * @return the subscriberPhoneNumber
     */
    public String getSubscriberPhoneNumber() {
        return subscriberPhoneNumber;
    }

    /**
     * @param subscriberPhoneNumber the subscriberPhoneNumber to set
     */
    public void setSubscriberPhoneNumber(String subscriberPhoneNumber) {
        this.subscriberPhoneNumber = subscriberPhoneNumber;
    }

    /**
     * @return the subscriberPassword
     */
    public String getSubscriberPassword() {
        return subscriberPassword;
    }

    /**
     * @param subscriberPassword the subscriberPassword to set
     */
    public void setSubscriberPassword(String subscriberPassword) {
        this.subscriberPassword = subscriberPassword;
    }

    public String generatePassword(String phoneNumber) throws UserNotFoundException {
        Random random = new Random();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int rnd = random.nextInt(128);
            while (!(rnd >= 48 && rnd <= 57) && !(rnd >= 64 && rnd <= 90) && !(rnd >= 97 && rnd <= 122)) {
                rnd = random.nextInt(128);
                System.out.println("rnd " + rnd);
            }
            System.out.println("rnd " + rnd);
            sb.append((char) rnd);
        }
        return sb.toString();
    }

    public String generate4DigitPassword() {
        Random random = new Random();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int rnd = random.nextInt(128);
            while (!(rnd >= 48 && rnd <= 57)) {
                rnd = random.nextInt(128);
                System.out.println("rnd " + rnd);
            }
            System.out.println("rnd " + rnd);
            sb.append((char) rnd);
        }
        System.out.println("Geerated Password : " + sb.toString());
        return sb.toString();
    }

    /**
     * @return the serviceId
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId the serviceId to set
     */
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return the userPhoneNumber
     */
    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    /**
     * @param userPhoneNumber the userPhoneNumber to set
     */
    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    /**
     * @return the userPassword
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * @param userPassword the userPassword to set
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * @return the user
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

}
