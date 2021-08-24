package com.revature.repo;

import com.revature.util.ColumnField;
import com.revature.util.ConnectionFactory;
import com.revature.util.Metamodel;
import com.revature.util.PrimaryField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ORM_DAOImplement implements ORM_DAO{
    ConnectionFactory connectionFactory = new ConnectionFactory();
    private final Connection connection = connectionFactory.getConnection();

    @Override
    public boolean createTable(Metamodel<?> metamodel, ArrayList<String> columnTypes) {
        //given an objects class and its params create a table for it to store data in
        PrimaryField primary = metamodel.getPrimaryKey();
        List<ColumnField> columns = metamodel.getColumns();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create table if not exists "+ metamodel.getTableName()+"("
                +primary.getPrimaryColumnName()+" serial constraint pk_"
                +metamodel.getSimpleClassName()+"_key primary key, ");
        for(int i=0;i<columns.size()-1;i++){
            stringBuilder.append(columns.get(i).getColumnName() +" "+columnTypes.get(i)+", ");
        }
        stringBuilder.append(columns.get(columns.size()-1).getColumnName() +" "+columnTypes.get(columnTypes.size()-1)+")");
        String query = stringBuilder.toString();
        Statement statement;

        try {
            statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean insertObjectIntoTable(Object obj, Metamodel<?> metamodel){
        //Given an object put it into its table based on its class name
        StringBuilder stringBuilder = new StringBuilder();
        List<ColumnField> columns = metamodel.getColumns();

        stringBuilder.append("insert into "+metamodel.getTableName()+" (");
        for(int i=0;i<columns.size()-1;i++){
            stringBuilder.append(columns.get(i).getColumnName()+",");
        }
        stringBuilder.append(columns.get(columns.size()-1).getColumnName()+") values (");
        for(int j=0;j<columns.size()-1;j++){
            stringBuilder.append("?,");
        }
        stringBuilder.append("?)");

        String query = stringBuilder.toString();
        PreparedStatement ps;

        try {
            ps = connection.prepareStatement(query);
            for(int k=0;k<columns.size()-1;k++){
                try {
                    ps.setObject(k+1,columns.get(k).getField().get(obj));
                } catch (IllegalAccessException e) {
                    System.out.println("Cannot access the field "+columns.get(k).getName());
                    return false;
                }
            }
            try {
                ps.setObject(columns.size(),columns.get(columns.size()-1).getField().get(obj));
            } catch (IllegalAccessException e) {
                System.out.println("Cannot access the field "+columns.get(columns.size()-1).getName());
                return false;
            }
            ps.execute();
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Optional<Object> getObjectFromTable(int id,Metamodel<?> metamodel,ArrayList<Method> sortedMethods) {
        //given an objects class name and primary key, get its data out of a table and return it as object
        PrimaryField primary = metamodel.getPrimaryKey();
        String query = "select * from "+metamodel.getTableName()+" where "+primary.getPrimaryColumnName()+" = "+id;
        Statement statement;

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if(rs.next()){
                Constructor<?>[] constructors = metamodel.getClazz().getConstructors();
                Object obj = null;
                metamodel.getClazz().cast(obj);

                try {
                    obj = Arrays.stream(constructors).filter(c -> c.getParameterTypes().length == 0)
                            .findFirst().orElseThrow(RuntimeException::new).newInstance();
                    for(int i=0;i<sortedMethods.size();i++){
                        sortedMethods.get(i).invoke(obj,rs.getObject(i+1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
                return Optional.of(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Object>> getObjectsFromTable(Metamodel<?> metamodel,ArrayList<Method> sortedMethods) {
        //given an object's class find its table and return all the entries in that table
        List<Object> allEntries = new ArrayList<>();
        String query = "select * from "+metamodel.getTableName();
        Statement statement;
        Constructor<?>[] constructors = metamodel.getClazz().getConstructors();
        Object obj = null;
        metamodel.getClazz().cast(obj);

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                try {
                    obj = Arrays.stream(constructors).filter(c -> c.getParameterTypes().length == 0)
                            .findFirst().orElseThrow(RuntimeException::new).newInstance();
                    for(int i=0;i<sortedMethods.size();i++){
                        sortedMethods.get(i).invoke(obj,rs.getObject(i+1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
                allEntries.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        if(allEntries.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(allEntries);
    }


    @Override
    public boolean updateObjectInTable(Object obj,Metamodel<?> metamodel) {
        PrimaryField primary = metamodel.getPrimaryKey();
        List<ColumnField> columns = metamodel.getColumns();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("update "+metamodel.getTableName()+" set ");
        for(int i=0;i< columns.size()-1;i++){
            stringBuilder.append(columns.get(i).getColumnName() + " = ? ,");
        }
        try {
            stringBuilder.append(columns.get(columns.size()-1).getColumnName() + " = ? ");
            stringBuilder.append("where " + primary.getPrimaryColumnName() + " = "
                    + primary.getField().get(obj));

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
        String query = stringBuilder.toString();
        PreparedStatement ps;

        try {
            ps = connection.prepareStatement(query);
            for(int j=0;j<columns.size()-1;j++){
                try {
                    ps.setObject(j+1,columns.get(j).getField().get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            try {
                ps.setObject(columns.size(),columns.get(columns.size()-1).getField().get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteRow(int id, Metamodel<?> metamodel) {
        PrimaryField primary = metamodel.getPrimaryKey();
        String query = "delete from "+metamodel.getTableName()+" where "+primary.getPrimaryColumnName()+" = "+id;
        int success = 0;
        Statement statement;

        try {
            statement = connection.createStatement();
            success = statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return success == 1;
    }

    @Override
    public boolean dropTable(Metamodel<?> metamodel){
        String query = "drop table if exists "+ metamodel.getTableName();
        Statement statement;

        try {
            statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void closeConnection(){
        try{
            connection.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
