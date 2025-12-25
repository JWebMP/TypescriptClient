package com.jwebmp.core.base.angular.client.services.interfaces;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class INgDataTypeTest {

    static class TestClass implements INgDataType<TestClass> {
        public List<String> simpleList;
        public List<com.jwebmp.core.base.angular.client.services.interfaces.INgDataTypeTest.NestedClass<?>> nestedList;
        public List<com.jwebmp.core.base.angular.client.services.interfaces.INgDataTypeTest.NestedClass<String>> nestedListWithString;
    }

    static class NestedClass<T> {}

    @Test
    public void testGetGenericTypeForField() throws NoSuchFieldException {
        TestClass testObj = new TestClass();
        
        Field simpleField = TestClass.class.getField("simpleList");
        Class simpleType = testObj.getGenericTypeForField(simpleField);
        assertEquals(String.class, simpleType);

        Field nestedField = TestClass.class.getField("nestedList");
        Class nestedType = testObj.getGenericTypeForField(nestedField);
        assertEquals(NestedClass.class, nestedType);

        Field nestedFieldString = TestClass.class.getField("nestedListWithString");
        Class nestedTypeString = testObj.getGenericTypeForField(nestedFieldString);
        assertEquals(NestedClass.class, nestedTypeString);
    }
}
