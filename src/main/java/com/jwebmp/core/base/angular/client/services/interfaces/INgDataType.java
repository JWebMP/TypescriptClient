package com.jwebmp.core.base.angular.client.services.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.guicedee.client.IGuiceContext;
import com.guicedee.services.jsonrepresentation.IJsonRepresentation;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDataType;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.tstypes.any;
import com.jwebmp.core.base.interfaces.ICSSImpl;
import com.jwebmp.core.base.servlets.interfaces.ICSSComponent;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.InvalidPathException;
import java.time.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgComponentReference;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;


public interface INgDataType<J extends INgDataType<J>>
        extends IComponent<J>, IJsonRepresentation<J>
{

    @Override
    default List<String> fields()
    {
        List<String> fields = IComponent.super.fields();
        if (fields == null)
        {
            fields = new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        Class<?> clazz = getClass();
        while (!clazz.equals(Object.class))
        {
            renderClassFields(sb, clazz);
            clazz = clazz.getSuperclass();
        }
        fields.add(sb.toString());
        return fields;
    }

    private void renderClassFields(StringBuilder sb, Class<?> clazz)
    {
        for (Field declaredField : clazz.getDeclaredFields())
        {
            renderFieldTS(sb, getFieldName(declaredField), declaredField.getType(), declaredField, false);
        }
    }

    default NgDataType.DataTypeClass typeClass()
    {
        List<NgDataType> cType = IGuiceContext.get(AnnotationHelper.class)
                                              .getAnnotationFromClass(getClass(), NgDataType.class);
        if (cType.isEmpty())
        {
            return NgDataType.DataTypeClass.Class;
        }
        var t = cType.getFirst();
        return t.value();
    }

    default Class getGenericTypeForField(Field field)
    {
        String typeName = field.getGenericType().getTypeName();
        String genericType = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
        if (genericType.contains("<"))
        {
            genericType = genericType.substring(0, genericType.indexOf("<"));
        }
        try
        {
            Class c = Class.forName(genericType);
            if (c.getSimpleName()
                 .equals("Object"))
            {
                return any.class;
            }
            return c;
        }
        catch (ClassNotFoundException | NullPointerException e)
        {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Something wrong with the generic type "
                + field.getName() + " in "
                + field.getDeclaringClass() + " / "
                + field.getGenericType()
                       .getTypeName());
        //return genericType;
    }

    default String typeField(Class fieldType, Field field)
    {
        String arrayString = fieldType.isArray() || Collection.class.isAssignableFrom(fieldType) ? "[]" : "";
        boolean array = arrayString.length() > 1;
        Class actualFieldType = field.getType();
        if (fieldType.isArray())
        {
            actualFieldType = fieldType.arrayType();
        }
        else if (Collection.class.isAssignableFrom(fieldType))
        {
            actualFieldType = getGenericTypeForField(field);
        }
        else
        {
            actualFieldType = fieldType;
        }


        /*Class actualFieldType = field.getType();
        if (array)
        {
            if (fieldType.isAssignableFrom(Collection.class))
            {
                var gt = getGenericTypeForField(field);
                try
                {
                    Class.forName(gt);
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }*/

        if (Object.class.equals(actualFieldType) || any.class.equals(actualFieldType))
        {
            return "any" + arrayString;
        }
        else if (
                Number.class.isAssignableFrom(actualFieldType) ||
                        BigDecimal.class.isAssignableFrom(actualFieldType) ||
                        BigInteger.class.isAssignableFrom(actualFieldType) ||
                        Integer.class.isAssignableFrom(actualFieldType) ||
                        Double.class.isAssignableFrom(actualFieldType) ||
                        Float.class.isAssignableFrom(actualFieldType) ||
                        Long.class.isAssignableFrom(actualFieldType) ||
                        int.class.isAssignableFrom(actualFieldType) ||
                        double.class.isAssignableFrom(actualFieldType) ||
                        float.class.isAssignableFrom(actualFieldType) ||
                        long.class.isAssignableFrom(actualFieldType)
        )
        {
            return "number" + arrayString;
        }
        else if (String.class.isAssignableFrom(actualFieldType) ||
                UUID.class.isAssignableFrom(actualFieldType) ||
                Character.class.isAssignableFrom(actualFieldType) ||
                actualFieldType.isEnum() ||
                LocalTime.class.isAssignableFrom(actualFieldType) ||
                Duration.class.isAssignableFrom(actualFieldType)
        )
        {
            return "string" + arrayString;
        }
        else if (Boolean.class.isAssignableFrom(actualFieldType) || boolean.class.isAssignableFrom(actualFieldType))
        {
            return "boolean" + arrayString;
        }
        else if (OffsetDateTime.class.isAssignableFrom(actualFieldType) ||
                LocalDateTime.class.isAssignableFrom(actualFieldType) ||
                ZonedDateTime.class.isAssignableFrom(actualFieldType) ||
                LocalDate.class.isAssignableFrom(actualFieldType) ||
                Date.class.isAssignableFrom(actualFieldType))
        {
            return "Date" + arrayString;
        }
        else if (INgDataType.class.isAssignableFrom(actualFieldType))
        {
            return getTsFilename(actualFieldType) + arrayString;
        }
        else if (IComponent.class.isAssignableFrom(actualFieldType))
        {
            return getTsFilename(actualFieldType) + arrayString;
        }
        else if (Collection.class.isAssignableFrom(actualFieldType))
        {
            return getGenericTypeForField(field).getSimpleName() + arrayString;
        }
        else if (ICSSImpl.class.isAssignableFrom(actualFieldType))
        {
            return "string" + arrayString;
        }
        else if (Serializable.class.getCanonicalName()
                                   .equals(actualFieldType.getCanonicalName()))
        {
            return "any" + arrayString;
        }
        else if (Map.class.isAssignableFrom(actualFieldType))
        {
            return "any" + arrayString;
        }
        else if (actualFieldType.isInterface())
        {
            return "any" + arrayString;
        }
        else if (Supplier.class.isAssignableFrom(actualFieldType) ||
                Consumer.class.isAssignableFrom(actualFieldType) ||
                IntFunction.class.isAssignableFrom(actualFieldType) ||
                DoubleFunction.class.isAssignableFrom(actualFieldType))
        {
            return "";
        }
        else
        {
            Logger.getLogger("DataType")
                  .warning("Type FIELD not catered for : [" + actualFieldType + "] - [" + getClass().getSimpleName() + "]");
            return "any" + arrayString;
        }
    }

    static String getFieldName(Field field)
    {
        return field.getName();
    }

    default void renderFieldTS(StringBuilder out, String fieldName, Class fieldType, Field field, boolean array)
    {
        if (field.getAnnotation(JsonIgnore.class) != null ||
                Modifier.isStatic(field.getModifiers()) ||
                Modifier.isFinal(field.getModifiers())
        )
        {
            return;
        }

        //    ObjectMapper mapper = IGuiceContext.get(DefaultObjectMapper);

        String optionalString = "";
        if (!isFieldRequired(field))
        {
            optionalString = "?";
        }

        var type = typeClass();
        String encap = " public";
        if (type == NgDataType.DataTypeClass.Interface)
        {
            encap = " ";
        }
        String fieldDeclaration = encap + " " + fieldName + optionalString;
        String typeField = typeField(fieldType, field);
        typeField = typeField.replace("java.lang.Object", "any");
        typeField = typeField.replace("za.co.uweassist.web.dto.", "");

        if (type == NgDataType.DataTypeClass.Interface)
        {
            out.append(fieldDeclaration + " : " + typeField + ";\n");
            return;
        }

        boolean added = appendBasicFieldType(out, fieldType, array, fieldDeclaration);


        if (Collection.class.isAssignableFrom(fieldType))
        {
            //get generic type
            String genericType = StringUtils.substringBetween(field.getGenericType()
                                                                   .getTypeName(), "<", ">");

            if (field.getGenericType() instanceof ParameterizedType)
            {
                var arguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                if (arguments != null)
                {
                    Object arg0 = arguments[0];
                    if (arg0 instanceof ParameterizedType)
                    {
                        ParameterizedType pType = (ParameterizedType) arguments[0];
                        Class<?> arg0Clazz = pType.getRawType()
                                                  .getClass();
                        try
                        {
                            appendBasicFieldType(out, arg0Clazz, true, fieldDeclaration);
                            //renderFieldTS(out, fieldName, arg0Clazz, field, true);
                        }
                        catch (InvalidPathException ipe)
                        {
                            Logger.getLogger("INgDataType")
                                  .log(Level.SEVERE, " Unable to generate generic based class - ", ipe);
                        }
                    }
                    else if (arg0 instanceof Class<?>)
                    {
                        try
                        {
                            renderFieldTS(out, fieldName, (Class) arg0, field, true);
                        }
                        catch (InvalidPathException ipe)
                        {
                            Logger.getLogger("INgDataType")
                                  .log(Level.SEVERE, " Unable to generate generic based class - ", ipe);
                        }
                    }
                }
            }
            else
            {
                try
                {

                    renderFieldTS(out, fieldName, Class.forName(genericType), field, true);
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
                catch (InvalidPathException ipe)
                {
                    Logger.getLogger("INgDataType")
                          .log(Level.SEVERE, " Unable to generate generic based class - ", ipe);
                }
            }
        }
        else if (fieldType.isArray())
        {
            if (fieldType.arrayType()
                         .isPrimitive())
            {
                appendBasicFieldType(out, fieldType.arrayType(), true, fieldDeclaration);
            }
            else
            {
                //get generic type
                String genericType = fieldType.arrayType()
                                              .getCanonicalName();
                try
                {
                    appendBasicFieldType(out, Class.forName(genericType), true, fieldDeclaration);
                    //renderFieldTS(out, fieldName, Class.forName(genericType), field, true);
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (fieldType.isEnum())
        {
            //get generic type
            String genericType = fieldType.arrayType()
                                          .getCanonicalName();
            try
            {
                //todo render then enum type and reference, use the nice typescript way
                renderFieldTS(out, fieldName, Class.forName(genericType), field, false);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static boolean appendBasicFieldType(StringBuilder out, Class fieldType, boolean array, String fieldDeclaration)
    {
        if (INgDataType.class.isAssignableFrom(fieldType))
        {
            //todo make this import the data type from the class
            //out.append(" public " + fieldName + "? : " + getTsFilename(fieldType) + "" + (array ? "[]" : "") + " = " + (array ? "[]" : "{}") + ";\n");
            String typeName = getTsFilename(fieldType);
            out.append(fieldDeclaration + " : " + typeName + " " + (array ? "[]" : "") + " = " + (array ? "[]" : renderObjectStructure(fieldType)) + ";\n");
            return true;
        }
        else if (IComponent.class.isAssignableFrom(fieldType))
        {
            //todo make this import the data type from the class
            //out.append(" public " + fieldName + "? : " + getTsFilename(fieldType) + "" + (array ? "[]" : "") + " = " + (array ? "[]" : "{}") + ";\n");
            out.append(fieldDeclaration + " : any " + (array ? "[]" : "") + " = " + (array ? "[]" : renderObjectStructure(fieldType)) + ";\n");
            return true;
        }
        else if (Object.class.equals(fieldType))
        {
            out.append(fieldDeclaration + " : any" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
            return true;
        }
        else if (Number.class.isAssignableFrom(fieldType))
        {
            out.append(fieldDeclaration + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
            return true;
        }
        else if (Long.class.isAssignableFrom(fieldType))
        {
            out.append(fieldDeclaration + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
            return true;
        }
        else if (Integer.class.isAssignableFrom(fieldType))
        {
            out.append(fieldDeclaration + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
            return true;
        }
        else if (Double.class.isAssignableFrom(fieldType))
        {
            out.append(fieldDeclaration + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
            return true;
        }
        else if (BigDecimal.class.isAssignableFrom(fieldType))
        {
            out.append(fieldDeclaration + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
            return true;
        }
        else if (BigInteger.class.isAssignableFrom(fieldType))
        {
            out.append(fieldDeclaration + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
            return true;
        }
        else if (String.class.isAssignableFrom(fieldType) || UUID.class.isAssignableFrom(fieldType) || Character.class.isAssignableFrom(fieldType))
        {
            out.append(fieldDeclaration + " : string" + (array ? "[]" : "") + " = " + (array ? "[]" : "''") + ";\n");
            return true;
        }
        else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType))
        {
            out.append(fieldDeclaration + " : boolean" + (array ? "[]" : "") + " =" + (array ? "[]" : "false") + ";\n");
            return true;
        }
        else if (OffsetDateTime.class.isAssignableFrom(fieldType) ||
                LocalDateTime.class.isAssignableFrom(fieldType) ||
                ZonedDateTime.class.isAssignableFrom(fieldType) ||
                LocalDate.class.isAssignableFrom(fieldType) ||
                Date.class.isAssignableFrom(fieldType)
        )
        {
            out.append(fieldDeclaration + " : Date" + (array ? "[]" : "") + " =" + (array ? "[]" : "new Date()") + ";\n");
            return true;
        }
        return false;
    }

    static boolean isFieldRequired(Field field)
    {
        return field.getAnnotation(NotNull.class) != null ||
                field.getType()
                     .isPrimitive()
                ;
    }

    static StringBuilder renderObjectStructure(Class<?> o)
    {
        StringBuilder out = new StringBuilder();
        out.append("{");
        for (Field declaredField : o.getDeclaredFields())
        {
            if (Modifier.isStatic(declaredField.getModifiers()) || Modifier.isFinal(declaredField.getModifiers()) || declaredField.getAnnotation(JsonIgnore.class) != null)
            {
                continue;
            }

            if (out.length() != 1)
            {
                out.append(",");
            }
            //  if (isFieldRequired(declaredField))
            //    {
            out.append(getFieldName(declaredField));
            out.append(" ");
            out.append(":");

            if (declaredField.getType()
                             .isEnum())
            {
                out.append("''");
            }
            else
            {
                switch (declaredField.getType()
                                     .getSimpleName())
                {
                    case "String":
                    case "Character":
                    case "char":
                    case "UUID":
                    case "Duration":
                        out.append("''");
                        break;
                    case "Boolean":
                    case "boolean":
                        out.append("false");
                        break;
                    case "Integer":
                    case "int":
                    case "Double":
                    case "double":
                    case "Long":
                    case "long":
                    case "Float":
                    case "float":
                    case "BigDecimal":
                    case "BigInteger":
                        out.append("0");
                        break;
                    case "OffsetDateTime":
                    case "Date":
                    case "LocalDateTime":
                    case "ZonedDateTime":
                    case "LocalDate":
                        out.append("new Date()");
                        break;
                    case "List":
                    case "ArrayList":
                    case "Set":
                    case "TreeSet":
                        out.append("[]");
                        break;
                    case "HashMap":
                    case "Map":
                    case "TreeMap":
                        out.append("{}");
                        break;
                    case "Object":
                        out.append("any");
                        break;
                    default:
                    {
                        if (o.getAnnotationsByType(NgDataType.class).length > 0 && !o.isEnum())
                        {
                            out.append(renderObjectStructure(declaredField.getType()));
                        }
                        else if (o.getAnnotationsByType(NgDataType.class).length > 0 && !o.isEnum())
                        {
                            out.append("''");
                        }
                        else
                        {
                            Logger.getLogger("DataType")
                                  .warning("Render Object Structure Type not catered for : " + declaredField.getType() + " in [" + o.getCanonicalName() + "]");
                        }
                        //    out.append(renderObjectStructure(declaredField.getType()));
                    }

                }
            }
/*            }
            else
            {
                out.append(getFieldName(declaredField));
                out.append("?");
            }*/
        }
        out.append("}");
        return out;
    }

    @Override
    default List<NgComponentReference> getComponentReferences()
    {
        List<NgComponentReference> out = IComponent.super.getComponentReferences();
        out.addAll(renderFieldReferences(getClass()));

        return out;
    }

    private List<NgComponentReference> renderFieldReferences(Class<?> clazz)
    {
        List<NgComponentReference> out = IComponent.super.getComponentReferences();
        for (Field declaredField : clazz.getDeclaredFields())
        {
            out.addAll(renderFieldReference(declaredField.getName(), declaredField.getType(), declaredField, false));
        }
        return out;
    }

    default List<NgComponentReference> renderFieldReference(String fieldName, Class fieldType, Field field, boolean array)
    {
        List<NgComponentReference> refs = new ArrayList<>();

        if (INgDataType.class.isAssignableFrom(fieldType))
        {
            refs.add(getNgComponentReference(fieldType));
        }
        else if (Collection.class.isAssignableFrom(fieldType) || fieldType.isArray())
        {
            if (fieldType.isArray())
            {
                System.out.print("field is array - ");
            }
            if (field.getGenericType() instanceof ParameterizedType)
            {
                var arguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                if (arguments != null)
                {
                    Object arg0 = arguments[0];
                    if (arg0 instanceof ParameterizedType)
                    {
                        ParameterizedType pType = (ParameterizedType) arguments[0];
                        Class<?> arg0Clazz = pType.getRawType()
                                                  .getClass();
                        try
                        {
                            refs.add(getNgComponentReference((Class<? extends IComponent<?>>) arg0Clazz));
                        }
                        catch (InvalidPathException ipe)
                        {
                            Logger.getLogger("INgDataType")
                                  .log(Level.SEVERE, " Unable to generate generic based class - ", ipe);
                        }
                    }
                    else if (arg0 instanceof Class<?> && !arg0.equals(Object.class) && !arg0.equals(String.class) && !arg0.equals(Boolean.class) && !arg0.equals(Integer.class) && !arg0.equals(Double.class))
                    {
                        refs.add(getNgComponentReference((Class<? extends IComponent<?>>) arg0));
                    }
                }
            }
        }
   /*     else if (fieldType.isArray())
        {
            //get generic type
            String genericType = fieldType.arrayType()
                                          .getCanonicalName();
            appendBasicFieldType();
            renderFieldReference(fieldName, fieldType.arrayType(), field, true);
        }*/
        return refs;
    }

    @Override
    default Map<String, String> imports()
    {
        Map<String, String> imports = IComponent.super.imports();

        return imports;
    }
}
