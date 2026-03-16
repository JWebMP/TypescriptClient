package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.NgRestClient;
import com.jwebmp.core.base.angular.client.annotations.angular.NgRestClientHeader;
import com.jwebmp.core.base.angular.client.annotations.angular.NgRestClientQueryParam;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;

import java.util.*;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;

/**
 * Interface for Angular REST client services.
 * <p>
 * Each implementation targets a <b>single</b> HTTP endpoint (one HTTP method + URL).
 * The generated TypeScript service is an {@code @Injectable} that uses Angular's
 * {@code HttpClient} and exposes the result via Angular {@code signal()} /
 * {@code WritableSignal} so consumers can read them reactively.
 * <p>
 * Built-in behaviours controlled through {@link NgRestClient}:
 * <ul>
 *   <li><b>Polling</b> – re-fetch at a fixed interval</li>
 *   <li><b>Caching</b> – skip HTTP call when cached data is still fresh</li>
 *   <li><b>Deduplication</b> – share in-flight requests</li>
 *   <li><b>Deep merge</b> – merge partial updates into existing signal value</li>
 *   <li><b>Retry</b> – automatic retry with configurable count &amp; delay</li>
 * </ul>
 *
 * @param <J> self-referencing generic
 */
@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "inject", reference = "@angular/core")
@NgImportReference(value = "signal, WritableSignal, computed, Signal", reference = "@angular/core")
@NgImportReference(value = "DestroyRef", reference = "@angular/core")
@NgImportReference(value = "HttpClient", reference = "@angular/common/http")
@NgImportReference(value = "HttpHeaders", reference = "@angular/common/http")
@NgImportReference(value = "Subscription, Subject, Observable, of, shareReplay, timer, switchMap, tap, retry, delay, catchError, EMPTY, finalize, takeUntil", reference = "rxjs")

@NgImportReference(value = "OnDestroy", reference = "@angular/core")

public interface INgRestClient<J extends INgRestClient<J>> extends IComponent<J> {

    @Override
    default List<String> interfaces() {
        List<String> out = IComponent.super.interfaces();
        out.add("OnDestroy");
        return out;
    }

    @Override
    default List<String> decorators() {
        List<String> out = IComponent.super.decorators();
        NgRestClient rc = getAnnotation();
        String providedIn = rc.singleton() ? "root" : "any";
        out.add("@Injectable({\n  providedIn: '" + providedIn + "'\n})");
        return out;
    }

    // ── Annotation accessor ────────────────────────────────────────────

    default NgRestClient getAnnotation() {
        return getClass().getAnnotation(NgRestClient.class);
    }

    // ── Fields ─────────────────────────────────────────────────────────

    @Override
    default List<String> fields() {
        List<String> fields = IComponent.super.fields();
        NgRestClient rc = getAnnotation();

        // Resolve response type name
        String typeName = resolveResponseTypeName();
        String fullType = rc.responseArray() ? typeName + "[]" : typeName;
        String defaultVal = rc.responseArray() ? "[]" : "undefined";
        String signalType = rc.responseArray() ? fullType : fullType + " | undefined";

        // Core HttpClient
        fields.add("private readonly http = inject(HttpClient);");
        fields.add("private readonly destroyRef = inject(DestroyRef);");

        // Result signal
        fields.add("readonly data: WritableSignal<" + signalType + "> = signal<" + signalType + ">(" + defaultVal + ");");

        // State signals
        fields.add("readonly loading: WritableSignal<boolean> = signal<boolean>(false);");
        fields.add("readonly error: WritableSignal<any> = signal<any>(undefined);");
        fields.add("readonly success: WritableSignal<boolean> = signal<boolean>(false);");

        // Endpoint config
        fields.add("private readonly endpointUrl = '" + rc.url() + "';");

        // Destroy subject
        fields.add("private readonly destroy$ = new Subject<void>();");

        // Deduplication
        if (rc.deduplication()) {
            fields.add("private inflightRequest$: Observable<" + signalType + "> | null = null;");
        }

        // Caching
        if (rc.cachingEnabled()) {
            fields.add("private cacheTimestamp: number | null = null;");
            fields.add("private readonly cacheTtlMs = " + rc.cacheTtlMs() + ";");
        }

        // Polling
        if (rc.pollingEnabled()) {
            fields.add("private pollingSubscription?: Subscription;");
            fields.add("private pollingIntervalMs = " + rc.pollingIntervalMs() + ";");
            fields.add("readonly polling: WritableSignal<boolean> = signal<boolean>(false);");
        }

        return fields;
    }

    // ── Constructor body ───────────────────────────────────────────────

    @Override
    default List<String> constructorBody() {
        List<String> body = IComponent.super.constructorBody();
        NgRestClient rc = getAnnotation();

        // Register destroy cleanup
        body.add("""
                this.destroyRef.onDestroy(() => {
                    this.destroy$.next();
                    this.destroy$.complete();
                });
                """);

        if (rc.fetchOnCreate()) {
            body.add("this.execute();");
        }

        if (rc.pollingEnabled()) {
            body.add("this.startPolling();");
        }

        return body;
    }

    // ── Methods ────────────────────────────────────────────────────────

    @Override
    default List<String> methods() {
        List<String> methods = IComponent.super.methods();
        NgRestClient rc = getAnnotation();

        String typeName = resolveResponseTypeName();
        String fullType = rc.responseArray() ? typeName + "[]" : typeName;
        String signalType = rc.responseArray() ? fullType : fullType + " | undefined";

        // ── execute() – main trigger ───────────────────────────────────
        methods.add(buildExecuteMethod(rc, signalType));

        // ── executeWithBody() – for POST/PUT/PATCH ─────────────────────
        if (rc.method() == NgRestClient.HttpMethod.POST ||
                rc.method() == NgRestClient.HttpMethod.PUT ||
                rc.method() == NgRestClient.HttpMethod.PATCH) {
            methods.add(buildExecuteWithBodyMethod(rc, signalType));
        }

        // ── buildHttpRequest$() ────────────────────────────────────────
        methods.add(buildHttpRequestMethod(rc, signalType));

        // ── buildHeaders() ─────────────────────────────────────────────
        methods.add(buildHeadersMethod());

        // ── handleResponse() ───────────────────────────────────────────
        methods.add(buildHandleResponseMethod(rc, signalType));

        // ── Deep merge utility ─────────────────────────────────────────
        if (rc.deepMerge()) {
            methods.add(buildDeepMergeMethod());
            methods.add(buildMergeArraysMethod());
        }

        // ── Polling ────────────────────────────────────────────────────
        if (rc.pollingEnabled()) {
            methods.add(buildStartPollingMethod());
            methods.add(buildStopPollingMethod());
        }

        // ── Cache helpers ──────────────────────────────────────────────
        if (rc.cachingEnabled()) {
            methods.add(buildIsCacheValidMethod());
            methods.add(buildInvalidateCacheMethod());
        }

        // ── reset() ────────────────────────────────────────────────────
        methods.add(buildResetMethod(rc, signalType));

        // ── ngOnDestroy ────────────────────────────────────────────────
        methods.add(buildNgOnDestroyMethod(rc));

        return methods;
    }

    // ── Private builder helpers ────────────────────────────────────────

    private String resolveResponseTypeName() {
        NgRestClient rc = getAnnotation();
        Class<? extends INgDataType> responseType = rc.responseType();
        if (responseType == INgDataType.class) {
            return "any";
        }
        return getTsFilename(responseType);
    }

    private String buildExecuteMethod(NgRestClient rc, String signalType) {
        StringBuilder sb = new StringBuilder();
        sb.append("execute(params?: Record<string, string>, extraHeaders?: Record<string, string>): void {\n");

        // Caching check
        if (rc.cachingEnabled()) {
            sb.append("    if (this.isCacheValid()) {\n");
            sb.append("        return;\n");
            sb.append("    }\n");
        }

        // Deduplication check
        if (rc.deduplication()) {
            sb.append("    if (this.inflightRequest$) {\n");
            sb.append("        this.inflightRequest$.pipe(takeUntil(this.destroy$)).subscribe();\n");
            sb.append("        return;\n");
            sb.append("    }\n");
        }

        sb.append("    this.loading.set(true);\n");
        sb.append("    this.error.set(undefined);\n");
        sb.append("    this.success.set(false);\n\n");

        sb.append("    const request$ = this.buildHttpRequest$(params, extraHeaders);\n");

        if (rc.deduplication()) {
            sb.append("    this.inflightRequest$ = request$.pipe(shareReplay(1));\n");
            sb.append("    this.inflightRequest$.pipe(\n");
            sb.append("        takeUntil(this.destroy$),\n");
            sb.append("        finalize(() => this.inflightRequest$ = null)\n");
            sb.append("    ).subscribe({\n");
        } else {
            sb.append("    request$.pipe(takeUntil(this.destroy$)).subscribe({\n");
        }

        sb.append("        next: (response) => this.handleResponse(response),\n");
        sb.append("        error: (err) => {\n");
        sb.append("            this.error.set(err);\n");
        sb.append("            this.loading.set(false);\n");
        sb.append("            this.success.set(false);\n");
        sb.append("            console.error(`[RestClient] Error on ").append(rc.method()).append(" ").append(rc.url()).append(":`, err);\n");
        sb.append("        }\n");
        sb.append("    });\n");
        sb.append("}");
        return sb.toString();
    }

    private String buildExecuteWithBodyMethod(NgRestClient rc, String signalType) {
        StringBuilder sb = new StringBuilder();
        sb.append("executeWithBody(body: any, params?: Record<string, string>, extraHeaders?: Record<string, string>): void {\n");

        if (rc.deduplication()) {
            sb.append("    if (this.inflightRequest$) {\n");
            sb.append("        this.inflightRequest$.pipe(takeUntil(this.destroy$)).subscribe();\n");
            sb.append("        return;\n");
            sb.append("    }\n");
        }

        sb.append("    this.loading.set(true);\n");
        sb.append("    this.error.set(undefined);\n");
        sb.append("    this.success.set(false);\n\n");

        sb.append("    const request$ = this.buildHttpRequest$(params, body, extraHeaders);\n");

        if (rc.deduplication()) {
            sb.append("    this.inflightRequest$ = request$.pipe(shareReplay(1));\n");
            sb.append("    this.inflightRequest$.pipe(\n");
            sb.append("        takeUntil(this.destroy$),\n");
            sb.append("        finalize(() => this.inflightRequest$ = null)\n");
            sb.append("    ).subscribe({\n");
        } else {
            sb.append("    request$.pipe(takeUntil(this.destroy$)).subscribe({\n");
        }

        sb.append("        next: (response) => this.handleResponse(response),\n");
        sb.append("        error: (err) => {\n");
        sb.append("            this.error.set(err);\n");
        sb.append("            this.loading.set(false);\n");
        sb.append("            this.success.set(false);\n");
        sb.append("            console.error(`[RestClient] Error on ").append(rc.method()).append(" ").append(rc.url()).append(":`, err);\n");
        sb.append("        }\n");
        sb.append("    });\n");
        sb.append("}");
        return sb.toString();
    }

    private String buildHttpRequestMethod(NgRestClient rc, String signalType) {
        StringBuilder sb = new StringBuilder();
        boolean hasBody = rc.method() == NgRestClient.HttpMethod.POST ||
                rc.method() == NgRestClient.HttpMethod.PUT ||
                rc.method() == NgRestClient.HttpMethod.PATCH;

        sb.append("private buildHttpRequest$(params?: Record<string, string>");
        if (hasBody) {
            sb.append(", body?: any");
        }
        sb.append(", extraHeaders?: Record<string, string>");
        sb.append("): Observable<").append(signalType).append("> {\n");

        // ── URL with default query params ──────────────────────────────
        sb.append("    let url = this.endpointUrl;\n");

        // Collect default query params from @NgRestClientQueryParam annotations
        NgRestClientQueryParam[] defaultParams = getClass().getAnnotationsByType(NgRestClientQueryParam.class);
        if (defaultParams.length > 0) {
            sb.append("    const defaultParams: Record<string, string> = {\n");
            for (NgRestClientQueryParam qp : defaultParams) {
                sb.append("        '").append(escapeTs(qp.name())).append("': '").append(escapeTs(qp.value())).append("',\n");
            }
            sb.append("    };\n");
            sb.append("    const mergedParams = { ...defaultParams, ...params };\n");
        } else {
            sb.append("    const mergedParams = params;\n");
        }

        sb.append("    if (mergedParams && Object.keys(mergedParams).length > 0) {\n");
        sb.append("        const queryString = Object.entries(mergedParams)\n");
        sb.append("            .map(([k, v]) => encodeURIComponent(k) + '=' + encodeURIComponent(v))\n");
        sb.append("            .join('&');\n");
        sb.append("        url += (url.includes('?') ? '&' : '?') + queryString;\n");
        sb.append("    }\n\n");

        // ── Headers ────────────────────────────────────────────────────
        sb.append("    let headers = this.buildHeaders(extraHeaders);\n\n");

        // ── HTTP call ──────────────────────────────────────────────────
        String options;
        switch (rc.method()) {
            case POST:
                options = "this.http.post<" + signalType + ">(url, body ?? {}, { headers })";
                break;
            case PUT:
                options = "this.http.put<" + signalType + ">(url, body ?? {}, { headers })";
                break;
            case DELETE:
                options = "this.http.delete<" + signalType + ">(url, { headers })";
                break;
            case PATCH:
                options = "this.http.patch<" + signalType + ">(url, body ?? {}, { headers })";
                break;
            default: // GET
                options = "this.http.get<" + signalType + ">(url, { headers })";
                break;
        }

        sb.append("    let request$ = ").append(options).append(";\n");

        // Retry
        if (rc.retryCount() > 0) {
            sb.append("    request$ = request$.pipe(retry({ count: ").append(rc.retryCount())
                    .append(", delay: ").append(rc.retryDelayMs()).append(" }));\n");
        }

        sb.append("    return request$;\n");
        sb.append("}");
        return sb.toString();
    }

    private String buildHeadersMethod() {
        NgRestClient rc = getAnnotation();
        NgRestClientHeader[] staticHeaders = getClass().getAnnotationsByType(NgRestClientHeader.class);

        StringBuilder sb = new StringBuilder();
        sb.append("private buildHeaders(extraHeaders?: Record<string, string>): HttpHeaders {\n");
        sb.append("    let headers = new HttpHeaders();\n");

        // Static headers from @NgRestClientHeader annotations
        for (NgRestClientHeader h : staticHeaders) {
            sb.append("    headers = headers.set('")
                    .append(escapeTs(h.name())).append("', '")
                    .append(escapeTs(h.value())).append("');\n");
        }

        // Authentication
        if (rc.authType() != NgRestClient.AuthType.NONE) {
            String tokenExpr = rc.authTokenField();
            sb.append("\n    const authToken = ").append(tokenExpr).append(";\n");
            sb.append("    if (authToken) {\n");

            switch (rc.authType()) {
                case BEARER:
                    sb.append("        headers = headers.set('Authorization', 'Bearer ' + authToken);\n");
                    break;
                case BASIC:
                    sb.append("        headers = headers.set('Authorization', 'Basic ' + authToken);\n");
                    break;
                case CUSTOM:
                    sb.append("        headers = headers.set('")
                            .append(escapeTs(rc.authHeaderName()))
                            .append("', authToken);\n");
                    break;
                default:
                    break;
            }

            sb.append("    }\n");
        }

        // Runtime extra headers override everything
        sb.append("\n    if (extraHeaders) {\n");
        sb.append("        for (const [key, value] of Object.entries(extraHeaders)) {\n");
        sb.append("            headers = headers.set(key, value);\n");
        sb.append("        }\n");
        sb.append("    }\n");

        sb.append("    return headers;\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Escapes a string for safe inclusion in a TypeScript single-quoted string literal.
     */
    private String escapeTs(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    private String buildHandleResponseMethod(NgRestClient rc, String signalType) {
        StringBuilder sb = new StringBuilder();
        sb.append("private handleResponse(response: ").append(signalType).append("): void {\n");

        if (rc.deepMerge()) {
            sb.append("    const current = this.data();\n");
            sb.append("    if (current !== undefined && current !== null && response !== undefined && response !== null) {\n");
            sb.append("        const merged = this.deepMergeInto(current, response);\n");
            sb.append("        if (merged) {\n");
            sb.append("            this.data.set({...current});\n");
            sb.append("        }\n");
            sb.append("    } else {\n");
            sb.append("        this.data.set(response);\n");
            sb.append("    }\n");
        } else {
            sb.append("    this.data.set(response);\n");
        }

        if (rc.cachingEnabled()) {
            sb.append("    this.cacheTimestamp = Date.now();\n");
        }

        sb.append("    this.loading.set(false);\n");
        sb.append("    this.success.set(true);\n");
        sb.append("}");
        return sb.toString();
    }

    private String buildDeepMergeMethod() {
        return """
                private deepMergeInto(target: any, source: any): boolean {
                    if (target === source) return false;
                    if (source === null || source === undefined) return false;
                
                    const isObject = (v: any) => v !== null && typeof v === 'object';
                    if (!isObject(source) || Array.isArray(source)) {
                        if (Array.isArray(source)) {
                            return this.mergeArraysInPlace(target, source);
                        }
                        return target !== source;
                    }
                
                    if (!isObject(target) || Array.isArray(target)) return true;
                
                    let changed = false;
                    for (const key of Object.keys(source)) {
                        const sVal = source[key];
                        const tHas = Object.prototype.hasOwnProperty.call(target, key);
                        const tVal = tHas ? target[key] : undefined;
                
                        if (Array.isArray(sVal)) {
                            const didChange = this.mergeArraysInPlace(tVal, sVal);
                            if (didChange) {
                                if (!Array.isArray(tVal)) {
                                    target[key] = [...sVal];
                                }
                                changed = true;
                            }
                        } else if (isObject(sVal)) {
                            if (!isObject(tVal) || Array.isArray(tVal)) {
                                target[key] = {};
                                changed = true;
                            }
                            if (this.deepMergeInto(target[key], sVal)) changed = true;
                        } else if (tVal !== sVal) {
                            target[key] = sVal;
                            changed = true;
                        }
                    }
                
                    for (const key of Object.keys(target)) {
                        if (!Object.prototype.hasOwnProperty.call(source, key)) {
                            delete target[key];
                            changed = true;
                        }
                    }
                
                    return changed;
                }""";
    }

    private String buildMergeArraysMethod() {
        return """
                private mergeArraysInPlace(targetArr: any, sourceArr: any[]): boolean {
                    if (!Array.isArray(sourceArr)) return false;
                    if (!Array.isArray(targetArr)) return true;
                
                    const itemsAreObjectsWithId =
                        sourceArr.length > 0 &&
                        typeof sourceArr[0] === 'object' &&
                        sourceArr[0] !== null &&
                        !Array.isArray(sourceArr[0]) &&
                        'id' in sourceArr[0];
                
                    if (!itemsAreObjectsWithId) {
                        if (targetArr.length !== sourceArr.length) {
                            targetArr.length = 0;
                            for (const item of sourceArr) targetArr.push(item);
                            return true;
                        }
                        let changed = false;
                        for (let i = 0; i < sourceArr.length; i++) {
                            if (targetArr[i] !== sourceArr[i]) {
                                targetArr[i] = sourceArr[i];
                                changed = true;
                            }
                        }
                        return changed;
                    }
                
                    const indexById = new Map<any, number>();
                    for (let i = 0; i < targetArr.length; i++) {
                        const item = targetArr[i];
                        if (item && typeof item === 'object' && 'id' in item) {
                            if (!indexById.has(item.id)) indexById.set(item.id, i);
                        }
                    }
                
                    let changed = false;
                    const seenIds = new Set<any>();
                
                    for (const srcItem of sourceArr) {
                        if (!srcItem || typeof srcItem !== 'object' || Array.isArray(srcItem) || !('id' in srcItem)) continue;
                        const id = (srcItem as any).id;
                        if (seenIds.has(id)) continue;
                        seenIds.add(id);
                
                        if (!indexById.has(id)) {
                            targetArr.push(srcItem);
                            changed = true;
                            indexById.set(id, targetArr.length - 1);
                        } else {
                            const idx = indexById.get(id)!;
                            const tgtItem = targetArr[idx];
                            if (tgtItem && typeof tgtItem === 'object' && !Array.isArray(tgtItem)) {
                                if (this.deepMergeInto(tgtItem, srcItem)) changed = true;
                            } else if (tgtItem !== srcItem) {
                                targetArr[idx] = srcItem;
                                changed = true;
                            }
                        }
                    }
                
                    for (let i = targetArr.length - 1; i >= 0; i--) {
                        const it = targetArr[i];
                        if (it && typeof it === 'object' && !Array.isArray(it) && 'id' in it) {
                            if (!seenIds.has((it as any).id)) {
                                targetArr.splice(i, 1);
                                changed = true;
                            }
                        }
                    }
                
                    return changed;
                }""";
    }

    private String buildStartPollingMethod() {
        return """
                startPolling(intervalMs?: number): void {
                    this.stopPolling();
                    if (intervalMs !== undefined) {
                        this.pollingIntervalMs = intervalMs;
                    }
                    this.polling.set(true);
                    this.pollingSubscription = timer(0, this.pollingIntervalMs).pipe(
                        takeUntil(this.destroy$)
                    ).subscribe(() => this.execute());
                }""";
    }

    private String buildStopPollingMethod() {
        return """
                stopPolling(): void {
                    this.polling.set(false);
                    if (this.pollingSubscription) {
                        this.pollingSubscription.unsubscribe();
                        this.pollingSubscription = undefined;
                    }
                }""";
    }

    private String buildIsCacheValidMethod() {
        return """
                private isCacheValid(): boolean {
                    if (this.cacheTimestamp === null) return false;
                    return (Date.now() - this.cacheTimestamp) < this.cacheTtlMs;
                }""";
    }

    private String buildInvalidateCacheMethod() {
        return """
                invalidateCache(): void {
                    this.cacheTimestamp = null;
                }""";
    }

    private String buildResetMethod(NgRestClient rc, String signalType) {
        String defaultVal = rc.responseArray() ? "[] as any" : "undefined";
        StringBuilder sb = new StringBuilder();
        sb.append("reset(): void {\n");
        sb.append("    this.data.set(").append(defaultVal).append(");\n");
        sb.append("    this.loading.set(false);\n");
        sb.append("    this.error.set(undefined);\n");
        sb.append("    this.success.set(false);\n");
        if (rc.cachingEnabled()) {
            sb.append("    this.invalidateCache();\n");
        }
        if (rc.deduplication()) {
            sb.append("    this.inflightRequest$ = null;\n");
        }
        sb.append("}");
        return sb.toString();
    }

    private String buildNgOnDestroyMethod(NgRestClient rc) {
        StringBuilder sb = new StringBuilder();
        sb.append("ngOnDestroy(): void {\n");
        sb.append("    this.destroy$.next();\n");
        sb.append("    this.destroy$.complete();\n");
        if (rc.pollingEnabled()) {
            sb.append("    this.stopPolling();\n");
        }
        sb.append("}");
        return sb.toString();
    }

    // ── Import resolution ──────────────────────────────────────────────

    @Override
    default List<NgImportReference> getAllImportAnnotations() {
        List<NgImportReference> out = IComponent.super.getAllImportAnnotations();
        NgRestClient rc = getAnnotation();

        // Import response data type if it's not 'any'
        if (rc.responseType() != INgDataType.class) {
            @SuppressWarnings("unchecked")
            Class<? extends IComponent<?>> responseClass = (Class<? extends IComponent<?>>) (Class<?>) rc.responseType();
            NgComponentReference ref = AnnotationUtils.getNgComponentReference(responseClass);
            out.addAll(putRelativeLinkInMap(getClass(), ref));
        }

        return out;
    }

    @Override
    default String renderOnDestroyMethod() {
        // ngOnDestroy is rendered via the methods() list – suppress the default rendering
        return "";
    }
}




