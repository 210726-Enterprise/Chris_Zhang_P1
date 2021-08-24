package com.revature.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {

    private static String dbUrl;
    private static String dbUsername;
    private static String dbPassword;
    private List<Metamodel<Class<?>>> metamodelList;

    public Config(){
    }

    public Config(String url,String username,String password){
        dbUrl = url;
        dbUsername = username;
        dbPassword = password;
    }

    public static String getUrl() {
        return dbUrl;
    }

    public static String getUsername() {
        return dbUsername;
    }

    public static String getPassword() {
        return dbPassword;
    }

    public void addAnnotatedClass(Class<?> annotatedClass){
        if(metamodelList == null){
            metamodelList = new ArrayList<>();
        }
        metamodelList.add(Metamodel.createMetamodel(annotatedClass));
    }

    public Metamodel<?> getModel(Class<?> clazz){
        for(Metamodel<?> metamodel:metamodelList){
            if(metamodel.getClassName().equals(clazz.getName())){
                return metamodel;
            }
        }
        return null;
    }

    public List<Metamodel<Class<?>>> getMetamodelList(){
        return (metamodelList == null) ? Collections.emptyList() : metamodelList;
    }
}
