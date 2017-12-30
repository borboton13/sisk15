package com.encens.khipus.controller;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Diego on 28/05/2015.
 */
@WebServlet("/pdf")
public class PdfServlet extends HttpServlet {

    @Inject
    PedidosReportController pedidosReportController;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String foo = request.getParameter("foo");
        String bar = request.getParameter("bar");
        // ...

        // Now just use the same code as in your original bean *without* FacesContext.
        // Note that the HttpServletResponse is readily available as method argument!
        response.setContentType("application/pdf");
        // ...
    }

}