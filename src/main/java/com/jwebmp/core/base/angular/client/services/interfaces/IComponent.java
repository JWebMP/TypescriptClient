package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.Strings;
import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedinjection.interfaces.IDefaultService;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDataType;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgInterface;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.spi.*;

import java.io.File;
import java.util.*;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.*;

public interface IComponent<J extends IComponent<J>> extends IDefaultService<J>, ImportsStatementsComponent<J>
{
    ThreadLocal<INgApp<?>> app = ThreadLocal.withInitial(() -> null);

    ThreadLocal<File> currentAppFile = ThreadLocal.withInitial(() -> null);

    static ThreadLocal<File> getCurrentAppFile()
    {
        return currentAppFile;
    }


    default J me()
    {
        return (J) this;
    }

    // Component Reference Location Assists
    static String getClassDirectory(Class<?> clazz)
    {
        return clazz.getPackageName()
                .replace('\\', '/');
    }

    default String renderBeforeClass()
    {
        return "";
    }

    default String renderAfterClass()
    {
        return "";
    }

    default boolean exportsClass()
    {
        return true;
    }

    default List<NgField> getAllFields()
    {
        List<NgField> out = new ArrayList<>();
        for (NgField annotation : IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgField.class))
        {
            if (annotation.onSelf())
            {
                out.add(annotation);
            }
        }


        for (NgComponentReference annotation : IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgComponentReference.class))
        {
            Class<?> reference = annotation.value();
            for (NgField ngField : IGuiceContext.get(AnnotationHelper.class)
                    .getAnnotationFromClass(reference, NgField.class))
            {
                if (ngField.onParent())
                {
                    out.add(ngField);
                }
            }


        }

        for (String field : fields())
        {
            out.add(getNgField(field));
        }

        Set<OnGetAllFields> interceptors = IGuiceContext.loaderToSet(ServiceLoader.load(OnGetAllFields.class));
        for (OnGetAllFields interceptor : interceptors)
        {
            interceptor.perform(out, this);
        }
        return out;
    }

    default List<NgConstructorParameter> getAllConstructorParameters()
    {
        List<NgConstructorParameter> out = new ArrayList<>();

        for (NgConstructorParameter annotation : IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgConstructorParameter.class))
        {
            if (annotation.onSelf())
            {
                out.add(annotation);
            }
        }
        List<NgGlobalConstructorParameter> allGlobals = IGuiceContext.get(AnnotationHelper.class)
                .getGlobalAnnotations(NgGlobalConstructorParameter.class);
        for (NgGlobalConstructorParameter global : allGlobals)
        {
            NgConstructorParameter param = getNgConstructorParameter(global.value());
            out.add(param);
        }

        for (String constructorParameter : constructorParameters())
        {
            out.add(getNgConstructorParameter(constructorParameter));
        }

        Set<OnGetAllConstructorParameters> interceptors = IGuiceContext.loaderToSet(ServiceLoader.load(OnGetAllConstructorParameters.class));
        for (OnGetAllConstructorParameters interceptor : interceptors)
        {
            interceptor.perform(out, this);
        }

        return out;
    }

    default List<NgConstructorBody> getAllConstructorBodies()
    {
        List<NgConstructorBody> out = new ArrayList<>();
        for (NgConstructorBody annotation : IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgConstructorBody.class))
        {
            if (annotation.onSelf())
            {
                out.add(annotation);
            }
        }

        //check references for constructors needed
        for (NgComponentReference annotation : IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgComponentReference.class))
        {
            Class<?> clazz = annotation.value();
            for (NgConstructorBody ngConstructorBody : IGuiceContext.get(AnnotationHelper.class)
                    .getAnnotationFromClass(clazz, NgConstructorBody.class))
            {
                if (ngConstructorBody.onParent())
                {
                    out.add(ngConstructorBody);
                }
            }
        }

        for (String body : constructorBody())
        {
            out.add(getNgConstructorBody(body));
        }

        Set<OnGetAllConstructorBodies> interceptors = IGuiceContext.loaderToSet(ServiceLoader.load(OnGetAllConstructorBodies.class));
        for (OnGetAllConstructorBodies interceptor : interceptors)
        {
            interceptor.perform(out, this);
        }

        return out;
    }

    //***************************************************************************************
    //***************************************************************************************
    // Renderers
    //***************************************************************************************

    default List<NgMethod> renderAllMethods()
    {
        List<NgMethod> out = new ArrayList<>();
        for (NgMethod annotation : IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgMethod.class))
        {
            if (annotation.onSelf())
            {
                out.add(annotation);
            }
        }
        for (NgComponentReference annotation : IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgComponentReference.class))
        {
            Class<?> reference = annotation.value();
            for (NgMethod ngMethod : IGuiceContext.get(AnnotationHelper.class)
                    .getAnnotationFromClass(reference, NgMethod.class))
            {
                if (ngMethod.onParent())
                {
                    out.add(ngMethod);
                }
            }
        }

        for (String componentMethod : componentMethods())
        {
            out.add(getNgMethod(componentMethod.trim()));
        }

        for (String componentMethod : methods())
        {
            out.add(getNgMethod(componentMethod.trim()));
        }
        return out;
    }

    default StringBuilder renderImports()
    {
        StringBuilder sb = new StringBuilder();
        List<NgImportReference> refs = getAllImportAnnotations();
        refs = clean(refs);
        refs.forEach((ref) -> {
            String refString = ref.reference();
            //refString = ImportsStatementsComponent.removeFirstParentDirectoryAsString(refString.replace('\\', '/'));
            if (ref.direct())
            {
                sb.append(String.format(importDirectString, ref.value()));
            }
            else if (!ref.value()
                    .startsWith("!"))
            {
                sb.append(String.format(importString, ref.value(), refString));
            }
            else
            {
                sb.append(String.format(importPlainString, ref.value()
                        .substring(1), refString));
            }
        });
        return sb;
    }

    default StringBuilder renderClassTs()
    {
        StringBuilder out = new StringBuilder();
        out.append(renderImports());
        @SuppressWarnings("unchecked")
        J component = (J) this;

        if (!Strings.isNullOrEmpty(component.renderBeforeClass()))
        {
            out.append(component.renderBeforeClass());
        }

        for (String globalField : globalFields())
        {
            out.append(globalField)
                    .append("\n");
        }

        for (String decorator : decorators())
        {
            out.append(decorator)
                    .append("\n");
        }

        out.append(renderClassDefinition());

        if (!Strings.isNullOrEmpty(component.renderAfterClass()))
        {
            out.append(";")
                    .append(component.renderAfterClass());
        }

        return out;
    }

    default StringBuilder renderClassDefinition()
    {
        StringBuilder out = new StringBuilder();
        out.append(exportsClass() ? "export " : "");

        List<NgDataType> cType = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgDataType.class);
        if (!cType.isEmpty())
        {
            out.append(cType.getFirst()
                            .value()
                            .description())
                    .append(" ");
            String functionName = cType.getFirst()
                    .name();
            if (!Strings.isNullOrEmpty(cType.getFirst()
                    .returnType()))
            {
                if (!Strings.isNullOrEmpty(functionName))
                {
                    out.append(" " + functionName + " ");
                }
                else
                {
                    out.append(" " + getTsFilename(getClass()) + " ");
                }
                out.append("() :  ")
                        .append(cType.getFirst()
                                .returnType());
            }
        }
        else
        {
            out.append("class ");
        }
        out.append(getTsFilename(getClass()));

        if (!Strings.isNullOrEmpty(ofType()))
        {
            out.append(" ")
                    .append(ofType());
        }

        out.append(renderInterfaces());

        out.append("\n");
        out.append(renderClassBody());
        return out;
    }

    /**
     * Renders after the class statement
     *
     * @return
     */
    default StringBuilder renderAfterClassEntry()
    {
        StringBuilder out = new StringBuilder();
        return out;
    }

    default StringBuilder renderBeforeClassBodyEnd()
    {
        StringBuilder out = new StringBuilder();
        return out;
    }

    default StringBuilder renderClassBody()
    {
        StringBuilder out = new StringBuilder();
        out.append("{\n");
        out.append(renderAfterClassEntry());
        out.append(renderFields());
        out.append(renderConstructor());
        out.append(renderMethods());
        out.append(renderBeforeClassBodyEnd());
        out.append("}\n");

        return out;
    }

    default StringBuilder renderInterfaces()
    {
        StringBuilder out = new StringBuilder();
        Set<String> ints = new HashSet<>(interfaces());
        List<NgInterface> interfacs = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgInterface.class);
        for (NgInterface interfac : interfacs)
        {
            if (interfac.onSelf())
            {
                ints.add(interfac.value());
            }
        }

        if (!ints.isEmpty())
        {
            StringBuilder sbInterfaces = new StringBuilder();
            sbInterfaces.append(" implements ");
            for (String interf : ints)
            {
                sbInterfaces.append(interf)
                        .append(",");
            }
            sbInterfaces.deleteCharAt(sbInterfaces.length() - 1);
            out.append(sbInterfaces);
        }
        return out;
    }

    default StringBuilder renderFields()
    {
        StringBuilder out = new StringBuilder();
        Set<String> fStrings = new LinkedHashSet<>();
        List<NgField> fAnno = getAllFields();
        for (NgField ngField : fAnno)
        {
            fStrings.add(ngField.value());
        }
        for (String field : fStrings.stream()
                .distinct()
                .toList())
        {
            if (Strings.isNullOrEmpty(field))
            {
                continue;
            }
            if (field.endsWith("\n"))
            {
                field = field.substring(0, field.length() - 1);
            }
            if (!field.endsWith(";"))
            {
                field += ";";
            }
            out.append("\t")
                    .append(field)
                    .append("\n");
        }

        //check for any fields on the component references
        var refs = AnnotationUtils.getAnnotation(getClass(), NgComponentReference.class);
        if (refs != null)
        {
            for (NgComponentReference ref : refs)
            {
                Class<?> refClass = ref.value();
                if (INgProvider.class.isAssignableFrom(refClass))
                {
                    var fields = AnnotationUtils.getAnnotation(refClass, NgField.class);
                    for (NgField field : fields)
                    {
                        if (field.onParent())
                        {

                        }
                    }

                    //check for fields with onParent

                }
            }

        }

        //ng output event emitter
        return out;
    }

    default StringBuilder renderConstructorParameters()
    {
        StringBuilder out = new StringBuilder();
        List<NgConstructorParameter> allParameters = getAllConstructorParameters();
        Set<String> constructorParameters = new LinkedHashSet<>();
        for (NgConstructorParameter allParameter : allParameters)
        {
            constructorParameters.add(allParameter.value());
        }

        if (!constructorParameters.isEmpty())
        {
            for (String constructorParameter : constructorParameters)
            {
                String param = constructorParameter.trim();
                if (!param.endsWith(","))
                {
                    param += ",";
                }
                param += " ";
                out.append(param);
            }
            if (out.length() > 1)
            {
                out.deleteCharAt(out.lastIndexOf(", "));
            }
        }
        return out;
    }

    default StringBuilder renderConstructorBody()
    {
        StringBuilder out = new StringBuilder();
        List<NgConstructorBody> allConstructorBodies = getAllConstructorBodies();
        Set<String> constructorBodies = new LinkedHashSet<>();
        for (NgConstructorBody allConstructorBody : allConstructorBodies)
        {
            constructorBodies.add(allConstructorBody.value()
                    .trim());
        }

        for (String constructorBody : constructorBodies)
        {
            out.append("\t")
                    .append(constructorBody)
                    .append("\n");
        }


        return out;
    }

    default StringBuilder renderConstructor()
    {
        StringBuilder out = new StringBuilder();

        String constructorParametersString = renderConstructorParameters().toString();
        String constructorBodyString = renderConstructorBody().toString();

        if (!Strings.isNullOrEmpty(constructorParametersString.toString()) || !Strings.isNullOrEmpty(constructorBodyString))
        {
            out.append("\tconstructor( ");
            out.append(constructorParametersString);
            out.append(")\n");

            out.append("\t{\n");
            out.append(constructorBodyString);
            out.append("\t}\n");
        }

        return out;
    }

    default StringBuilder renderMethods()
    {
        StringBuilder out = new StringBuilder();
        List<NgMethod> allMethods = renderAllMethods();
        allMethods = new ArrayList<>(new HashSet<>(allMethods));
        Set<String> methodStrings = new LinkedHashSet<>();
        Set<OnGetAllMethods> interceptors = IGuiceContext.loaderToSet(ServiceLoader.load(OnGetAllMethods.class));
        for (OnGetAllMethods interceptor : interceptors)
        {
            interceptor.perform(allMethods, this);
        }
        for (NgMethod allMethod : allMethods)
        {
            methodStrings.add(allMethod.value());
        }
        for (String methods : methodStrings)
        {
            out.append(methods)
                    .append("\n");
        }
        return out;
    }

    default List<String> constructorParameters()
    {
        List<String> parms = new ArrayList<>();
        return parms;
    }

    default List<Class<? extends NgDataType>> types()
    {
        return new ArrayList<>();
    }

    default List<String> constructorBody()
    {
        return new ArrayList<>();
    }

    default List<String> componentMethods()
    {
        List<String> list = new ArrayList<>();
        list.add(renderOnInitMethod());
        list.add(renderOnDestroyMethod());
        return list;
    }


    default String renderOnInitMethod()
    {
        StringBuilder out = new StringBuilder();
        return out.toString();
    }

    default String renderOnDestroyMethod()
    {
        StringBuilder out = new StringBuilder();
        return out.toString();
    }

    //***********************************************************
    // The lifecycle of angular objects
    //***********************************************************

    default List<String> onInit()
    {
        return new ArrayList<>();
    }

    default List<String> onDestroy()
    {
        return new ArrayList<>();
    }

    default List<String> methods()
    {
        return new ArrayList<>();
    }

    default List<String> globalFields()
    {
        return new ArrayList<>();
    }

    default List<String> fields()
    {
        return new ArrayList<>();
    }

    default String ofType()
    {
        return "";
    }

    default List<String> interfaces()
    {
        return new ArrayList<>();
    }

    default List<String> decorators()
    {
        return new ArrayList<>();
    }

}
