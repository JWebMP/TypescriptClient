package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.*;
import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;

import java.util.*;

import static com.jwebmp.core.base.angular.client.services.AnnotationsMap.*;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.*;

/**
 * Override and use the fields method to render the function body, and return statement
 *
 * @param <J>
 */
@NgDataType(exports = true, value = NgDataType.DataTypeClass.Const)
@NgImportReference(value = "AbstractControl, ValidationErrors, ValidatorFn,NG_VALIDATORS,Validator, FormGroup", reference = "@angular/forms")

public interface INgFormControlValidatorFunction<J extends INgFormControlValidatorFunction<J>> extends INgDataType<J>
{
    @Override
    default String ofType()
    {
        return "ValidatorFn = (control: AbstractControl): ValidationErrors | null =>";
    }

    @Override
    default StringBuilder renderFields()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                const value = control.value;
                        if (!value) {
                            return null;
                        }""");
        sb.append(INgDataType.super.renderFields());
        return sb;
    }


    @Override
    default boolean exportsClass()
    {
        return true;
    }

    default StringBuilder renderClassDefinition()
    {
        StringBuilder out = new StringBuilder();
        out.append(exportsClass() ? "export " : "");

        List<NgDataType> cType = IGuiceContext.get(AnnotationHelper.class)
                                              .getAnnotationFromClass(getClass(), NgDataType.class);
        if (!cType.isEmpty())
        {
            out.append(cType.get(0)
                            .value()
                            .description())
               .append(" ");
            String functionName = cType.get(0)
                                       .name();
            if (!Strings.isNullOrEmpty(functionName))
            {
                out.append(" " + functionName + " ");
            }
            else if (!Strings.isNullOrEmpty(cType.get(0)
                                                 .returnType()))
            {
                out.append(" " + getTsFilename(getClass()) + " ");
                out.append("() :  ")
                   .append(cType.get(0)
                                .returnType());
            }
        }
        out.append(getTsFilename(getClass()));
        if (!Strings.isNullOrEmpty(ofType()))
        {
            out.append(" : ")
               .append(ofType());
        }

        out.append(renderInterfaces());

        out.append("\n");
        out.append(renderClassBody());
        return out;
    }

}
