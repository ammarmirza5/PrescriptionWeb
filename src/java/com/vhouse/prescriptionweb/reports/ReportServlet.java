/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vhouse.prescriptionweb.reports;

/**
 *
 * @author ammar
 */
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/reports/pdf")
public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            JasperPrint jasperPrint = (JasperPrint) session.getAttribute("jasperPrint");
            if (jasperPrint == null) {
                throw new ServletException("No JasperPrint object found in session.");
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

            byte[] pdfData = byteArrayOutputStream.toByteArray();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=report.pdf");
            response.setContentLength(pdfData.length);

            OutputStream outputStream = response.getOutputStream();
            outputStream.write(pdfData);
            outputStream.flush();
        } catch (Exception e) {
            throw new ServletException("Error generating PDF report", e);
        }
    }
}
