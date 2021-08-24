package com.revature.service;

import com.revature.repo.ORM_DAO;
import com.revature.repo.ORM_DAOImplement;
import com.revature.util.ColumnField;
import com.revature.util.Metamodel;
import com.revature.util.PrimaryField;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ORMService {
    protected ORM_DAO ormDAO;

    public ORMService(){
        ormDAO = new ORM_DAOImplement();
    }

    public ORMService(ORM_DAO ormDAO){
        this.ormDAO = ormDAO;
    }

    public boolean createEntityTable(Metamodel<?> metamodel){
        List<ColumnField> columns = metamodel.getColumns();
        ArrayList<String> typedColumns = convertColumnTypes(columns);
        if(ormDAO.createTable(metamodel,typedColumns)){
            return true;
        }
        return false;
    }

    public boolean insertObject(Object obj){
        Metamodel<?> metamodel = new Metamodel<>(obj.getClass());
        if(ormDAO.insertObjectIntoTable(obj,metamodel)){
            return true;
        }
        return false;
    }

    public Object getObject(int id,Metamodel<?> metamodel){
        PrimaryField primary = metamodel.getPrimaryKey();
        List<ColumnField> columnFields = metamodel.getColumns();
        List<Method> methods = metamodel.getSetterMethods();

        ArrayList<Method> sortedMethods = sortMethodsByFields(primary,columnFields,methods);

        Optional<Object> result = ormDAO.getObjectFromTable(id,metamodel,sortedMethods);
        return result.orElseGet(Object::new);
    }

    public List<Object> getObjects(Metamodel<?> metamodel){
        PrimaryField primary = metamodel.getPrimaryKey();
        List<ColumnField> columnFields = metamodel.getColumns();
        List<Method> methods = metamodel.getSetterMethods();

        ArrayList<Method> sortedMethods = sortMethodsByFields(primary,columnFields,methods);

        Optional<List<Object>> results = ormDAO.getObjectsFromTable(metamodel,sortedMethods);
        return results.orElseGet(ArrayList::new);

    }

    public boolean updateObject(Object obj){
        Metamodel<?> metamodel = new Metamodel<>(obj.getClass());
        if(ormDAO.updateObjectInTable(obj,metamodel)){
            return true;
        }
        return false;
    }

    public boolean deleteTableRow(int id,Metamodel<?> metamodel){
        if(ormDAO.deleteRow(id,metamodel)){
            return true;
        }
        return false;
    }

    public boolean dropTable(Metamodel<?> metamodel){
        if(ormDAO.dropTable(metamodel)){
            return true;
        }
        return false;
    }

    private ArrayList<String> convertColumnTypes(List<ColumnField> columns){
        ArrayList<String> tempList = new ArrayList<>();
        for(ColumnField column:columns){
            if(column.getType().getSimpleName().equals("String")){
                tempList.add("text");
            }
            else if(column.getType().getSimpleName().equals("char")){
                tempList.add("varchar(5)");
            }
            else if(column.getType().getSimpleName().equals("double") || column.getType().getSimpleName().equals("float")){
                tempList.add("float8");
            }
            else{
                tempList.add(column.getType().getSimpleName());
            }
        }
        return tempList;
    }

    private ArrayList<Method> sortMethodsByFields(PrimaryField primary,List<ColumnField> columns, List<Method>setterMethods){
        ArrayList<Method> sortedMethods = new ArrayList<>();
        String temp = primary.getName();
        temp = "set" + temp.substring(0,1).toUpperCase() + temp.substring(1).toLowerCase();

        for(int j=0;j<setterMethods.size();j++){
            if(setterMethods.get(j).getName().equals(temp)){
                sortedMethods.add(setterMethods.get(j));
                break;
            }
        }

        for(ColumnField column:columns){
            temp = column.getName();
            temp = "set" + temp.substring(0,1).toUpperCase() + temp.substring(1).toLowerCase();
            for(int i=0;i<setterMethods.size();i++){
                if(setterMethods.get(i).getName().equals(temp)){
                    sortedMethods.add(setterMethods.get(i));
                    break;
                }
            }
        }
        if(sortedMethods.size() == 0){
            throw new RuntimeException("Could not sort the methods");
        }
        return sortedMethods;
    }
}
