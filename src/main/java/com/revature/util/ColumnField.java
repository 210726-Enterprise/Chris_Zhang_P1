package com.revature.util;

import com.revature.annotations.Column;

import java.lang.reflect.Field;

public class ColumnField {

    private Field field;

    public ColumnField(Field field){
        if(field.getAnnotation(Column.class) == null){
            throw new IllegalStateException("The field "+ field.getName() + "is not annotated with @Column");
        }
        this.field = field;
    }

    public String getName(){
        return field.getName();
    }

    public Class<?> getType(){
        return field.getType();
    }

    public Field getField(){
        return field;
    }

    public String getColumnName(){
        return field.getAnnotation(Column.class).columnName();
    }
}
