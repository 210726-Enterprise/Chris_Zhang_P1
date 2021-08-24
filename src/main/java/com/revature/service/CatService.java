package com.revature.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.Cat;
import com.revature.repo.ORM_DAO;
import com.revature.util.Metamodel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class CatService{

    private Metamodel<?> metamodel = new Metamodel<>(Cat.class);
    private ObjectMapper mapper;
    ORMService service;

    public CatService(){
        service = new ORMService();
        mapper = new ObjectMapper();
    }

    /**
     * Takes in a request from the Servlet to get all Cat Objects from table and submits them
     * in json format into the response, then returns a response code based on task completion.
     * @param req
     * @param resp
     */
    public void getAllCats(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(service.getObjects(metamodel));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getOutputStream().print(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes in a request from the Servlet to insert a Cat Object into table and returns a response code
     * based on task completion.
     * @param req
     * @param resp
     */
    public void insertCat(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            req.getReader().lines().collect(Collectors.toList()).forEach(stringBuilder::append);

            Cat cat = mapper.readValue(stringBuilder.toString(),Cat.class);
            boolean result = service.insertObject(cat);

            if(result){
                resp.setStatus(HttpServletResponse.SC_CREATED);
            }else{
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                //Fool... you would stoke the fires of conflict?
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes in a request from the Servlet to update a Cat Object in table and returns a response code
     * based on task completion.
     * @param req
     * @param resp
     */
    public void updateCat(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            req.getReader().lines().collect(Collectors.toList()).forEach(stringBuilder::append);

            Cat cat = mapper.readValue(stringBuilder.toString(),Cat.class);
            if(cat.getId() != 0){
                boolean result = service.updateObject(cat);
                if(result){
                    resp.setStatus(HttpServletResponse.SC_OK);

                    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cat);
                    resp.getWriter().print(json);
                }else{
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes in a request from the Servlet to remove a Cat Object in table and returns a response code
     * based on task completion
     * @param req
     * @param resp
     */
    public void deleteCat(HttpServletRequest req, HttpServletResponse resp) {
        int id = Integer.parseInt(req.getParameter("catId"));

        boolean result = service.deleteTableRow(id,metamodel);
        if(result){
            resp.setStatus(HttpServletResponse.SC_OK);
        }else{
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
