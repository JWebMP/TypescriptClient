package com.jwebmp.core.base.angular.client.services;

import lombok.*;
import lombok.extern.java.Log;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        annotationsLists
                .add(lookup);
    }

}
