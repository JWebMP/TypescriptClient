package com.jwebmp.core.base.angular.client.annotations.angular;

import com.jwebmp.core.base.angular.client.services.interfaces.INgDataType;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Marks a class as an Angular REST client service for a single endpoint.
 * Each annotated class generates a standalone injectable service that communicates
 * with one specific endpoint using the specified HTTP method.
 * <p>
 * The generated service provides:
 * <ul>
 *   <li>A writable signal for the result data</li>
 *   <li>Loading / error / success state signals</li>
 *   <li>Optional polling at a configurable interval</li>
 *   <li>Optional caching with configurable TTL</li>
 *   <li>Request deduplication (prevents duplicate in-flight requests)</li>
 *   <li>Deep-merge support for incoming data</li>
 *   <li>Retry with configurable attempts and delay</li>
 * </ul>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgRestClient
{
    /**
     * The URL / path of the REST endpoint (relative or absolute).
     *
     * @return the endpoint URL
     */
    String url();

    /**
     * The HTTP method to use for the request.
     *
     * @return the HTTP method (GET, POST, PUT, DELETE, PATCH)
     */
    HttpMethod method() default HttpMethod.GET;

    /**
     * A friendly name for this client service – used as the base for the generated
     * TypeScript class name when not derived from the Java class name.
     *
     * @return the service name
     */
    String value() default "";

    // ── Response type ──────────────────────────────────────────────────

    /**
     * The data-type class that represents the response body.
     * Defaults to {@code Object} / {@code any} when not specified.
     *
     * @return the response data type
     */
    Class<? extends INgDataType> responseType() default INgDataType.class;

    /**
     * Whether the response is an array of {@link #responseType()}.
     *
     * @return true if the response is an array
     */
    boolean responseArray() default false;

    // ── Provided-in ────────────────────────────────────────────────────

    /**
     * Whether the service is a singleton ({@code providedIn: 'root'})
     * or transient ({@code providedIn: 'any'}).
     *
     * @return true for singleton
     */
    boolean singleton() default true;

    // ── Fetch on create ────────────────────────────────────────────────

    /**
     * Whether to automatically fire the request when the service is first injected.
     *
     * @return true to fetch on creation
     */
    boolean fetchOnCreate() default false;

    // ── Polling ────────────────────────────────────────────────────────

    /**
     * Enable polling – the service will re-issue the request at a fixed interval.
     *
     * @return true to enable polling
     */
    boolean pollingEnabled() default false;

    /**
     * Polling interval in milliseconds. Only used when {@link #pollingEnabled()} is true.
     *
     * @return interval in ms
     */
    int pollingIntervalMs() default 30_000;

    // ── Caching ────────────────────────────────────────────────────────

    /**
     * Enable client-side caching of the last successful response.
     *
     * @return true to enable caching
     */
    boolean cachingEnabled() default false;

    /**
     * Cache time-to-live in milliseconds. A cached response older than this
     * value is considered stale and a fresh request will be made.
     *
     * @return TTL in ms
     */
    int cacheTtlMs() default 60_000;

    // ── Deduplication ──────────────────────────────────────────────────

    /**
     * Enable request deduplication – if a request is already in-flight,
     * subsequent calls will share the same response observable instead of
     * firing a new HTTP request.
     *
     * @return true to enable deduplication
     */
    boolean deduplication() default true;

    // ── Deep merge ─────────────────────────────────────────────────────

    /**
     * When enabled, incoming data is deep-merged into the current signal
     * value instead of replacing it outright. Useful for partial updates.
     *
     * @return true to enable deep merge
     */
    boolean deepMerge() default false;

    // ── Retry ──────────────────────────────────────────────────────────

    /**
     * Number of retry attempts on failure (0 = no retries).
     *
     * @return retry count
     */
    int retryCount() default 0;

    /**
     * Delay between retries in milliseconds.
     *
     * @return delay in ms
     */
    int retryDelayMs() default 1_000;

    // ── Authentication ─────────────────────────────────────────────────

    /**
     * The type of authentication to apply to outgoing requests.
     * <ul>
     *   <li>{@code NONE} – no authentication header (default)</li>
     *   <li>{@code BEARER} – adds {@code Authorization: Bearer <token>}</li>
     *   <li>{@code BASIC} – adds {@code Authorization: Basic <token>}</li>
     *   <li>{@code CUSTOM} – adds a custom header whose name is given by
     *       {@link #authHeaderName()} and value is read from
     *       {@link #authTokenField()}</li>
     * </ul>
     *
     * @return the authentication type
     */
    AuthType authType() default AuthType.NONE;

    /**
     * The name of a field / method / signal on the service (or a string
     * expression) that resolves to the authentication token at runtime.
     * <p>
     * Typical values:
     * <ul>
     *   <li>{@code "localStorage.getItem('token')"}</li>
     *   <li>{@code "this.authService.token()"} – reading a signal from an
     *       injected service</li>
     * </ul>
     * Only used when {@link #authType()} is not {@code NONE}.
     *
     * @return a TypeScript expression that evaluates to the token string
     */
    String authTokenField() default "localStorage.getItem('token')";

    /**
     * The header name used when {@link #authType()} is {@code CUSTOM}.
     * Ignored for {@code BEARER} and {@code BASIC} (which always use
     * {@code Authorization}).
     *
     * @return custom authentication header name
     */
    String authHeaderName() default "Authorization";

    /**
     * HTTP method enum for the REST client.
     */
    enum HttpMethod
    {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH
    }

    /**
     * Authentication type enum for the REST client.
     */
    enum AuthType
    {
        /**
         * No authentication header.
         */
        NONE,
        /**
         * Bearer token – adds {@code Authorization: Bearer <token>}.
         */
        BEARER,
        /**
         * Basic auth – adds {@code Authorization: Basic <token>}.
         */
        BASIC,
        /**
         * Custom header – uses {@link NgRestClient#authHeaderName()} as the
         * header name and {@link NgRestClient#authTokenField()} as the value.
         */
        CUSTOM
    }
}


