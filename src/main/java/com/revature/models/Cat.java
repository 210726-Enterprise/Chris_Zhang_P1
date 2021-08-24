package com.revature.models;

import com.revature.annotations.Column;
import com.revature.annotations.Entity;
import com.revature.annotations.Primary;

@Entity(tableName = "cats")
public class Cat {

    @Primary(primaryColumnName = "cat_id")
    private int id;

    @Column(columnName = "name")
    private String name;

    @Column(columnName = "type")
    private String type;

    @Column(columnName = "age")
    private int age;

    public Cat(){
    }

    public Cat(String name, String type,int age){
        this.name = name;
        this.type = type;
        this.age = age;
    }

    public Cat(int id, String name, String type, int age){
        this(name,type,age);
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override //[1,Mr.Whiskers,Persian,22]
    public String toString(){
        if(id != 0){
            return"["+id+","+name+","+ type +","+age+"]";
        }
        return "["+name+","+ type +","+age+"]"; }
}