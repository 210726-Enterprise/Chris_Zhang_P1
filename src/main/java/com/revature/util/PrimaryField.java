package com.revature.util;

import com.revature.annotations.Primary;

import java.lang.reflect.Field;

public class PrimaryField {

    private Field field;

    public PrimaryField(Field field){
        if(field.getAnnotation(Primary.class) == null){
            throw new IllegalStateException("The field "+ field.getName() + "is not annotated with @Primary");
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

    public String getPrimaryColumnName(){
        return field.getAnnotation(Primary.class).primaryColumnName();
    }
}
