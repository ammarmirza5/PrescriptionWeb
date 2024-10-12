package com.vhouse.prescriptionweb.reports;


public class MonthlyBillReportParam extends JasperReportParameters {

	@Override
	public String getJasperFilePath() {
		return "/reports/MonthlyBill.jasper";
	}
	public MonthlyBillReportParam(){
		super();
	}
}
