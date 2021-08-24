package com.revature.servlet;

import com.revature.service.CatService;
import com.revature.service.ORMService;
import com.sun.org.apache.xpath.internal.operations.Or;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/cats")
public class CatServlet extends HttpServlet {

    CatService service;

    /**
     * Default constructor
     */
    public CatServlet(){
        service = new CatService();
    }

    /**
     * Constructor taking in a mock service object intended for testing purposes
     * @param service
     */
    public CatServlet(CatService service){
        this.service = service;
    }

    /**
     * Queries the service for all Cat Objects in the database
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.getAllCats(req,resp);
    }

    /**
     * Sends a Cat Object in json format for the service to parse and then insert into the database
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.insertCat(req,resp);
    }

    /**
     * Sends an updated version of a Cat Object in json format for the service to parse and then update
     * within the database
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.updateCat(req,resp);
    }

    /**
     * Sends an int parameter of a primary key of a Cat Object within the database and queries the service
     * to remove it from the table.
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.deleteCat(req,resp);
    }
}
