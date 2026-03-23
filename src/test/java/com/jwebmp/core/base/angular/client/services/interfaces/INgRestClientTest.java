package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.NgRestClient;
import com.jwebmp.core.base.angular.client.annotations.angular.NgRestClientHeader;
import com.jwebmp.core.base.angular.client.annotations.angular.NgRestClientQueryParam;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the INgRestClient interface to ensure TypeScript generation
 * produces correct output for various annotation configurations.
 */
public class INgRestClientTest
{

    // ── Test fixtures ──────────────────────────────────────────────────

    /**
     * Minimal GET client – defaults for everything except url.
     */
    @NgRestClient(url = "/api/users")
    static class SimpleGetClient implements INgRestClient<SimpleGetClient>
    {
    }

    /**
     * POST client with body support, caching, retry, and deduplication.
     */
    @NgRestClient(
            url = "/api/orders",
            method = NgRestClient.HttpMethod.POST,
            singleton = true,
            fetchOnCreate = false,
            cachingEnabled = true,
            cacheTtlMs = 45_000,
            deduplication = true,
            retryCount = 3,
            retryDelayMs = 2_000
    )
    static class PostOrderClient implements INgRestClient<PostOrderClient>
    {
    }

    /**
     * GET client with polling, deep merge, and array response.
     */
    @NgRestClient(
            url = "/api/notifications",
            method = NgRestClient.HttpMethod.GET,
            responseArray = true,
            pollingEnabled = true,
            pollingIntervalMs = 10_000,
            deepMerge = true,
            deduplication = false,
            fetchOnCreate = true
    )
    static class PollingNotificationsClient implements INgRestClient<PollingNotificationsClient>
    {
    }

    /**
     * PUT client – non-singleton, no dedup, no cache.
     */
    @NgRestClient(
            url = "/api/profiles/{id}",
            method = NgRestClient.HttpMethod.PUT,
            singleton = false,
            deduplication = false
    )
    static class PutProfileClient implements INgRestClient<PutProfileClient>
    {
    }

    /**
     * DELETE client – minimal config.
     */
    @NgRestClient(
            url = "/api/items/{id}",
            method = NgRestClient.HttpMethod.DELETE,
            singleton = true
    )
    static class DeleteItemClient implements INgRestClient<DeleteItemClient>
    {
    }

    /**
     * PATCH client with all features enabled.
     */
    @NgRestClient(
            url = "/api/settings",
            method = NgRestClient.HttpMethod.PATCH,
            singleton = true,
            fetchOnCreate = true,
            pollingEnabled = true,
            pollingIntervalMs = 60_000,
            cachingEnabled = true,
            cacheTtlMs = 120_000,
            deduplication = true,
            deepMerge = true,
            retryCount = 5,
            retryDelayMs = 500
    )
    static class PatchSettingsClient implements INgRestClient<PatchSettingsClient>
    {
    }

    /**
     * GET client with static headers.
     */
    @NgRestClient(url = "/api/data")
    @NgRestClientHeader(name = "Accept", value = "application/json")
    @NgRestClientHeader(name = "X-Custom-Header", value = "my-value")
    static class HeadersClient implements INgRestClient<HeadersClient>
    {
    }

    /**
     * GET client with Bearer auth.
     */
    @NgRestClient(
            url = "/api/protected",
            authType = NgRestClient.AuthType.BEARER,
            authTokenField = "localStorage.getItem('access_token')"
    )
    static class BearerAuthClient implements INgRestClient<BearerAuthClient>
    {
    }

    /**
     * GET client with Basic auth.
     */
    @NgRestClient(
            url = "/api/basic",
            authType = NgRestClient.AuthType.BASIC,
            authTokenField = "btoa('user:pass')"
    )
    static class BasicAuthClient implements INgRestClient<BasicAuthClient>
    {
    }

    /**
     * GET client with Custom auth header.
     */
    @NgRestClient(
            url = "/api/custom-auth",
            authType = NgRestClient.AuthType.CUSTOM,
            authTokenField = "this.apiKey",
            authHeaderName = "X-API-Key"
    )
    static class CustomAuthClient implements INgRestClient<CustomAuthClient>
    {
    }

    /**
     * GET client with no auth (explicit).
     */
    @NgRestClient(
            url = "/api/public",
            authType = NgRestClient.AuthType.NONE
    )
    static class NoAuthClient implements INgRestClient<NoAuthClient>
    {
    }

    /**
     * GET client with default query params.
     */
    @NgRestClient(url = "/api/search")
    @NgRestClientQueryParam(name = "format", value = "json")
    @NgRestClientQueryParam(name = "version", value = "2")
    static class QueryParamsClient implements INgRestClient<QueryParamsClient>
    {
    }

    /**
     * POST client with headers + auth + query params combined.
     */
    @NgRestClient(
            url = "/api/combined",
            method = NgRestClient.HttpMethod.POST,
            authType = NgRestClient.AuthType.BEARER,
            authTokenField = "this.authService.token()"
    )
    @NgRestClientHeader(name = "Content-Type", value = "application/json")
    @NgRestClientQueryParam(name = "tenant", value = "default")
    static class CombinedClient implements INgRestClient<CombinedClient>
    {
    }

    // ═══════════════════════════════════════════════════════════════════
    // ANNOTATION ACCESSOR
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testAnnotationAccessor()
    {
        SimpleGetClient client = new SimpleGetClient();
        NgRestClient annotation = client.getAnnotation();
        assertNotNull(annotation, "Annotation should be retrievable");
        assertEquals("/api/users", annotation.url());
        assertEquals(NgRestClient.HttpMethod.GET, annotation.method());
        assertTrue(annotation.singleton());
    }

    // ═══════════════════════════════════════════════════════════════════
    // DECORATORS
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testDecorators_Singleton()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> decorators = client.decorators();
        assertTrue(decorators.stream().anyMatch(d -> d.contains("providedIn: 'root'")),
                "Singleton should produce providedIn: 'root'");
    }

    @Test
    void testDecorators_NonSingleton()
    {
        PutProfileClient client = new PutProfileClient();
        List<String> decorators = client.decorators();
        assertTrue(decorators.stream().anyMatch(d -> d.contains("providedIn: 'any'")),
                "Non-singleton should produce providedIn: 'any'");
    }

    // ═══════════════════════════════════════════════════════════════════
    // INTERFACES
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testInterfaces_IncludesOnDestroy()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> interfaces = client.interfaces();
        assertTrue(interfaces.contains("OnDestroy"), "Should implement OnDestroy");
    }

    // ═══════════════════════════════════════════════════════════════════
    // FIELDS – Simple GET
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testFields_SimpleGet_ContainsCoreFields()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        assertTrue(joined.contains("inject(HttpClient)"), "Should inject HttpClient");
        assertTrue(joined.contains("inject(DestroyRef)"), "Should inject DestroyRef");
        assertTrue(joined.contains("data: WritableSignal<any | undefined>"), "Should have data signal");
        assertTrue(joined.contains("loading: WritableSignal<boolean>"), "Should have loading signal");
        assertTrue(joined.contains("error: WritableSignal<any>"), "Should have error signal");
        assertTrue(joined.contains("success: WritableSignal<boolean>"), "Should have success signal");
        assertTrue(joined.contains("endpointUrl = '/api/users'"), "Should have endpoint URL");
        assertTrue(joined.contains("destroy$ = new Subject<void>()"), "Should have destroy subject");
    }

    @Test
    void testFields_SimpleGet_HasDeduplication()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        // deduplication defaults to true
        assertTrue(joined.contains("inflightRequest$"), "Should have inflight request field (dedup enabled by default)");
    }

    @Test
    void testFields_SimpleGet_NoCaching()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        assertFalse(joined.contains("cacheTimestamp"), "Should NOT have cache fields when caching disabled");
        assertFalse(joined.contains("cacheTtlMs"), "Should NOT have cacheTtlMs when caching disabled");
    }

    @Test
    void testFields_SimpleGet_PollingFieldsAlwaysPresent()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        assertTrue(joined.contains("pollingSubscription"), "Should always have polling fields for on-demand polling");
        assertTrue(joined.contains("pollingIntervalMs"), "Should always have pollingIntervalMs for on-demand polling");
    }

    // ═══════════════════════════════════════════════════════════════════
    // FIELDS – POST with caching
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testFields_PostOrder_HasCachingFields()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        assertTrue(joined.contains("cacheTimestamp"), "Should have cacheTimestamp when caching enabled");
        assertTrue(joined.contains("cacheTtlMs = 45000"), "Should have correct cacheTtlMs");
    }

    // ═══════════════════════════════════════════════════════════════════
    // FIELDS – Polling with array response
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testFields_Polling_HasPollingFields()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        assertTrue(joined.contains("pollingSubscription"), "Should have pollingSubscription when polling enabled");
        assertTrue(joined.contains("pollingIntervalMs = 10000"), "Should have correct pollingIntervalMs");
    }

    @Test
    void testFields_ArrayResponse_CorrectSignalType()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        // For array response, type should be any[]
        assertTrue(joined.contains("WritableSignal<any[]>"), "Array response should use any[] signal type");
        // Default value for array should be []
        assertTrue(joined.contains("signal<any[]>([])"), "Array response signal default should be []");
    }

    @Test
    void testFields_Polling_NoDeduplication()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        assertFalse(joined.contains("inflightRequest$"), "Should NOT have inflight request when dedup disabled");
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONSTRUCTOR BODY
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testConstructorBody_AlwaysHasDestroyRefCleanup()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> body = client.constructorBody();
        String joined = String.join("\n", body);

        assertTrue(joined.contains("this.destroyRef.onDestroy"), "Should register destroyRef cleanup");
        assertTrue(joined.contains("this.destroy$.next()"), "Should call destroy$.next() in cleanup");
        assertTrue(joined.contains("this.destroy$.complete()"), "Should call destroy$.complete() in cleanup");
    }

    @Test
    void testConstructorBody_NoFetchOnCreate_WhenDisabled()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> body = client.constructorBody();
        String joined = String.join("\n", body);

        assertFalse(joined.contains("this.execute()"), "Should NOT auto-execute when fetchOnCreate is false");
    }

    @Test
    void testConstructorBody_FetchOnCreate_WhenEnabled()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> body = client.constructorBody();
        String joined = String.join("\n", body);

        assertTrue(joined.contains("this.execute()"), "Should auto-execute when fetchOnCreate is true");
    }

    @Test
    void testConstructorBody_StartPolling_WhenEnabled()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> body = client.constructorBody();
        String joined = String.join("\n", body);

        assertTrue(joined.contains("this.startPolling()"), "Should start polling when polling enabled");
    }

    @Test
    void testConstructorBody_NoPolling_WhenDisabled()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> body = client.constructorBody();
        String joined = String.join("\n", body);

        assertFalse(joined.contains("this.startPolling()"), "Should NOT start polling when polling disabled");
    }

    // ═══════════════════════════════════════════════════════════════════
    // METHODS – Simple GET
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_SimpleGet_HasExecute()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("execute(params?: Record<string, string>, extraHeaders?: Record<string, string>): void"),
                "Should have execute method with params and extraHeaders");
    }

    @Test
    void testMethods_SimpleGet_NoExecuteWithBody()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertFalse(joined.contains("executeWithBody"),
                "GET client should NOT have executeWithBody");
    }

    @Test
    void testMethods_SimpleGet_HasBuildHttpRequest()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("buildHttpRequest$"),
                "Should have buildHttpRequest$ method");
        assertTrue(joined.contains("this.http.get<"),
                "GET client should use http.get");
    }

    @Test
    void testMethods_SimpleGet_HasHandleResponse()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("handleResponse"),
                "Should have handleResponse method");
        assertTrue(joined.contains("this.data.set(response)"),
                "handleResponse should set data signal");
        assertTrue(joined.contains("this.loading.set(false)"),
                "handleResponse should set loading to false");
        assertTrue(joined.contains("this.success.set(true)"),
                "handleResponse should set success to true");
    }

    @Test
    void testMethods_SimpleGet_HasReset()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("reset(): void"),
                "Should have reset method");
        assertTrue(joined.contains("this.data.set(undefined)"),
                "reset should set data to undefined for non-array");
    }

    @Test
    void testMethods_SimpleGet_HasNgOnDestroy()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("ngOnDestroy(): void"),
                "Should have ngOnDestroy method");
        assertTrue(joined.contains("this.destroy$.next()"),
                "ngOnDestroy should call destroy$.next()");
        assertTrue(joined.contains("this.destroy$.complete()"),
                "ngOnDestroy should call destroy$.complete()");
    }

    @Test
    void testMethods_SimpleGet_NoDeepMergeMethods()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertFalse(joined.contains("deepMergeInto"),
                "Should NOT have deepMerge methods when deepMerge disabled");
        assertFalse(joined.contains("mergeArraysInPlace"),
                "Should NOT have mergeArraysInPlace when deepMerge disabled");
    }

    @Test
    void testMethods_SimpleGet_NoCacheMethods()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertFalse(joined.contains("isCacheValid"),
                "Should NOT have isCacheValid when caching disabled");
        assertFalse(joined.contains("invalidateCache"),
                "Should NOT have invalidateCache when caching disabled");
    }

    @Test
    void testMethods_SimpleGet_PollingMethodsAlwaysPresent()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("startPolling"),
                "Should always have startPolling for on-demand polling");
        assertTrue(joined.contains("stopPolling"),
                "Should always have stopPolling for on-demand polling");
    }

    @Test
    void testMethods_SimpleGet_HasDeduplication()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("inflightRequest$"),
                "Execute should check inflight when dedup enabled");
        assertTrue(joined.contains("shareReplay(1)"),
                "Should use shareReplay for dedup");
    }

    // ═══════════════════════════════════════════════════════════════════
    // METHODS – POST client
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_Post_HasExecuteWithBody()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("executeWithBody(body: any, params?: Record<string, string>, extraHeaders?: Record<string, string>): void"),
                "POST client should have executeWithBody method");
    }

    @Test
    void testMethods_Post_UsesHttpPost()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("this.http.post<"),
                "POST client should use http.post");
    }

    @Test
    void testMethods_Post_HasRetry()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("retry({ count: 3, delay: 2000 })"),
                "Should have retry with correct count and delay");
    }

    @Test
    void testMethods_Post_HasCacheMethods()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("isCacheValid"),
                "Should have isCacheValid when caching enabled");
        assertTrue(joined.contains("invalidateCache"),
                "Should have invalidateCache when caching enabled");
    }

    @Test
    void testMethods_Post_ExecuteChecksCacheFirst()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        // The execute method should contain cache check
        String executeMethod = methods.stream()
                                      .filter(m -> m.startsWith("execute(params"))
                                      .findFirst()
                                      .orElse("");
        assertTrue(executeMethod.contains("this.isCacheValid()"),
                "execute() should check cache validity when caching enabled");
    }

    @Test
    void testMethods_Post_HandleResponse_SetsCacheTimestamp()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("this.cacheTimestamp = Date.now()"),
                "handleResponse should set cacheTimestamp when caching enabled");
    }

    // ═══════════════════════════════════════════════════════════════════
    // METHODS – Polling + deep merge client
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_Polling_HasPollingMethods()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("startPolling(intervalMs?: number): void"),
                "Should have startPolling method");
        assertTrue(joined.contains("stopPolling(): void"),
                "Should have stopPolling method");
        assertTrue(joined.contains("timer(0, this.pollingIntervalMs)"),
                "startPolling should use timer");
    }

    @Test
    void testMethods_Polling_NgOnDestroy_StopsPolling()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> methods = client.methods();

        String destroyMethod = methods.stream()
                                      .filter(m -> m.contains("ngOnDestroy"))
                                      .findFirst()
                                      .orElse("");
        assertTrue(destroyMethod.contains("this.stopPolling()"),
                "ngOnDestroy should stop polling when polling enabled");
    }

    @Test
    void testMethods_DeepMerge_HasMergeMethods()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("deepMergeInto(target: any, source: any): boolean"),
                "Should have deepMergeInto when deepMerge enabled");
        assertTrue(joined.contains("mergeArraysInPlace(targetArr: any, sourceArr: any[]): boolean"),
                "Should have mergeArraysInPlace when deepMerge enabled");
    }

    @Test
    void testMethods_DeepMerge_HandleResponseUsesMerge()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("this.deepMergeInto(current, response)"),
                "handleResponse should use deepMergeInto when deepMerge enabled");
    }

    @Test
    void testMethods_ArrayResponse_ResetUsesEmptyArray()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("this.data.set([] as any)"),
                "reset should set data to [] for array response");
    }

    @Test
    void testMethods_NoDedup_NoInflightCheck()
    {
        PollingNotificationsClient client = new PollingNotificationsClient();
        List<String> methods = client.methods();

        String executeMethod = methods.stream()
                                      .filter(m -> m.startsWith("execute(params"))
                                      .findFirst()
                                      .orElse("");
        assertFalse(executeMethod.contains("inflightRequest$"),
                "execute() should NOT check inflight when dedup disabled");
        assertFalse(executeMethod.contains("shareReplay"),
                "Should NOT use shareReplay when dedup disabled");
    }

    // ═══════════════════════════════════════════════════════════════════
    // METHODS – PUT client
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_Put_HasExecuteWithBody()
    {
        PutProfileClient client = new PutProfileClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("executeWithBody"),
                "PUT client should have executeWithBody");
        assertTrue(joined.contains("this.http.put<"),
                "PUT client should use http.put");
    }

    @Test
    void testMethods_Put_BuildHttpRequest_AcceptsBody()
    {
        PutProfileClient client = new PutProfileClient();
        List<String> methods = client.methods();

        String buildMethod = methods.stream()
                                    .filter(m -> m.startsWith("private buildHttpRequest"))
                                    .findFirst()
                                    .orElse("");
        assertFalse(buildMethod.isEmpty(),
                "buildHttpRequest method definition should exist for PUT client");
        assertTrue(buildMethod.contains("body"),
                "buildHttpRequest for PUT should reference body. Content:\n" + buildMethod);
    }

    // ═══════════════════════════════════════════════════════════════════
    // METHODS – DELETE client
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_Delete_UsesHttpDelete()
    {
        DeleteItemClient client = new DeleteItemClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("this.http.delete<"),
                "DELETE client should use http.delete");
    }

    @Test
    void testMethods_Delete_NoExecuteWithBody()
    {
        DeleteItemClient client = new DeleteItemClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertFalse(joined.contains("executeWithBody"),
                "DELETE client should NOT have executeWithBody");
    }

    @Test
    void testMethods_Delete_BuildHttpRequest_NoBodyParam()
    {
        DeleteItemClient client = new DeleteItemClient();
        List<String> methods = client.methods();

        String buildMethod = methods.stream()
                                    .filter(m -> m.startsWith("private buildHttpRequest"))
                                    .findFirst()
                                    .orElse("");
        assertFalse(buildMethod.isEmpty(),
                "buildHttpRequest method definition should exist for DELETE client");
        assertFalse(buildMethod.contains("body"),
                "buildHttpRequest for DELETE should NOT reference body");
    }

    // ═══════════════════════════════════════════════════════════════════
    // METHODS – PATCH client with all features
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_PatchAllFeatures_HasAllMethods()
    {
        PatchSettingsClient client = new PatchSettingsClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        // execute
        assertTrue(joined.contains("execute(params"), "Should have execute");
        // executeWithBody (PATCH has body)
        assertTrue(joined.contains("executeWithBody"), "PATCH should have executeWithBody");
        // buildHttpRequest$
        assertTrue(joined.contains("buildHttpRequest$"), "Should have buildHttpRequest$");
        assertTrue(joined.contains("this.http.patch<"), "PATCH should use http.patch");
        // handleResponse
        assertTrue(joined.contains("handleResponse"), "Should have handleResponse");
        // deep merge
        assertTrue(joined.contains("deepMergeInto"), "Should have deepMergeInto");
        assertTrue(joined.contains("mergeArraysInPlace"), "Should have mergeArraysInPlace");
        // polling
        assertTrue(joined.contains("startPolling"), "Should have startPolling");
        assertTrue(joined.contains("stopPolling"), "Should have stopPolling");
        // cache
        assertTrue(joined.contains("isCacheValid"), "Should have isCacheValid");
        assertTrue(joined.contains("invalidateCache"), "Should have invalidateCache");
        // retry
        assertTrue(joined.contains("retry({ count: 5, delay: 500 })"), "Should have retry with count 5, delay 500");
        // reset
        assertTrue(joined.contains("reset(): void"), "Should have reset");
        // ngOnDestroy
        assertTrue(joined.contains("ngOnDestroy(): void"), "Should have ngOnDestroy");
    }

    @Test
    void testFields_PatchAllFeatures_HasAllFields()
    {
        PatchSettingsClient client = new PatchSettingsClient();
        List<String> fields = client.fields();
        String joined = String.join("\n", fields);

        assertTrue(joined.contains("inject(HttpClient)"), "Should inject HttpClient");
        assertTrue(joined.contains("inject(DestroyRef)"), "Should inject DestroyRef");
        assertTrue(joined.contains("data: WritableSignal<"), "Should have data signal");
        assertTrue(joined.contains("loading: WritableSignal<boolean>"), "Should have loading");
        assertTrue(joined.contains("error: WritableSignal<any>"), "Should have error");
        assertTrue(joined.contains("success: WritableSignal<boolean>"), "Should have success");
        assertTrue(joined.contains("endpointUrl = '/api/settings'"), "Should have endpoint URL");
        assertTrue(joined.contains("destroy$"), "Should have destroy$");
        assertTrue(joined.contains("inflightRequest$"), "Should have inflight (dedup enabled)");
        assertTrue(joined.contains("cacheTimestamp"), "Should have cacheTimestamp (cache enabled)");
        assertTrue(joined.contains("cacheTtlMs = 120000"), "Should have correct cacheTtlMs");
        assertTrue(joined.contains("pollingSubscription"), "Should have pollingSubscription (polling enabled)");
        assertTrue(joined.contains("pollingIntervalMs = 60000"), "Should have correct pollingIntervalMs");
    }

    @Test
    void testConstructorBody_PatchAllFeatures_HasAll()
    {
        PatchSettingsClient client = new PatchSettingsClient();
        List<String> body = client.constructorBody();
        String joined = String.join("\n", body);

        assertTrue(joined.contains("this.destroyRef.onDestroy"), "Should register destroyRef cleanup");
        assertTrue(joined.contains("this.execute()"), "Should auto-execute (fetchOnCreate true)");
        assertTrue(joined.contains("this.startPolling()"), "Should start polling (polling enabled)");
    }

    // ═══════════════════════════════════════════════════════════════════
    // RENDER ON DESTROY – should be suppressed (rendered via methods())
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testRenderOnDestroyMethod_ReturnsEmpty()
    {
        SimpleGetClient client = new SimpleGetClient();
        assertEquals("", client.renderOnDestroyMethod(),
                "renderOnDestroyMethod should return empty (ngOnDestroy is rendered via methods())");
    }

    // ═══════════════════════════════════════════════════════════════════
    // EXECUTE METHOD – error logging includes correct method + URL
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_Execute_ErrorLogIncludesMethodAndUrl()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("[RestClient] Error on POST /api/orders:"),
                "Error log should include HTTP method and URL");
    }

    @Test
    void testMethods_Execute_ErrorLogIncludesMethodAndUrl_Get()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("[RestClient] Error on GET /api/users:"),
                "Error log should include HTTP method and URL");
    }

    // ═══════════════════════════════════════════════════════════════════
    // QUERY PARAMS SUPPORT
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_BuildHttpRequest_HandlesQueryParams()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();

        // Find the buildHttpRequest method definition (not invocations in other methods)
        String buildMethod = methods.stream()
                                    .filter(m -> m.startsWith("private buildHttpRequest"))
                                    .findFirst()
                                    .orElse("");

        assertFalse(buildMethod.isEmpty(),
                "buildHttpRequest method definition should exist in methods list");

        // The method should handle query params
        assertTrue(buildMethod.contains("let url = this.endpointUrl"),
                "Should reference endpointUrl. Content:\n" + buildMethod);
        assertTrue(buildMethod.contains("mergedParams"),
                "Should use mergedParams. Content:\n" + buildMethod);
        assertTrue(buildMethod.contains("queryString"),
                "Should build queryString. Content:\n" + buildMethod);
        assertTrue(buildMethod.contains("this.http.get<"),
                "Should use http.get for GET client. Content:\n" + buildMethod);
    }

    // ═══════════════════════════════════════════════════════════════════
    // RESET METHOD – dedup + cache cleanup
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_Reset_ClearsDedup()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();

        String resetMethod = methods.stream()
                                    .filter(m -> m.contains("reset(): void"))
                                    .findFirst()
                                    .orElse("");
        assertTrue(resetMethod.contains("this.inflightRequest$ = null"),
                "reset should clear inflight request when dedup enabled");
    }

    @Test
    void testMethods_Reset_ClearsCache()
    {
        PostOrderClient client = new PostOrderClient();
        List<String> methods = client.methods();

        String resetMethod = methods.stream()
                                    .filter(m -> m.contains("reset(): void"))
                                    .findFirst()
                                    .orElse("");
        assertTrue(resetMethod.contains("this.invalidateCache()"),
                "reset should invalidate cache when caching enabled");
    }

    @Test
    void testMethods_Reset_NoCacheCleanup_WhenDisabled()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();

        String resetMethod = methods.stream()
                                    .filter(m -> m.contains("reset(): void"))
                                    .findFirst()
                                    .orElse("");
        assertFalse(resetMethod.contains("invalidateCache"),
                "reset should NOT invalidate cache when caching disabled");
    }

    // ═══════════════════════════════════════════════════════════════════
    // NO RETRY when retryCount = 0
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_NoRetry_WhenCountIsZero()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();

        String buildMethod = methods.stream()
                                    .filter(m -> m.startsWith("private buildHttpRequest"))
                                    .findFirst()
                                    .orElse("");
        assertFalse(buildMethod.contains("retry("),
                "Should NOT have retry operator when retryCount is 0");
    }

    // ═══════════════════════════════════════════════════════════════════
    // HEADERS – static headers via @NgRestClientHeader
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_Headers_HasBuildHeadersMethod()
    {
        HeadersClient client = new HeadersClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("private buildHeaders(extraHeaders?: Record<string, string>): HttpHeaders"),
                "Should have buildHeaders method");
    }

    @Test
    void testMethods_Headers_SetsStaticHeaders()
    {
        HeadersClient client = new HeadersClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        assertTrue(headersMethod.contains("headers.set('Accept', 'application/json')"),
                "Should set Accept header. Content:\n" + headersMethod);
        assertTrue(headersMethod.contains("headers.set('X-Custom-Header', 'my-value')"),
                "Should set X-Custom-Header. Content:\n" + headersMethod);
    }

    @Test
    void testMethods_Headers_SupportsExtraHeadersOverride()
    {
        HeadersClient client = new HeadersClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        assertTrue(headersMethod.contains("if (extraHeaders)"),
                "buildHeaders should support runtime extra headers override");
        assertTrue(headersMethod.contains("Object.entries(extraHeaders)"),
                "Should iterate over extra headers entries");
    }

    @Test
    void testMethods_NoHeaders_StillHasBuildHeaders()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();
        String joined = String.join("\n", methods);

        assertTrue(joined.contains("buildHeaders"),
                "Even with no static headers, buildHeaders should exist (for extraHeaders support)");
    }

    @Test
    void testMethods_NoHeaders_DoesNotSetStaticHeaders()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        // Count occurrences of headers.set - should only be inside the extraHeaders block
        int setCallsBeforeExtraHeaders = headersMethod.indexOf("if (extraHeaders)");
        String beforeExtraHeaders = headersMethod.substring(0, Math.max(0, setCallsBeforeExtraHeaders));
        // Only the `new HttpHeaders()` line should be before extraHeaders; no static set() calls
        assertFalse(beforeExtraHeaders.contains("headers.set('"),
                "Should NOT have any static header set() calls when no @NgRestClientHeader");
    }

    // ═══════════════════════════════════════════════════════════════════
    // HTTP REQUEST uses headers
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_BuildHttpRequest_PassesHeadersToHttpCall()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();

        String buildMethod = methods.stream()
                                    .filter(m -> m.startsWith("private buildHttpRequest"))
                                    .findFirst()
                                    .orElse("");
        assertTrue(buildMethod.contains("this.buildHeaders(extraHeaders)"),
                "buildHttpRequest$ should call buildHeaders with extraHeaders");
        assertTrue(buildMethod.contains("{ headers }"),
                "HTTP call should pass headers option");
    }

    // ═══════════════════════════════════════════════════════════════════
    // AUTHENTICATION – Bearer
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_BearerAuth_SetsAuthorizationHeader()
    {
        BearerAuthClient client = new BearerAuthClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        assertTrue(headersMethod.contains("localStorage.getItem('access_token')"),
                "Should read token from configured authTokenField. Content:\n" + headersMethod);
        assertTrue(headersMethod.contains("'Authorization', 'Bearer ' + authToken"),
                "Should set Authorization header with Bearer prefix. Content:\n" + headersMethod);
    }

    @Test
    void testMethods_BearerAuth_ChecksTokenBeforeSetting()
    {
        BearerAuthClient client = new BearerAuthClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        assertTrue(headersMethod.contains("if (authToken)"),
                "Should guard auth header with null check");
    }

    // ═══════════════════════════════════════════════════════════════════
    // AUTHENTICATION – Basic
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_BasicAuth_SetsAuthorizationHeader()
    {
        BasicAuthClient client = new BasicAuthClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        assertTrue(headersMethod.contains("btoa('user:pass')"),
                "Should read token from configured authTokenField");
        assertTrue(headersMethod.contains("'Authorization', 'Basic ' + authToken"),
                "Should set Authorization header with Basic prefix");
    }

    // ═══════════════════════════════════════════════════════════════════
    // AUTHENTICATION – Custom
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_CustomAuth_SetsCustomHeader()
    {
        CustomAuthClient client = new CustomAuthClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        assertTrue(headersMethod.contains("this.apiKey"),
                "Should read token from configured authTokenField");
        assertTrue(headersMethod.contains("headers.set('X-API-Key', authToken)"),
                "Should set custom header name with token value");
    }

    // ═══════════════════════════════════════════════════════════════════
    // AUTHENTICATION – None
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_NoAuth_DoesNotSetAuthHeader()
    {
        NoAuthClient client = new NoAuthClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        assertFalse(headersMethod.contains("authToken"),
                "Should NOT have any auth token logic when authType is NONE");
        assertFalse(headersMethod.contains("Authorization"),
                "Should NOT reference Authorization when authType is NONE");
    }

    @Test
    void testMethods_DefaultAuth_IsNone()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");
        assertFalse(headersMethod.contains("authToken"),
                "Default auth type should be NONE – no auth token logic");
    }

    // ═══════════════════════════════════════════════════════════════════
    // QUERY PARAMS – @NgRestClientQueryParam
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_DefaultQueryParams_MergesIntoRequest()
    {
        QueryParamsClient client = new QueryParamsClient();
        List<String> methods = client.methods();

        String buildMethod = methods.stream()
                                    .filter(m -> m.startsWith("private buildHttpRequest"))
                                    .findFirst()
                                    .orElse("");
        assertTrue(buildMethod.contains("'format': 'json'"),
                "Should include default query param 'format'. Content:\n" + buildMethod);
        assertTrue(buildMethod.contains("'version': '2'"),
                "Should include default query param 'version'. Content:\n" + buildMethod);
        assertTrue(buildMethod.contains("{ ...defaultParams, ...params }"),
                "Should merge default params with runtime params (runtime wins)");
    }

    @Test
    void testMethods_NoDefaultQueryParams_NoMerge()
    {
        SimpleGetClient client = new SimpleGetClient();
        List<String> methods = client.methods();

        String buildMethod = methods.stream()
                                    .filter(m -> m.startsWith("private buildHttpRequest"))
                                    .findFirst()
                                    .orElse("");
        assertFalse(buildMethod.contains("defaultParams"),
                "Should NOT have defaultParams when no @NgRestClientQueryParam");
        assertTrue(buildMethod.contains("const mergedParams = params"),
                "Should pass params through directly when no defaults");
    }

    // ═══════════════════════════════════════════════════════════════════
    // COMBINED – headers + auth + query params
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testMethods_Combined_HasAllFeatures()
    {
        CombinedClient client = new CombinedClient();
        List<String> methods = client.methods();

        String headersMethod = methods.stream()
                                      .filter(m -> m.startsWith("private buildHeaders"))
                                      .findFirst()
                                      .orElse("");

        // Static header
        assertTrue(headersMethod.contains("headers.set('Content-Type', 'application/json')"),
                "Should set Content-Type header");

        // Bearer auth
        assertTrue(headersMethod.contains("this.authService.token()"),
                "Should read token from injected service");
        assertTrue(headersMethod.contains("'Authorization', 'Bearer ' + authToken"),
                "Should set Bearer auth header");

        // Extra headers support
        assertTrue(headersMethod.contains("if (extraHeaders)"),
                "Should support runtime extra headers");

        String buildMethod = methods.stream()
                                    .filter(m -> m.startsWith("private buildHttpRequest"))
                                    .findFirst()
                                    .orElse("");

        // Default query params
        assertTrue(buildMethod.contains("'tenant': 'default'"),
                "Should include default query param 'tenant'");

        // POST body
        assertTrue(buildMethod.contains("body"),
                "POST client should support body");
    }

    @Test
    void testMethods_Combined_ExecuteWithBodyPassesExtraHeaders()
    {
        CombinedClient client = new CombinedClient();
        List<String> methods = client.methods();

        String executeWithBody = methods.stream()
                                        .filter(m -> m.startsWith("executeWithBody"))
                                        .findFirst()
                                        .orElse("");
        assertTrue(executeWithBody.contains("extraHeaders"),
                "executeWithBody should accept extraHeaders parameter");
    }

    // ═══════════════════════════════════════════════════════════════════
    // PER-ENDPOINT ISOLATION – each client is independent
    // ═══════════════════════════════════════════════════════════════════

    @Test
    void testPerEndpoint_DifferentUrls()
    {
        SimpleGetClient getClient = new SimpleGetClient();
        PostOrderClient postClient = new PostOrderClient();

        List<String> getFields = getClient.fields();
        List<String> postFields = postClient.fields();

        String getJoined = String.join("\n", getFields);
        String postJoined = String.join("\n", postFields);

        assertTrue(getJoined.contains("'/api/users'"), "GET client should have /api/users");
        assertTrue(postJoined.contains("'/api/orders'"), "POST client should have /api/orders");
    }

    @Test
    void testPerEndpoint_DifferentHttpMethods()
    {
        SimpleGetClient getClient = new SimpleGetClient();
        PostOrderClient postClient = new PostOrderClient();
        PutProfileClient putClient = new PutProfileClient();
        DeleteItemClient deleteClient = new DeleteItemClient();

        String getMethods = String.join("\n", getClient.methods());
        String postMethods = String.join("\n", postClient.methods());
        String putMethods = String.join("\n", putClient.methods());
        String deleteMethods = String.join("\n", deleteClient.methods());

        assertTrue(getMethods.contains("this.http.get<"), "GET should use http.get");
        assertTrue(postMethods.contains("this.http.post<"), "POST should use http.post");
        assertTrue(putMethods.contains("this.http.put<"), "PUT should use http.put");
        assertTrue(deleteMethods.contains("this.http.delete<"), "DELETE should use http.delete");
    }
}

