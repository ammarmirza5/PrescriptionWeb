package com.vhouse.prescriptionweb.reports;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import java.io.InputStream;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
//import org.apache.commons.io.output.NullOutputStream;


public class JasperReportDriver {

    public JasperReportDriver() {

    }

    public void printReport(JasperReportParameters params) {
        System.out.println("Printing Report : " + params.getJasperFilePath());
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    params.getJasperFileStream(), params.getParametersMap(),
                    new JRBeanCollectionDataSource(params.getDataList()));

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context
                    .getExternalContext().getResponse();
            String fileName = params.getJasperFilePath();
            fileName = fileName.substring(fileName.lastIndexOf('/'), fileName.indexOf('.')) + ".pdf";

            response.setHeader("Content-disposition",
                    "attachment;filename=" + fileName + ";");
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.flushBuffer();
            context.responseComplete();
            System.out.println("report printed");
        } catch (Exception ex) {
            String connectMsg = "Could not create the report "
                    + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        }
    }

    public void printReportToExcel(JasperReportParameters params, Connection con) {
        printReportToExcel(params, con, true);

    }

    public void printReportToExcel(JasperReportParameters params, Connection con, boolean checkType) {
        System.out.println("Printing Excel Report............"
                + params.getJasperFilePath());
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    params.getJasperFileStream(), params.getParametersMap(), con);
            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            JRXlsExporter exporter = new JRXlsExporter();
//			JExcelApiExporter exporter=new JExcelApiExporter();
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context
                    .getExternalContext().getResponse();
            String fileName = params.getJasperFilePath();
            fileName = fileName.substring(fileName.lastIndexOf('/'), fileName.indexOf('.')) + ".xls";
            OutputStream outputStream = response.getOutputStream();
            exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, checkType);
            exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.FALSE);
//			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
//					"c://meter_reading.xls");
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition",
                    "attachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
            exporter.exportReport();
            response.flushBuffer();
            context.responseComplete();

        } catch (Exception ex) {
            String connectMsg = "Could not create the report "
                    + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        }
    }

    public void printReport(JasperReportParameters params, Connection con) {
        System.out.println("Printing Report : " + params.getJasperFilePath());
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    params.getJasperFileStream(), params.getParametersMap(), con);
//            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
//            InputStream istream = new ByteArrayInputStream(bytes);
//            printPdfToPrinter(istream);
            printPdfToPrinter(jasperPrint);
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpServletResponse response = (HttpServletResponse) context
//                    .getExternalContext().getResponse();
//
//            response.setHeader("Content-disposition",
//                    "attachment;filename=Prescription.pdf;");
//            response.setContentType("application/pdf");
//            response.setContentLength(bytes.length);
//            response.getOutputStream().write(bytes);
//            response.flushBuffer();
//            context.responseComplete();
//            System.out.println("report printed");
        } catch (Exception ex) {
            String connectMsg = "Could not create the report "
                    + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        }
    }

    public void printPOSReport(JasperReportParameters params, Connection con) {
        System.out.println("Printing Report : " + params.getJasperFilePath());
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    params.getJasperFileStream(), params.getParametersMap(), con);
//            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
//            InputStream istream = new ByteArrayInputStream(bytes);
//            printPdfToPrinter(istream);
            printPOSInvoice(jasperPrint);
//            FacesContext context = FacesContext.getCurrentInstance();
//            HttpServletResponse response = (HttpServletResponse) context
//                    .getExternalContext().getResponse();
//
//            response.setHeader("Content-disposition",
//                    "attachment;filename=Prescription.pdf;");
//            response.setContentType("application/pdf");
//            response.setContentLength(bytes.length);
//            response.getOutputStream().write(bytes);
//            response.flushBuffer();
//            context.responseComplete();
//            System.out.println("report printed");
        } catch (Exception ex) {
            String connectMsg = "Could not create the report "
                    + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        }
    }

    public void printReport(JasperReportParameters params, String reportTitle) {
        System.out.println("Printing Report with Title : "
                + params.getJasperFilePath());
        System.out.println("Report Title : " + reportTitle);

        try {

            System.out.println("Before fillReport Size DataList..............."
                    + params.getDataList().size());
            System.out
                    .println("Before fillReport size ParametersMap()..............."
                            + params.getParametersMap().size());

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    params.getJasperFileStream(), params.getParametersMap(),
                    new JRBeanCollectionDataSource(params.getDataList()));
            System.out.println("After fillReport...........");
            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context
                    .getExternalContext().getResponse();
            response.setHeader("Content-disposition", "attachment; filename="
                    + reportTitle);
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.flushBuffer();
            context.responseComplete();
            System.out.println("report printed");
        } catch (Exception ex) {
            String connectMsg = "Could not create the report "
                    + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        }
    }

    public void printReportToExcel(JasperReportParameters params) {
        System.out.println("Printing Excel Report............"
                + params.getJasperFilePath());
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    params.getJasperFileStream(), params.getParametersMap(),
                    new JRBeanCollectionDataSource(params.getDataList()));
            JRXlsExporter exporter = new JRXlsExporter();
//			JExcelApiExporter exporter=new JExcelApiExporter();
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context
                    .getExternalContext().getResponse();
            String fileName = params.getJasperFilePath();
            fileName = fileName.substring(fileName.lastIndexOf('/'), fileName.indexOf('.')) + ".xls";
            OutputStream outputStream = response.getOutputStream();
            exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, true);
            exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.FALSE);
//			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
//					"c://meter_reading.xls");
            response.setHeader("Content-disposition",
                    "attachment;filename=" + fileName);
            response.setContentType("application/vnd.ms-excel");
//			response.setContentLength(bytes.length);
//			response.getOutputStream().write(bytes);
            exporter.exportReport();
            response.flushBuffer();
            context.responseComplete();

        } catch (Exception ex) {
            String connectMsg = "Could not create the report "
                    + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param params
     */
    public void saveReportToExcel(JasperReportParameters params) {
        System.out.println("Printing Excel Report............"
                + params.getJasperFilePath());
        File rep = new File("testfile.xls");
        System.out.println("File name " + rep.getAbsolutePath());
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(params.getJasperFilePath());
//            JasperReport jasperReport = (JasperReport) JasperCompileManager.compileReport(is);
//            jasperPrint = JasperFillManager.fillReport(jasperReport, null, con);
//...
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    is, params.getParametersMap(),
                    new JRBeanCollectionDataSource(params.getDataList()));
            JRXlsExporter exporter = new JRXlsExporter();
//			JExcelApiExporter exporter=new JExcelApiExporter();
//            FacesContext context = FacesContext.getCurrentInstance();
//			HttpServletResponse response = (HttpServletResponse) context
//					.getExternalContext().getResponse();
            String fileName = params.getJasperFilePath();
            fileName = fileName.substring(fileName.lastIndexOf('/'), fileName.indexOf('.')) + ".xls";
//			OutputStream outputStream=response.getOutputStream();
            exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
//			exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM,  outputStream);
            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, true);
            exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.FALSE);
//			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,
//					"c://meter_reading.xls");
//			response.setHeader("Content-disposition",
//					"attachment;filename="+fileName);
//			response.setContentType("application/vnd.ms-excel");
//			response.setContentLength(bytes.length);
//			response.getOutputStream().write(bytes);
//			exporter.exportReport();
//			response.flushBuffer();
//			context.responseComplete();
            File report = new File("d:\\MessBill.xls");
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE, report);
            exporter.exportReport();

        } catch (Exception ex) {
            String connectMsg = "Could not create the report "
                    + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception exp) {
            }
        }

    }

    public void printBusinessCase(List<JasperReportParameters> params) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) context
                    .getExternalContext().getResponse();
            response.setContentType("application/pdf");
            List<JasperPrint> printList = new ArrayList<JasperPrint>();
            for (JasperReportParameters param : params) {
                System.out.println("Printing Report............"
                        + param.getJasperFilePath() + ", Class: "
                        + param.getClass());

                JasperPrint jasperPrint = JasperFillManager.fillReport(
                        param.getJasperFileStream(), param.getParametersMap(),
                        new JRBeanCollectionDataSource(param.getDataList()));
                printList.add(jasperPrint);
            }

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST,
                    printList);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,
                    response.getOutputStream());
            exporter.exportReport();
            response.flushBuffer();
            context.responseComplete();
        } catch (Exception ex) {
            System.out.println("Error printing business case "
                    + ex.getMessage());
            String connectMsg = "Could not create the report "
                    + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        }
    }
    private static boolean jobRunning = true;

    public void printPdfToPrinter(InputStream is) throws Exception {
//        InputStream is = new BufferedInputStream(new FileInputStream("myfile.pdf"));

        DocFlavor flavor = DocFlavor.INPUT_STREAM.PDF;
        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob printJob = service.createPrintJob();
        printJob.addPrintJobListener(new JobCompleteMonitor());
        Doc doc = new SimpleDoc(is, flavor, null);
//        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
//        attributes.add(new Destination(new java.net.URI("file:C:/myfile.ps")));
        printJob.print(doc, null);
//        while (jobRunning) {
//            Thread.sleep(1000);
//        }
//        System.out.println("Exiting app");
        is.close();
    }

    public void printPdfToPrinter(JasperPrint jasperPrint) throws Exception {
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
//        DocPrintJob printJob = service.createPrintJob();
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintService(printService);
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
        printRequestAttributeSet.add(new Copies(1));

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printService.getAttributes());
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        exporter.exportReport();
//        JasperPrintManager.printReport(jasperPrint, false);
    }

    public void printPOSInvoice(JasperPrint jasperPrint) throws Exception {

        // Define the custom size in mm and convert to inches
        float widthInMM = 80;
        float heightInMM = 200;
        float widthInInches = widthInMM / 25.4f;
        float heightInInches = heightInMM / 25.4f;

        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
//        DocPrintJob printJob = service.createPrintJob();
//        PrinterJob printJob = PrinterJob.getPrinterJob();
//        printJob.setPrintService(printService);
//        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
//        printRequestAttributeSet.add(MediaSizeName.INVOICE);
//        printRequestAttributeSet.add(new Copies(1));
//
//        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
//        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
//        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, printService);
//        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printService.getAttributes());
//        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
//        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
//        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
//        exporter.exportReport();
//        JasperPrintManager.printReport(jasperPrint, false);
       PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 80, 297, MediaPrintableArea.MM));
            printRequestAttributeSet.add(OrientationRequested.PORTRAIT);

                JRPrintServiceExporter exporter = new JRPrintServiceExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                
                SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
                configuration.setPrintRequestAttributeSet(printRequestAttributeSet);
                configuration.setPrintService(printService);
                configuration.setDisplayPageDialog(false);
                configuration.setDisplayPrintDialog(false);

                exporter.setConfiguration(configuration);
                
                exporter.exportReport();
    }

    private static class JobCompleteMonitor extends PrintJobAdapter {

        @Override
        public void printJobCompleted(PrintJobEvent jobEvent) {
            System.out.println("Job completed");
            jobRunning = false;
        }
    }
}
