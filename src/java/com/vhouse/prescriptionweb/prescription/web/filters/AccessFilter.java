/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vhouse.prescriptionweb.prescription.web.filters;

import com.vhouse.prescriptionweb.prescription.LoginBean;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author arahman
 */
@WebFilter({"/AddPrescription.xhtml","/EditMedicine.xhtml","/EditPrescription.xhtml","/Reports.xhtml"})
public class AccessFilter implements Filter {

    LoginBean loginBean;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        loginBean=(LoginBean) req.getSession().getAttribute("loginBean");
        if (loginBean!=null && loginBean.getUser()!= null) {
            // User is logged in, so just continue request.
            chain.doFilter(request, response);
        } else {
            // User is not logged in, so redirect to index.
            HttpServletResponse res = (HttpServletResponse) response;
            System.out.println("Redirecting to : "+req.getContextPath() + "/Login.xhtml");
            res.sendRedirect(req.getContextPath() + "/Login.xhtml");
        }
    }

    // You need to override init() and destroy() as well, but they can be kept empty.
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
        
    }
}
