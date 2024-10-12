/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vhouse.prescriptionweb.prescription;

import com.vhouse.prescription.entity.PatientEntity;
import com.vhouse.prescription.patient.PatientManagerLocal;
import com.vhouse.prescription.patient.PatientNotFoundException;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

/**
 *
 * @author arahman
 */
@Named
@FacesConverter(value = "patientConverter")

public class PatientConverter implements Converter {

    @EJB
    private PatientManagerLocal patientManager;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("Get as Object called in converter : "+value);
        PatientEntity patient=null;
        try {
            patient=patientManager.getPatientById(Integer.parseInt(value));
        } catch (NumberFormatException ne) {
            System.out.println("Number format exception in PatientConverter : " + ne.getMessage());
        } catch (PatientNotFoundException ex) {
            System.out.println("Error in PatientConverter : " + ex.getMessage());
        }
        return patient;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println("GetAsString called in converter : "+((PatientEntity)value).toString());
        return ((PatientEntity)value).toString();
    }

}
