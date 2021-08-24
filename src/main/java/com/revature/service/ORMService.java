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

    /**
     * default constructor
     */
    public ORMService(){
        ormDAO = new ORM_DAOImplement();
    }

    /**
     * Takes in a mock DAO as its DAO for service testing
     * @param ormDAO
     */
    public ORMService(ORM_DAO ormDAO){
        this.ormDAO = ormDAO;
    }

    /**
     * Given a Metamodel of a class communicates with the DAO layer to create and persist a table
     * in the database of that class
     * @param metamodel
     * @return true if the table now exists within the database; false otherwise
     */
    public boolean createEntityTable(Metamodel<?> metamodel){
        List<ColumnField> columns = metamodel.getColumns();
        ArrayList<String> typedColumns = convertColumnTypes(columns);
        if(ormDAO.createTable(metamodel,typedColumns)){
            return true;
        }
        return false;
    }

    /**
     * Given an object communicates with the DAO layer to find its corresponding class table and
     * inserts it into the database
     * @param obj
     * @return true if the object was inserted into its table
     */
    public boolean insertObject(Object obj){
        Metamodel<?> metamodel = new Metamodel<>(obj.getClass());
        if(ormDAO.insertObjectIntoTable(obj,metamodel)){
            return true;
        }
        return false;
    }

    /**
     * Given an id and a Metamodel of a Class, communicates with the DAO layer to find the
     * row in the Class' table and retrieves it in Object form
     * @param id
     * @param metamodel
     * @return an Object of the Metamodel's Class; null if it doesn't exist
     */
    public Object getObject(int id,Metamodel<?> metamodel){
        PrimaryField primary = metamodel.getPrimaryKey();
        List<ColumnField> columnFields = metamodel.getColumns();
        List<Method> methods = metamodel.getSetterMethods();

        ArrayList<Method> sortedMethods = sortMethodsByFields(primary,columnFields,methods);

        Optional<Object> result = ormDAO.getObjectFromTable(id,metamodel,sortedMethods);
        return result.orElseGet(Object::new);
    }

    /**
     * Given a Metamodel of a Class, communicates with the DAO layer to find the Class' table
     * in the database and retrieves all Objects stored within the table
     * @param metamodel
     * @return a List of Objects of the Metamodel's Class; empty List if doesn't exist
     */
    public List<Object> getObjects(Metamodel<?> metamodel){
        PrimaryField primary = metamodel.getPrimaryKey();
        List<ColumnField> columnFields = metamodel.getColumns();
        List<Method> methods = metamodel.getSetterMethods();

        ArrayList<Method> sortedMethods = sortMethodsByFields(primary,columnFields,methods);

        Optional<List<Object>> results = ormDAO.getObjectsFromTable(metamodel,sortedMethods);
        return results.orElseGet(ArrayList::new);

    }

    /**
     * Updates an Object with a primary key inside it within its corresponding Class' table
     * @param obj
     * @return true if object was updated in database; false otherwise
     */
    public boolean updateObject(Object obj){
        Metamodel<?> metamodel = new Metamodel<>(obj.getClass());
        if(ormDAO.updateObjectInTable(obj,metamodel)){
            return true;
        }
        return false;
    }

    /**
     * Given a int Id that corresponds to a primary key within a Metamodel's Class' table,
     * deletes that specific row in the table
     * @param id
     * @param metamodel
     * @return true if the row was deleted; false otherwise
     */
    public boolean deleteTableRow(int id,Metamodel<?> metamodel){
        if(ormDAO.deleteRow(id,metamodel)){
            return true;
        }
        return false;
    }

    /**
     * Given a Metamodel of a Class, communicates with the DAO layer to see if that Class
     * has a table within a database and drops the table if it exists
     * @param metamodel
     * @return true if table was dropped or never existed; false otherwise
     */
    public boolean dropTable(Metamodel<?> metamodel){
        if(ormDAO.dropTable(metamodel)){
            return true;
        }
        return false;
    }

    /**
     * Parses the @Column fields in a Class and converts their names into strings
     * @param columns
     * @return ArrayList of Strings of the @Column fields in a Class
     */
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

    /**
     * Creates a ArrayList of setter Methods in a Class sorted by the way they're stored in columns
     * inside a table
     * @param primary
     * @param columns
     * @param setterMethods
     * @return ArrayList of Methods sorted to be same order as a table
     */
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
