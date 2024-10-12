package com.vhouse.prescriptionweb.reports;


public class PrescriptionReportParam extends JasperReportParameters {

	@Override
	public String getJasperFilePath() {
		return "/reports/Prescription.jasper";
	}
	public PrescriptionReportParam(){
		super();
	}
}
