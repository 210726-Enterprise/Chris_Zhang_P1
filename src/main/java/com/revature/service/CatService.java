package com.revature.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.Cat;
import com.revature.repo.ORM_DAO;
import com.revature.repo.ORM_DAOImplement;
import com.revature.util.Metamodel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class CatService extends ORMService{

    private Metamodel<?> metamodel = new Metamodel<>(Cat.class);
    private ObjectMapper mapper;

    public CatService(){
        super();
    }

    public CatService(ORM_DAO ormDAO, ObjectMapper mapper){
        super(ormDAO);
        this.mapper = mapper;
    }

    public void getAllCats(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(super.getObjects(metamodel));
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getOutputStream().print(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertCat(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            req.getReader().lines().collect(Collectors.toList()).forEach(stringBuilder::append);

            Cat cat = mapper.readValue(stringBuilder.toString(),Cat.class);
            boolean result = super.insertObject(cat);

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

    public void updateCat(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            req.getReader().lines().collect(Collectors.toList()).forEach(stringBuilder::append);

            Cat cat = mapper.readValue(stringBuilder.toString(),Cat.class);
            if(cat.getId() != 0){
                boolean result = super.updateObject(cat);
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

    public void deleteCat(HttpServletRequest req, HttpServletResponse resp) {
        int id = Integer.parseInt(req.getParameter("catId"));

        boolean result = super.deleteTableRow(id,metamodel);
        if(result){
            resp.setStatus(HttpServletResponse.SC_OK);
        }else{
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}