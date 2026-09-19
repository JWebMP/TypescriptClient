package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.NgDataType;
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

    static class ArrayDataType implements INgDataType<ArrayDataType> {
        public int[] daysOfWeek;
        public int[][] matrix;
        public String[] names;
        public boolean[] flags;

        @Override
        public NgDataType.DataTypeClass typeClass() {
            return NgDataType.DataTypeClass.Class;
        }
    }

    @Test
    void mapsArrayElementTypesAndDimensions() throws NoSuchFieldException {
        ArrayDataType data = new ArrayDataType();
        assertArrayField(data, "daysOfWeek", "number[]");
        assertArrayField(data, "matrix", "number[][]");
        assertArrayField(data, "names", "string[]");
        assertArrayField(data, "flags", "boolean[]");
    }

    private void assertArrayField(ArrayDataType data, String name, String expectedType) throws NoSuchFieldException {
        Field field = ArrayDataType.class.getField(name);
        assertEquals(expectedType, data.typeField(field.getType(), field));
        StringBuilder rendered = new StringBuilder();
        data.renderFieldTS(rendered, name, field.getType(), field, false);
        assertEquals(" public " + name + "? : " + expectedType + " = [];\n", rendered.toString());
    }

    @Test
    void rendersArrayInterfaceFieldWithoutInitializer() throws NoSuchFieldException {
        ArrayDataType data = new ArrayDataType() {
            @Override
            public NgDataType.DataTypeClass typeClass() {
                return NgDataType.DataTypeClass.Interface;
            }
        };
        Field field = ArrayDataType.class.getField("daysOfWeek");
        StringBuilder rendered = new StringBuilder();
        data.renderFieldTS(rendered, field.getName(), field.getType(), field, false);
        assertEquals("  daysOfWeek? : number[];\n", rendered.toString());
    }

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
