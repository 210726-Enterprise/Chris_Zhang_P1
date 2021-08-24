package com.revature.util;

import com.revature.annotations.Column;
import com.revature.annotations.Entity;
import com.revature.annotations.Primary;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Metamodel<T> {

    private Class<?> clazz;
    private PrimaryField primary;
    private List<ColumnField> columnFields;
    private List<Method> setterMethods;

    /**
     * Takes in a Class and serves as a wrapper for it as well as obtaining information from it
     * @param clazz
     */
    public Metamodel(Class<?> clazz){
        this.clazz = clazz;
    }

    public static <T> Metamodel<T> createMetamodel(Class<?> clazz){
        if(clazz.getAnnotation(Entity.class) == null){
            throw new IllegalStateException("The class "+clazz.getName()+ "does not have the @Entity annotation");
        }
        return new Metamodel<>(clazz);
    }

    public String getClassName(){
        return clazz.getName();
    }

    public String getSimpleClassName(){
        return clazz.getSimpleName();
    }

    public Class<?> getClazz(){
        return clazz;
    }

    public String getTableName(){
        return clazz.getAnnotation(Entity.class).tableName();
    }

    public PrimaryField getPrimaryKey(){
        if(primary == null){
            Field[] fields = clazz.getDeclaredFields();
            for(Field field: fields) {
                Primary primaryKey = field.getAnnotation(Primary.class);
                if (primaryKey != null) {
                    field.setAccessible(true);
                    primary = new PrimaryField(field);
                    return primary;
                }
            }
            throw new RuntimeException("Could not find a field annotated with @Primary");
        }
        return primary;
    }

    public List<ColumnField> getColumns() {
        if (columnFields == null) {
            columnFields = new ArrayList<>();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    field.setAccessible(true);
                    columnFields.add(new ColumnField(field));
                }
            }
        }
        if (columnFields.isEmpty()) {
            throw new RuntimeException("No columns found in: " + getClassName());
        }
        return columnFields;
    }

    public List<Method> getSetterMethods(){
        if(setterMethods == null) {
            setterMethods = new ArrayList<>();
            Method[] methods = clazz.getDeclaredMethods();
            if (methods.length == 0) {
                throw new RuntimeException("No methods in " + getClassName());
            }
            Arrays.stream(methods).filter(m -> m.getName().startsWith("set"));

            for (Method method : methods) {
                setterMethods.add(method);
            }
        }
        if (setterMethods.size() == 0) {
            throw new RuntimeException("No methods starting with 'set' in " + getClassName());
        }
        return setterMethods;
    }
}
