package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Defines a static query parameter that is appended to every request URL
 * for the annotated {@link NgRestClient} service.
 * <p>
 * This annotation is repeatable – add multiple to set several default query parameters.
 * <p>
 * Example:
 * <pre>
 * {@literal @}NgRestClient(url = "/api/data")
 * {@literal @}NgRestClientQueryParam(name = "format", value = "json")
 * {@literal @}NgRestClientQueryParam(name = "version", value = "2")
 * public class DataClient implements INgRestClient&lt;DataClient&gt; {}
 * </pre>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@Repeatable(NgRestClientQueryParams.class)
public @interface NgRestClientQueryParam
{
    /**
     * The query parameter name.
     *
     * @return the parameter name
     */
    String name();

    /**
     * The query parameter value.
     *
     * @return the parameter value
     */
    String value();
}

