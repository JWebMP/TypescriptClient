package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Defines a static HTTP header that is sent with every request from the
 * annotated {@link NgRestClient} service.
 * <p>
 * This annotation is repeatable – add multiple to set several default headers.
 * <p>
 * Example:
 * <pre>
 * {@literal @}NgRestClient(url = "/api/data")
 * {@literal @}NgRestClientHeader(name = "Accept", value = "application/json")
 * {@literal @}NgRestClientHeader(name = "X-Custom-Header", value = "my-value")
 * public class DataClient implements INgRestClient&lt;DataClient&gt; {}
 * </pre>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@Repeatable(NgRestClientHeaders.class)
public @interface NgRestClientHeader
{
    /**
     * The header name (e.g. {@code "Content-Type"}, {@code "Accept"}).
     *
     * @return the header name
     */
    String name();

    /**
     * The header value (e.g. {@code "application/json"}).
     *
     * @return the header value
     */
    String value();
}

