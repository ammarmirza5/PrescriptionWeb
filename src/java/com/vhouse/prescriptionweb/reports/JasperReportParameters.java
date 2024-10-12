package com.vhouse.prescriptionweb.reports;

import java.io.InputStream;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import javax.faces.context.FacesContext;
 
import javax.swing.ImageIcon;
 
public abstract class JasperReportParameters {
    protected Map parametersMap;
    protected List dataList;
    public JasperReportParameters(){
        parametersMap=new HashMap();
    }
    public void setParametersMap(Map parametersMap) {
        this.parametersMap = parametersMap;
    }
    public Map getParametersMap() {
        return parametersMap;
    }
 
    public void setDataList(List dataList) {
        this.dataList = dataList;
    }
 
    public List getDataList() {
        return dataList;
    }
 
    public abstract String getJasperFilePath();
 
    public InputStream getJasperFileStream() {
        System.out.println("Jasper File Path : "+this.getJasperFilePath());
        InputStream inStream=null;
        try{
            inStream=FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(this.getJasperFilePath());
        }catch(Exception e){
            System.out.println("Error Getting Jasper File InputStream :"+e.getMessage());
        }
        return inStream;
    }
} 