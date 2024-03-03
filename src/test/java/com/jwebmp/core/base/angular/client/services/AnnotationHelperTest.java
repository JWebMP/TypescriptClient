package com.jwebmp.core.base.angular.client.services;

import org.junit.jupiter.api.Test;

class AnnotationHelperTest
{

    @Test
    public void testAnnotationReader()
    {
        AnnotationHelper annotationHelper = new AnnotationHelper();
        annotationHelper.scanClass(AnnotationTestClass.class);
        ClassAnnotationMapping classMappings = annotationHelper.getClassMappings(AnnotationTestClass.class);
        System.out.println(classMappings);

    }
}