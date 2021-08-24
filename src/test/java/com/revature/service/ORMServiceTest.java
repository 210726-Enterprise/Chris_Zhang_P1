package com.revature.service;

import com.revature.models.Cat;
import com.revature.util.Metamodel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ORMServiceTest {
    Metamodel metamodel;
    Cat cat;
    Cat catUpdate;
    ORMService service = new ORMService();

    @Before
    public void setUp() throws Exception {
        metamodel = new Metamodel(Cat.class);
        cat = new Cat("Test","Cat",0);
        catUpdate = new Cat(1,"Steroids","Buff",0);
        dropTableTest();
    }

    @Test
    public void testInOrderTest(){
        createEntityTableTest();
        insertObjectTest();
        getObjectTest();
        getObjectsTest();
        updateObjectTest();
        deleteTableRowTest();
        dropTableTest();
    }

    public void createEntityTableTest(){
        assertTrue("createEntityTable didn't function as intended.",
                service.createEntityTable(metamodel));
    }

    public void insertObjectTest(){
        assertTrue("insertObjectIntoTable didn't function as intended",
                service.insertObject(cat));
    }

    public void getObjectTest(){
        Cat sample = (Cat) service.getObject(1,metamodel);
        assertFalse("cat ran away",sample.equals(null));

        assertTrue(cat.getName()+" does not equal "+sample.getName(),
                    cat.getName().equals(sample.getName()));
        assertTrue(cat.getType()+" does not equal "+sample.getType(),
                    cat.getType().equals(sample.getType()));
        assertTrue(cat.getAge()+" does not equal "+sample.getAge(),
                    cat.getAge() == sample.getAge());
    }

    public void getObjectsTest(){
        List<Object> sampleYep = service.getObjects(metamodel);
        assertFalse("cats ran away",sampleYep.equals(null));
        for (Object obj : sampleYep){
            assertTrue(obj.getClass()+" is not a Cat",obj.getClass().equals(Cat.class));
        }
    }

    public void updateObjectTest(){
        assertTrue("cat didn't like needle",service.updateObject(catUpdate));
    }

    public void deleteTableRowTest(){
        assertTrue("cat avoided deletion",service.deleteTableRow(1,metamodel));
    }

    public void dropTableTest(){
        assertTrue("┬─┬ノ(º_ºノ)",service.dropTable(metamodel));
    }

    @After
    public void shutDown(){
        createEntityTableTest();
    }
}