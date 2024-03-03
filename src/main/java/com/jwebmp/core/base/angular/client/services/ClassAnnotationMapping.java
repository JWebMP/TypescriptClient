package com.jwebmp.core.base.angular.client.services;

import com.google.common.base.Strings;
import lombok.*;
import lombok.extern.java.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@EqualsAndHashCode(of = {"classKey"})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Log
public class ClassAnnotationMapping
{
    private Class<?> classKey;
    private List<Annotation> annotations = new ArrayList<>();
    private Map<Class<? extends Annotation>, List<Annotation>> lookup = new HashMap<>();

    @SneakyThrows
    public void addLookup(Class<? extends Annotation> key, Annotation lookup)
    {
        if (!this.lookup.containsKey(key))
        {
            this.lookup.put(key, new ArrayList<>());
        }
        List<Annotation> annotationsLists = this.lookup.get(key);
        try
        {
            Method lookupValue = lookup.getClass()
                                       .getMethod("value");
            Object lookupInvoke = lookupValue.invoke(lookup);
            if (lookupInvoke != null && !Strings.isNullOrEmpty(lookupInvoke.toString()))
            {
                String lookupString = lookupInvoke.toString();
                boolean exists = false;
                for (Annotation annotationsList : annotationsLists)
                {
                    Method value = annotationsList.getClass()
                                                  .getMethod("value");
                    Object invoke = value.invoke(annotationsList);
                    if (invoke != null)
                    {
                        String val = invoke.toString();
                        if (!Strings.isNullOrEmpty(val))
                        {
                            if (lookupString.equals(val))
                            {
                                exists = true;
                            }
                        }
                    }
                }
                if (!exists)
                {
                    annotationsLists
                            .add(lookup);
                }
                else
                {
                    log.finer("Ignoring duplicate, perhaps inherited annotation");
                }
            }
        }
        catch (NoSuchMethodException nsme)
        {
            log.log(Level.WARNING, "Cannot read value method of annotation - " + key);
            annotationsLists.add(lookup);
        }
    }

}
