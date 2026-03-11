---
name: angular-rest-client
description: Comprehensive guide for using the @NgRestClient annotation and its generated Angular service. Covers polling, caching, deduplication, deep merging, and more.
metadata:
  short-description: Usage guide for @NgRestClient and its generated service.
---

# Angular REST Client Generator

The `@NgRestClient` annotation is used to generate a standalone Angular service (`@Injectable`) that communicates with a single HTTP endpoint. Each annotated Java class targets one HTTP method + URL and exposes its results reactively through Angular `Signal`s.

## Annotation Configuration (@NgRestClient)

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `url` | `String` | (Required) | The endpoint URL or path. |
| `method` | `HttpMethod` | `GET` | `GET`, `POST`, `PUT`, `DELETE`, `PATCH`. |
| `responseType` | `Class` | `INgDataType.class` | The data type representing the response body. |
| `responseArray` | `boolean` | `false` | If `true`, the response is treated as an array of `responseType`. |
| `singleton` | `boolean` | `true` | If `true`, the service is provided in `root`. |
| `fetchOnCreate` | `boolean` | `false` | If `true`, the request is fired when the service is first injected. |
| `pollingEnabled` | `boolean` | `false` | Enables automatic polling at start. |
| `pollingIntervalMs` | `int` | `30,000` | The default interval for polling. |
| `cachingEnabled` | `boolean` | `false` | Enables client-side caching of the last successful response. |
| `cacheTtlMs` | `int` | `60,000` | Time-to-live for the cache in milliseconds. |
| `deduplication` | `boolean` | `true` | Prevents duplicate in-flight requests by sharing the same observable. |
| `deepMerge` | `boolean` | `false` | Merges incoming data into the current signal value (useful for partial updates). |
| `retryCount` | `int` | `0` | Number of automatic retry attempts on failure. |
| `retryDelayMs` | `int` | `1,000` | Delay between retries in milliseconds. |
| `authType` | `AuthType` | `NONE` | `NONE`, `BEARER`, `BASIC`, `CUSTOM`. |
| `authTokenField` | `String` | `'localStorage.getItem("token")'` | TS expression to resolve the auth token. |

## Generated TypeScript API

Every generated client provides the following core properties and methods:

### Signals (Reactive State)
- `data: WritableSignal<T | undefined>`: The latest successful response data.
- `loading: WritableSignal<boolean>`: `true` when a request is in progress.
- `error: WritableSignal<any>`: The error object if the last request failed.
- `success: WritableSignal<boolean>`: `true` if the last request was successful.
- `polling: WritableSignal<boolean>`: `true` if polling is currently active.

### Methods
- `execute(params?, extraHeaders?)`: Triggers a request (GET/DELETE or POST/PUT/PATCH with empty body).
- `executeWithBody(body, params?, extraHeaders?)`: Triggers a request with a payload (POST/PUT/PATCH).
- `startPolling(intervalMs?)`: Starts or restarts polling with an optional interval override.
- `stopPolling()`: Stops active polling.
- `reset()`: Clears data, error, and resets loading/success states.
- `invalidateCache()`: Clears the cache timestamp (if caching is enabled).

## Examples

### 1. Basic GET Client
```java
@NgRestClient(url = "/api/user/profile", responseType = UserProfile.class)
public class UserProfileClient implements INgRestClient<UserProfileClient> {}
```

### 2. POST Client with Body and Auth
```java
@NgRestClient(
    url = "/api/orders/create",
    method = HttpMethod.POST,
    responseType = OrderResponse.class,
    authType = AuthType.BEARER
)
public class CreateOrderClient implements INgRestClient<CreateOrderClient> {}
```

### 3. Polling and Caching
```java
@NgRestClient(
    url = "/api/system/status",
    pollingEnabled = true,
    pollingIntervalMs = 5000,
    cachingEnabled = true,
    cacheTtlMs = 2000
)
public class SystemStatusClient implements INgRestClient<SystemStatusClient> {}
```

### 4. Custom Headers and Query Params
Use `@NgRestClientHeader` and `@NgRestClientQueryParam` for static values:
```java
@NgRestClient(url = "/api/data")
@NgRestClientHeader(name = "X-Client-ID", value = "ActivityMaster")
@NgRestClientQueryParam(name = "version", value = "v2")
public class AppDataClient implements INgRestClient<AppDataClient> {}
```

## TypeScript Usage (Angular)
```typescript
@Component({ ... })
export class MyComponent {
  private profileClient = inject(UserProfileClient);

  // Read data reactively
  userData = this.profileClient.data;
  isLoading = this.profileClient.loading;

  loadProfile() {
    this.profileClient.execute({ id: '123' });
  }

  togglePolling() {
    if (this.profileClient.polling()) {
      this.profileClient.stopPolling();
    } else {
      this.profileClient.startPolling(10000);
    }
  }
}
```
