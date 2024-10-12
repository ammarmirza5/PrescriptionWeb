package com.vhouse.prescriptionweb.reports;


public class SaleReportParam extends JasperReportParameters {

	@Override
	public String getJasperFilePath() {
		return "/reports/sale_report.jasper";
	}
	public SaleReportParam(){
		super();
	}
}
