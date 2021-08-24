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

    /**
     * Given a metamodel of a class annotated with @Entity and a list of its fields annotated by
     * @Column in String format, creates a table of that class if it does not exist already.
     * @param metamodel
     * @param columnTypes
     * @return true if a table of the metamodel's Class was created or already exists; false otherwise
     */
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

    /**
     * Given an Object and a metamodel of the Object's Class, inserts the Object into the table
     * matching the Class.
     * @param obj
     * @param metamodel
     * @return true if the Object was successfully inserted; false otherwise
     */
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

    /**
     * Given an Id number serving as the primary key for a table of a Class stored in a Metamodel and a
     * list of setter methods from the same Class, creates an Object of the Class with the values at that Id
     * @param id
     * @param metamodel
     * @param sortedMethods
     * @return Optional of the object within the table in case it doesn't exist
     */
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

    /**
     * Given a Metamodel of a Class and its setter methods, retrieves all the data stored in said
     * Class' table and creates them into a list of objects of the Class
     * @param metamodel
     * @param sortedMethods
     * @return Optional List of objects of the table Class in case they don't exist
     */
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

    /**
     * Given an updated version of an Object and a Metamodel of its Class, find the matching primary key
     * in the database table of the Class and update its values to match that of the Object
     * @param obj
     * @param metamodel
     * @return true if Object was updated in its Class table; false otherwise
     */
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

    /**
     * Given an Id serving as a primary key for a table of a Class stored in Metamodel,
     * locates the specific row and deletes it from the table
     * @param id
     * @param metamodel
     * @return true if row was successfully deleted; false otherwise
     */
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

    /**
     * Given a Metamodel containing a Class that could have a table in the database,
     * finds if the table exists and drops it
     * @param metamodel
     * @return true if a table was dropped or never existed; false otherwise
     */
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

    //Closes the connection to the database, ties up loose connections
    @Override
    public void closeConnection(){
        try{
            connection.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
