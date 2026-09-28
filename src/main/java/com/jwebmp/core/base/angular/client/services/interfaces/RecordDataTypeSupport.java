package com.jwebmp.core.base.angular.client.services.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDataType;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import jakarta.validation.constraints.NotNull;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.*;
import java.util.*;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgComponentReference;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgImportReference;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;

/** Record-specific data shape rendering. Class field rendering remains unchanged. */
final class RecordDataTypeSupport
{
    private RecordDataTypeSupport() {}

    static void render(StringBuilder out, RecordComponent component, NgDataType.DataTypeClass mode)
    {
        if (component.isAnnotationPresent(JsonIgnore.class) || component.getAccessor().isAnnotationPresent(JsonIgnore.class)) return;
        boolean required = component.getType().isPrimitive() || component.isAnnotationPresent(NotNull.class) || component.getAccessor().isAnnotationPresent(NotNull.class);
        String name = component.getName() + (required ? "" : "?");
        String type = type(component.getGenericType());
        if (mode == NgDataType.DataTypeClass.Interface)
        {
            out.append("  ").append(name).append(" : ").append(type).append(";\n");
        }
        else
        {
            out.append(" public ").append(name).append(" : ").append(type).append(" = ").append(defaultValue(component.getGenericType())).append(";\n");
        }
    }

    static String type(Type type)
    {
        if (type instanceof GenericArrayType array) return type(array.getGenericComponentType()) + "[]";
        if (type instanceof ParameterizedType parameterized)
        {
            Type raw = parameterized.getRawType();
            if (raw instanceof Class<?> rawClass && Collection.class.isAssignableFrom(rawClass))
                return type(parameterized.getActualTypeArguments()[0]) + "[]";
            if (raw instanceof Class<?> rawClass && Map.class.isAssignableFrom(rawClass))
                return "Record<string, " + type(parameterized.getActualTypeArguments()[1]) + ">";
            if (raw instanceof Class<?> rawClass && Optional.class.isAssignableFrom(rawClass))
                return type(parameterized.getActualTypeArguments()[0]) + " | null";
            if (raw instanceof Class<?> rawClass && INgDataType.class.isAssignableFrom(rawClass))
                return type(raw) + "<" + Arrays.stream(parameterized.getActualTypeArguments()).map(RecordDataTypeSupport::type).collect(java.util.stream.Collectors.joining(", ")) + ">";
            return type(raw);
        }
        if (type instanceof WildcardType wildcard) return type(wildcard.getUpperBounds()[0]);
        if (type instanceof TypeVariable<?> variable) return variable.getName();
        if (!(type instanceof Class<?> clazz)) return "any";
        if (clazz.isArray()) return type(clazz.getComponentType()) + "[]";
        if (clazz == boolean.class || clazz == Boolean.class) return "boolean";
        if (clazz == String.class || clazz == char.class || clazz == Character.class || clazz == UUID.class || clazz.isEnum() || clazz == LocalTime.class || clazz == Duration.class) return "string";
        if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) || clazz == BigDecimal.class || clazz == BigInteger.class) return "number";
        if (clazz == Date.class || clazz == LocalDate.class || clazz == LocalDateTime.class || clazz == OffsetDateTime.class || clazz == ZonedDateTime.class) return "Date";
        if (INgDataType.class.isAssignableFrom(clazz)) return getTsFilename(clazz);
        return "any";
    }

    static String defaultValue(Type type)
    {
        return defaultValue(type, new HashSet<>());
    }

    private static String defaultValue(Type type, Set<Class<?>> active)
    {
        if (type instanceof GenericArrayType) return "[]";
        if (type instanceof ParameterizedType parameterized)
        {
            if (parameterized.getRawType() instanceof Class<?> raw && Collection.class.isAssignableFrom(raw)) return "[]";
            if (parameterized.getRawType() instanceof Class<?> raw && Map.class.isAssignableFrom(raw)) return "{}";
            if (parameterized.getRawType() instanceof Class<?> raw && Optional.class.isAssignableFrom(raw)) return "null";
            if (parameterized.getRawType() instanceof Class<?> raw && INgDataType.class.isAssignableFrom(raw)) return "undefined as unknown as " + type(type);
        }
        if (!(type instanceof Class<?> clazz)) return "undefined";
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) return "[]";
        if (clazz == boolean.class || clazz == Boolean.class) return "false";
        if (clazz == String.class || clazz == char.class || clazz == Character.class || clazz == UUID.class || clazz.isEnum() || clazz == LocalTime.class || clazz == Duration.class) return "''";
        if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz)) return "0";
        if (clazz == Date.class || clazz == LocalDate.class || clazz == LocalDateTime.class || clazz == OffsetDateTime.class || clazz == ZonedDateTime.class) return "new Date()";
        if (clazz.isRecord() && INgDataType.class.isAssignableFrom(clazz))
        {
            if (active.contains(clazz)) return "undefined as unknown as " + type(clazz);
            return objectStructure(clazz, active).toString();
        }
        return "undefined";
    }

    static StringBuilder objectStructure(Class<?> record)
    {
        return objectStructure(record, new HashSet<>());
    }

    private static StringBuilder objectStructure(Class<?> record, Set<Class<?>> active)
    {
        active.add(record);
        StringBuilder out = new StringBuilder("{");
        for (RecordComponent component : record.getRecordComponents())
        {
            if (component.isAnnotationPresent(JsonIgnore.class) || component.getAccessor().isAnnotationPresent(JsonIgnore.class)) continue;
            if (out.length() > 1) out.append(",");
            out.append(component.getName()).append(": ").append(defaultValue(component.getGenericType(), active));
        }
        active.remove(record);
        return out.append("}");
    }

    static Object jsonValue(Object value)
    {
        if (value == null) return null;
        if (value instanceof Optional<?> optional) return optional.map(RecordDataTypeSupport::jsonValue).orElse(null);
        if (value instanceof Collection<?> collection)
        {
            List<Object> out = new ArrayList<>(collection.size());
            for (Object item : collection) out.add(jsonValue(item));
            return out;
        }
        if (value instanceof Map<?, ?> map)
        {
            Map<String, Object> out = new LinkedHashMap<>();
            map.forEach((key, item) -> out.put(String.valueOf(key), jsonValue(item)));
            return out;
        }
        if (value.getClass().isArray())
        {
            List<Object> out = new ArrayList<>();
            for (int i = 0; i < Array.getLength(value); i++) out.add(jsonValue(Array.get(value, i)));
            return out;
        }
        if (!value.getClass().isRecord()) return value;
        Map<String, Object> out = new LinkedHashMap<>();
        for (RecordComponent component : value.getClass().getRecordComponents())
        {
            if (component.isAnnotationPresent(JsonIgnore.class) || component.getAccessor().isAnnotationPresent(JsonIgnore.class)) continue;
            try
            {
                out.put(component.getName(), jsonValue(component.getAccessor().invoke(value)));
            }
            catch (ReflectiveOperationException e)
            {
                throw new IllegalStateException("Cannot serialize record component " + component.getName(), e);
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    static void references(List<NgComponentReference> out, Type type)
    {
        if (type instanceof GenericArrayType array) { references(out, array.getGenericComponentType()); return; }
        if (type instanceof ParameterizedType parameterized)
        {
            if (parameterized.getRawType() instanceof Class<?> raw && (Collection.class.isAssignableFrom(raw) || Optional.class.isAssignableFrom(raw)))
                references(out, parameterized.getActualTypeArguments()[0]);
            else if (parameterized.getRawType() instanceof Class<?> raw && Map.class.isAssignableFrom(raw))
                references(out, parameterized.getActualTypeArguments()[1]);
            else if (parameterized.getRawType() instanceof Class<?> raw && INgDataType.class.isAssignableFrom(raw))
                references(out, raw);
            return;
        }
        if (type instanceof Class<?> clazz)
        {
            if (clazz.isArray()) references(out, clazz.getComponentType());
            else if (INgDataType.class.isAssignableFrom(clazz)) out.add(getNgComponentReference((Class<? extends IComponent<?>>) clazz));
        }
    }

    static void correctImports(List<NgImportReference> imports, Class<?> owner)
    {
        List<NgComponentReference> references = new ArrayList<>();
        for (RecordComponent component : owner.getRecordComponents()) references(references, component.getGenericType());
        Path source = Path.of(ImportsStatementsComponent.getClassLocationDirectory(owner));
        for (NgComponentReference reference : references)
        {
            Class<?> target = reference.value();
            if (target == owner) continue;
            String name = getTsFilename(target);
            imports.removeIf(ref -> ref.value().equals(name));
            String path = source.relativize(Path.of(ImportsStatementsComponent.getClassLocationDirectory(target), name)).toString().replace('\\', '/');
            if (!path.startsWith(".")) path = "./" + path;
            imports.add(getNgImportReference(name, path));
        }
    }
}
