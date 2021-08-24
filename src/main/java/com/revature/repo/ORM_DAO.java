package com.revature.repo;

import com.revature.util.Metamodel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ORM_DAO {

    //CREATE METHOD(S)
    boolean insertObjectIntoTable(Object obj, Metamodel<?> metamodel);
    boolean createTable(Metamodel<?> metamodel, ArrayList<String> columnTypes);

    //READ METHOD(S)
    Optional<Object> getObjectFromTable(int id, Metamodel<?> metamodel, ArrayList<Method> sortedMethods);
    Optional<List<Object>> getObjectsFromTable(Metamodel<?> metamodel, ArrayList<Method> sortedMethods);

    //UPDATE METHOD(S)
    boolean updateObjectInTable(Object obj, Metamodel<?> metamodel);
    void closeConnection();

    //DELETE METHOD(S)
    boolean deleteRow(int id, Metamodel<?> metamodel);
    boolean dropTable(Metamodel<?> metamodel);
}