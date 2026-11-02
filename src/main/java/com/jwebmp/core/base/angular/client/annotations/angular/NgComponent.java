package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgComponent
{
    /**
     * The selector going to be used
     *
     * @return
     */
    String value();

    String providedIn() default "";

    /**
     * @return If the component must render as a standalone component
     */
    boolean standalone() default true;

    /** Change detection for the generated component; EAGER preserves pre-Angular 22 behavior. */
    ChangeDetection changeDetection() default ChangeDetection.EAGER;

    enum ChangeDetection
    {
        EAGER("Eager"), ON_PUSH("OnPush");

        private final String angularName;

        ChangeDetection(String angularName)
        {
            this.angularName = angularName;
        }

        public String angularName()
        {
            return angularName;
        }
    }
}
