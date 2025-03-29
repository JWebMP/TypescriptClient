package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.NgProvider;
import com.jwebmp.core.base.angular.client.annotations.boot.NgBootEntryComponent;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgInject;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.interfaces.INgProvider;

import java.util.List;

//@NgScript("sockjs-client/dist/sockjs.js")
//@NgScript("@vertx/event-bridge-client.js/vertx-eventbus.js")

@NgImportReference(value = "!EventBus", reference = "@vertx/eventbus-bridge-client.js")
@NgImportReference(value = "!{ EventBus as EventBusType }", reference = "@vertx/eventbus-bridge-client.js")
//@NgImportReference(value = "!SockJS", reference = "sockjs-client")
@NgImportReference(value = "ElementRef", reference = "@angular/core")
@NgImportReference(value = "Location", reference = "@angular/common")
@NgBootEntryComponent()

//@NgField(value = "private readonly eventBusService = inject(EventBusService);", onSelf = false, onParent = true)
//@NgImportReference(value = "inject", reference = "@angular/core", onSelf = false, onParent = true)

//@NgConstructorParameter(value = "private eventBusService : EventBusService", onParent = true, onSelf = false)


@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "BehaviorSubject", reference = "rxjs")
@NgImportReference(value = "take", reference = "rxjs")
@NgImportReference(value = "filter", reference = "rxjs")
@NgImportReference(value = "Subject", reference = "rxjs")
@NgImportReference(value = "Observable", reference = "rxjs")
@NgImportReference(value = "timer", reference = "rxjs")
@NgImportReference(value = "takeUntil", reference = "rxjs/operators")

@NgField("""
        private eventBus?: EventBusType;
          private readonly eventBusUrl: string = '/eventbus'; // Update as needed
          private reconnectAttempts: number = 0;
          private readonly reconnectDelay: number = 5000;
          private readonly maxReconnectAttempts: number = 99999;
          private readonly maxReconnectDelay: number = 30000;
        
          private registeredListeners = new Set<string>();
          private totalExpectedListeners = 0;
          private listenerReady$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
          private connectionState$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
        
          private pendingListeners: Array<{ address: string; handlerId: string }> = [];
        
        
          private destroy$ = new Subject<void>();
          private messageSubjects: Map<string, Map<string, Subject<any>>> = new Map();
          private messageQueue: Array<{ action: string; data: object; eventType: string; event?: any; component?: ElementRef<any> }> = [];
          private guid: string
        """)

@NgConstructorBody("""
        \t
          this.connectionState$
              .pipe(takeUntil(this.destroy$))
              .subscribe((isConnected) => {
                  if (isConnected) {
                      console.log('[EventBusService] Connection restored. Re-registering all listeners...');
                      // Requeue all existing listeners
                      this.messageSubjects.forEach((handlers, address) => {
                          handlers.forEach((_, handlerId) => {
                              this.queueListener(address, handlerId);
                          });
                      });
                      this.processQueuedMessages();
                  } else {
                      console.warn('[EventBusService] Connection lost. Waiting for reconnection...');
                  }
              });
        
        """)
@NgConstructorBody("this.guid = this.generateGUID();\n" +
        "    this.contextIdService.setContextId(this.guid)")
@NgConstructorBody("this.initializeEventBus();")
@NgConstructorBody("""
        \t
            this.listen(this.guid,this.guid).subscribe(data => {
                    this.processResult(data);
                });
            const everyoneId = this.generateGUID();
            this.listen("Everyone",everyoneId).subscribe(data => {
                this.processResult(data);
            });
            const sessionStorage = this.generateGUID();
            this.listen("SessionStorage",everyoneId).subscribe(data => {
                Object.keys(data).forEach(prop => {
                        window.sessionStorage.setItem(prop, data[prop]);
                        if (prop === 'contextId') {
                            this.contextIdService.setContextId(data[prop]);
                        }
                    });
            });
            const localStorage = this.generateGUID();
            this.listen("LocalStorage",everyoneId).subscribe(data => {
                Object.keys(data).forEach(prop => {
                        window.localStorage.setItem(prop, data[prop]);
                    });
            });
        """)


@NgConstructorParameter("private routeLocation: Location")
@NgConstructorParameter("private router: Router")
@NgConstructorParameter("private route: ActivatedRoute")
@NgImportReference(value = "RouterModule, ParamMap,Router", reference = "@angular/router")
@NgImportReference(value = "ActivatedRoute", reference = "@angular/router")

@NgComponentReference(ContextIdService.class)
@NgConstructorParameter("private contextIdService : ContextIdService")

@NgOnDestroy("""
        // ngOnDestroy(): void {
             this.destroy$.next(); // Notify all subscriptions to terminate
             this.destroy$.complete();
        
             // Optionally unregister all listeners (if necessary)
             this.registeredListeners.forEach((address) => {
                 this.unregisterListener(address);
             });
         //}
        """)

@NgMethod("""
        \tprivate processPendingListeners(): void {
                  if (!this.eventBus) {
                      console.warn('[EventBusService] EventBus not initialized yet!');
                      return;
                  }
        
                  console.log('[EventBusService] Processing queued listeners...');
                  while (this.pendingListeners.length > 0) {
                      const { address, handlerId } = this.pendingListeners.shift()!;
        
                      // Register the listener
                      this.eventBus.registerHandler(address, {}, (error, message) => {
                          if (error) {
                              console.error(`[EventBusService] Error in listener for address "${address}":`, error);
                          } else {
                              console.log(`[EventBusService] Message received on address "${address}":`, message);
        
                              // Emit the message to the relevant Subject
                              const handlers = this.messageSubjects.get(address);
                              const subject = handlers?.get(handlerId);
                              subject?.next(message.body);
                          }
                      });
                      if(this.guid) {
                           this.eventBus.registerHandler(this.guid + "." + address, {}, (error, message) => {
                               if (error) {
                                   console.error(`[EventBusService] Error in listener for address "${this.guid + "." + address}":`, error);
                               } else {
                                   console.log(`[EventBusService] Message received on address "${this.guid + "." + address}":`, message);
        
                                   // Emit the message to the relevant Subject
                                   const handlers = this.messageSubjects.get(address);
                                   const subject = handlers?.get(handlerId);
                                   subject?.next(message.body);
                               }
                           });
                           console.log(`[EventBusService] Private Listener registered for address: "${this.guid + "." + address}", handler ID: "${handlerId}"`);
                       }
        
                      console.log(`[EventBusService] Listener registered for address: "${address}", handler ID: "${handlerId}"`);
                  }
              }
        """)

@NgMethod("""
        \tprivate queueListener(address: string, handlerId: string): void {
                  // Check if the listener is already queued
                  const isAlreadyQueued = this.pendingListeners.some(
                      listener => listener.address === address && listener.handlerId === handlerId
                  );
        
                  if (!isAlreadyQueued) {
                      console.log(`[EventBusService] Queuing listener for address: "${address}", handler ID: "${handlerId}"`);
                      this.pendingListeners.push({ address, handlerId });
                  }
        
                  // Process all queued listeners when the connection becomes ready
                  this.connectionState$
                      .pipe(
                          takeUntil(this.destroy$),
                          filter(isConnected => isConnected), // Wait for a true (connected) state
                          take(1) // Process the queue on the first successful connection
                      )
                      .subscribe(() => this.processPendingListeners());
              }
        """)

@NgMethod("""
          /**
           * Initializes the EventBus connection.
           */
          private initializeEventBus(): void {
            //this.connect(); // Connect to the EventBus
            this.connectWithTimeout(); // Connect to the EventBus
        
            // Set up a listener to monitor the connection state
           /* if (this.eventBus) {
                this.eventBus.onopen = () => {
                    console.log('[EventBusService] Connection to EventBus established.');
                    this.connectionState$.next(true); // Connection is open
                    this.reconnectAttempts = 0;
                };
        
                this.eventBus.onclose = () => {
                    console.warn('[EventBusService] Connection to EventBus lost.');
                    this.connectionState$.next(false); // Connection is closed
                    this.reconnect(); // Attempt to reconnect
                };
            }*/
        
            // Handle connection state updates
            this.connectionState$.pipe(takeUntil(this.destroy$)).subscribe((isConnected) => {
                if (!isConnected) {
                    console.warn('[EventBusService] EventBus is disconnected. Trying to reconnect...');
                }
            });
        }
        
          /**
           * Connect to the Vert.x EventBus.
           */
          private connect(): void {
            this.eventBus = new EventBus(this.eventBusUrl,{
        
                  vertxbus_ping_interval: 10000,
                  vertxbus_reconnect_attempts_max: 9999,
                  vertxbus_reconnect_delay_min: 1000,
                  vertxbus_reconnect_delay_max: 10000,
              }
          );
        
            this.eventBus.onopen = () => {
              console.log('[EventBus] Connected.');
              this.reconnectAttempts = 0;
              this.connectionState$.next(true); // Notify successful connection
            };
        
            this.eventBus.onclose = () => {
              console.warn('[EventBus] Connection closed.');
              this.connectionState$.next(false); // Notify disconnection
            };
        
            this.eventBus.onerror = (error: any) => {
              console.error('[EventBus] Error:', error);
              this.connectionState$.next(false); // Notify disconnection on error
            };
          }
        
        /**
             * Connects to the EventBus with a timeout.
             * If the connection is not established within the defined timeout, it triggers a reconnect attempt.
             */
            private connectWithTimeout(): void {
              const connectionTimeout = 10000; // 10 seconds
        
              let connectionEstablished = false;
        
              const timeout = setTimeout(() => {
                if (!connectionEstablished) {
                  console.error('[EventBusService] Connection timed out. Retrying...');
                  this.eventBus?.close(); // Ensure the WebSocket is closed
                  this.scheduleReconnect(); // Trigger a reconnect attempt
                }
              }, connectionTimeout);
        
              // Create event bus instance
              this.eventBus = new EventBus(this.eventBusUrl, {
                transport: 'websocket',
                vertxbus_ping_interval: 10000,
                vertxbus_reconnect_attempts_max: 99999,
                vertxbus_reconnect_delay_min: 1000,
                vertxbus_reconnect_delay_max: 10000,
              });
        
              this.eventBus.onopen = () => {
                console.log('[EventBusService] Connection to EventBus established.');
                connectionEstablished = true; // Mark connection as established
                clearTimeout(timeout); // Clear the timeout
                this.connectionState$.next(true); // Update connection state
                this.reconnectAttempts = 0; // Reset reconnect attempts
              };
        
              this.eventBus.onclose = () => {
                console.warn('[EventBusService] Connection lost. Retrying...');
                this.connectionState$.next(false);
                clearTimeout(timeout); // Ensure timeout is cleared
                this.scheduleReconnect(); // Trigger a reconnect attempt
              };
            }
        
            private scheduleReconnect(): void {
                  if (this.reconnectAttempts < this.maxReconnectAttempts) {
                    const delay = Math.min(
                      this.reconnectDelay * Math.pow(2, this.reconnectAttempts), // Exponential backoff
                      this.maxReconnectDelay
                    );
        
                    setTimeout(() => {
                      console.log(`[EventBusService] Reconnecting... Attempt ${this.reconnectAttempts + 1}`);
                      this.reconnectAttempts++;
                      this.connectWithTimeout(); // Retry connection
                    }, delay);
                  } else {
                    console.error('[EventBusService] Maximum reconnect attempts reached. Connection failed.');
                  }
                }
        
          /**
           * Reconnect to the EventBus with a backoff strategy.
           */
          private reconnect(): void {
            if (this.reconnectAttempts >= this.maxReconnectAttempts) {
              console.error(
                `[EventBus] Maximum reconnect attempts (${this.maxReconnectAttempts}) reached.`
              );
              return;
            }
        
            this.reconnectAttempts++;
            console.log(`[EventBus] Reconnecting... (Attempt #${this.reconnectAttempts})`);
        
            setTimeout(() => this.connect(), this.reconnectDelay);
          }
        
          /**
           * Wait for all listeners (directives) to be ready before processing sends.
           */
          public waitForListeners(totalListeners: number): void {
            this.totalExpectedListeners = totalListeners;
            this.checkAllListenersReady();
          }
        
        
          /**
             * Normalize an input address to ensure it's always a string.
             * @param address The address to normalize (can be string, array, or array-like string).
             * @returns A normalized address as a plain string.
             */
            private normalizeAddress(address: string | string[]): string {
                if (Array.isArray(address)) {
                    // If it's an array, use the first element
                    return address[0];
                }
        
                if (typeof address === 'string') {
                    try {
                        // Check if it's a stringified array (e.g., "['SessionLinesListWebUpdates']")
                        const parsedAddress = JSON.parse(address);
                        if (Array.isArray(parsedAddress)) {
                            // If it's a valid array, return the first element
                            return parsedAddress[0];
                        }
                    } catch (error) {
                        // If parsing fails, assume it's already a valid string
                    }
                }
        
                // Fallback: return address as-is
                return address;
            }
        
        
              /**
                  * Subscribe to an EventBus address with a unique ID.
                  * @param address The address to listen to.
                  * @param handlerId A unique identifier for the handler (or generated automatically).
                  * @returns Observable for messages on the address.
                  */
                 listen(address: string, handlerId: string): Observable<any> {
                     // Ensure a Subject exists for the given address and handler ID
                     if (!this.messageSubjects.has(address)) {
                         this.messageSubjects.set(address, new Map());
                     }
        
                     const handlers = this.messageSubjects.get(address)!;
                     if (!handlers.has(handlerId)) {
                         handlers.set(handlerId, new Subject<any>());
                     }
        
                     const listenerSubject = handlers.get(handlerId)!;
        
                     // Add the listener to the pending queue
                     this.queueListener(address, handlerId);
        
                     return listenerSubject.asObservable();
                 }
        
        
                 /**
                  * Generate a unique identifier for handlers.
                  * @returns A random string ID.
                  */
                 private generateUniqueId(): string {
                     return Math.random().toString(36).substr(2, 9); // Example: "k39uflv7a"
                 }
        
          /**
           * Send a message to a specific address over the EventBus.
           * @param address Address to send the message to.
           * @param message Payload to send to the address.
           */
          send(action: string, data: object, eventType: string, event?: any, component?: ElementRef<any>): void {
                  const message = { action, data, eventType, event, component };
        
                  // Check connection state
                  if (!this.connectionState$.value) {
                      console.warn('[EventBus] Connection is not ready. Message queued.');
                      this.messageQueue.push(message);
                      return;
                  }
        
                  // Connection is ready, send the message
                  this.sendMessageNow(message);
              }
        
              private sendMessageNow(message: { action: string; data: object; eventType: string; event?: any; component?: ElementRef<any> }) {
                  const news: any = {};
                  news.data = message.data;
                  news.action = message.action;
                  news.data.guid = this.guid;
                  news.data.url = window.location;
                  news.data.localStorage = window.localStorage;
                  news.data.sessionStorage = window.sessionStorage;
                  news.data.parameters = this.getParametersObject();
                  news.data.hashbang = window.location.hash;
                  news.data.route = this.routeLocation.path();
                  news.data.state = this.routeLocation.getState();
                  news.data.history = history.state;
                  news.data.datetime = new Date().getUTCDate();
                  news.data.eventType = message.eventType;
                  news.data.attributes = {};
        
                    if (message.component?.nativeElement?.attributes) {
                        const attributes = message.component.nativeElement.attributes;
        
                        // Convert attributes into a plain object
                        for (let i = 0; i < attributes.length; i++) {
                            const attr = attributes[i];
                            news.data.attributes[attr.name] = attr.value;
                        }
                    }
        
                  news.data.headers = {};
                  news.data.headers.useragent = navigator.userAgent;
                  news.data.headers.cookieEnabled = navigator.cookieEnabled;
                  news.data.headers.appName = navigator.appName;
                  news.data.headers.appVersion = navigator.appVersion;
                  news.data.headers.language = navigator.language;
        
                  if (message.event) {
                      news.event = JSON.stringify(message.event);
                  }
        
                  if (this.eventBus) {
                      this.eventBus.send('incoming', news, (err: any, reply: any) => {
                          if (err) {
                              console.error(`[EventBus] Failed to send message: ${err.message}`);
                          } else {
                              console.log(`[EventBus] Message sent successfully:`, reply);
                          }
                      });
                  } else {
                      console.error('[EventBus] EventBus is not initialized.');
                  }
              }
        
              private processQueuedMessages(): void {
                  while (this.messageQueue.length > 0) {
                      const message = this.messageQueue.shift(); // Remove the first message from the queue
                      if (message) {
                          this.sendMessageNow(message);
                      }
                  }
              }
        
          /**
           * Register a listener and mark it as ready.
           */
          private registerListener(address: string): void {
            this.registeredListeners.add(address);
            this.checkAllListenersReady();
          }
        
          /**
           * Check if all necessary listeners (directives) are ready.
           */
          private checkAllListenersReady(): void {
            if (
              this.totalExpectedListeners > 0 &&
              this.registeredListeners.size >= this.totalExpectedListeners
            ) {
              this.listenerReady$.next(true); // Mark as ready
              console.log('[EventBus] All listeners registered.');
            }
          }
        
          /**
           * Wait until all listeners are marked as ready.
           */
          private waitForAllListeners(): Promise<void> {
            return new Promise(resolve => {
              this.listenerReady$.pipe(takeUntil(this.destroy$)).subscribe(isReady => {
                if (isReady) resolve();
              });
            });
          }
        
          /**
           * Disconnect the service and clean up resources.
           */
          public disconnect(): void {
            this.eventBus?.close();
            console.log('[EventBus] Disconnected.');
            this.destroy$.next();
            this.destroy$.complete();
          }
        
        """)

@NgMethod("""
        \t
            getParametersObject() : object {
                try {
                    var search = location.search.substring(1);
                    return JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\\\"').replace(/&/g, '","').replace(/=/g, '":"') + '"}');
                } catch (err) {
                    return {};
                }
            }
        """)

@NgMethod("""
        \t
            processResult(response: any) {
                    if (response.localStorage) {
                        Object.keys(response.localStorage).forEach(prop => {
                            window.localStorage.setItem(prop, response.localStorage[prop]);
                        });
                    }
                    if (response.sessionStorage) {
                        Object.keys(response.sessionStorage).forEach(prop => {
                            window.sessionStorage.setItem(prop, response.sessionStorage[prop]);
                            if (prop === 'contextId') {
                                this.contextIdService.setContextId(response.sessionStorage[prop]);
                            }
                        });
                    }
                    if (response.features) {
                    }
                    if (response.reactions) {
                        for (let reaction of response.reactions) {
                            const react: any = reaction;
                            if ("RedirectUrl" == react.reactionType) {
                                this.router.navigateByUrl(react.reactionMessage);
                            }
                        }
                    }
                }
        """)

/*

@NgMethod("""
        send(action:string,data:object, eventType :string,event? : any, component? : ElementRef<any>) : void {
             const news : any = {
             };
             news.data = data;
             news.action = action;
             news.data.url = window.location;
             news.data.localStorage = window.localStorage;
             news.data.sessionStorage = window.sessionStorage;
             news.data.parameters = this.getParametersObject();
             news.data.hashbang = window.location.hash;
             news.data.route = this.routeLocation.path();
             news.data.state = this.routeLocation.getState();
             news.data.history = history.state;
             news.data.datetime = new Date().getUTCDate();
             news.data.eventType = eventType;
             news.data.headers = {};
             news.data.headers.useragent = navigator.userAgent;
             news.data.headers.cookieEnabled = navigator.cookieEnabled ;
             news.data.headers.appName = navigator.appName  ;
             news.data.headers.appVersion = navigator.appVersion   ;
             news.data.headers.language = navigator.language ;
             if(event)
             {
                news.event = JSON.stringify(event);
             }
             if(component)
             {
                 let ele = component.nativeElement;
                 news.componentId = ele.getAttribute("id");
                 news.data.attributes = {};
                 for (const attributeName of ele.getAttributeNames()) {
                     news.data.attributes[attributeName] = ele.getAttribute(attributeName);
                 }
             }
             this.sendMessage('incoming',news);
        }
        """)
*/

@NgMethod("""
        \t
            /**
               * Remove a specific handler for an EventBus address by its ID.
               * If no listeners remain for the address, the handler is fully removed from the EventBus.
               * @param address The address of the listeners to check.
               * @param handlerId The specific handler ID to unregister.
               */
              unregisterListener(address: string, handlerId: string): void {
                  const normalizedAddress = this.normalizeAddress(address);
        
                  if (this.messageSubjects.has(normalizedAddress)) {
                      const listeners = this.messageSubjects.get(normalizedAddress);
        
                      if (listeners!.has(handlerId)) {
                          // Remove this specific handler
                          listeners!.get(handlerId)!.complete(); // Notify subscribers of closure
                          listeners!.delete(handlerId);
                          console.log(`[EventBusService] Unregistered handler ID "${handlerId}" for address: "${normalizedAddress}"`);
        
                          // If no listeners remain for this address, clean up the EventBus handler
                          if (listeners!.size === 0) {
                              this.messageSubjects.delete(normalizedAddress);
        
                              if (this.eventBus && this.registeredListeners.has(normalizedAddress)) {
                                  this.eventBus.unregisterHandler(normalizedAddress, (error: any) => {
                                      if (error) {
                                          console.error(`[EventBusService] Failed to unregister handler for address: "${normalizedAddress}"`, error);
                                      } else {
                                          console.log(`[EventBusService] All handlers removed. EventBus handler unregistered for address: "${normalizedAddress}"`);
                                          this.registeredListeners.delete(normalizedAddress);
                                      }
                                  });
                                  this.eventBus.unregisterHandler(this.guid + "." + normalizedAddress, (error: any) => {
                                      if (error) {
                                          console.error(`[EventBusService] Failed to unregister handler for address: "${normalizedAddress}"`, error);
                                      } else {
                                          console.log(`[EventBusService] All handlers removed. EventBus handler unregistered for address: "${normalizedAddress}"`);
                                      }
                                  });
                              }
                          }
                      } else {
                          console.warn(`[EventBusService] No handler found with ID "${handlerId}" for address: "${normalizedAddress}"`);
                      }
                  } else {
                      console.warn(`[EventBusService] No listeners found for address: "${normalizedAddress}"`);
                  }
              }""")

@NgMethod("""
        \t
            /**
             * Utility to unregister all listeners. Useful for cleanup operations.
             */
            unregisterAllListeners(): void {
                if (this.eventBus && this.messageSubjects.size > 0) {
                    this.messageSubjects.forEach((handlers, address) => {
                        // Unregister each handler for the address
                        handlers.forEach((_, handlerId) => {
                            this.unregisterListener(address, handlerId);
                        });
                    });
                    console.log("[EventBusService] Unregistered all listeners.");
                } else {
                    console.warn("[EventBusService] No active listeners to unregister.");
                }
            }
        """)

@NgMethod("""
        
          /**
           * Method to generate a GUID.
           */
          private generateGUID(): string {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
              const r = (Math.random() * 16) | 0;
              const v = c === 'x' ? r : (r & 0x3) | 0x8;
              return v.toString(16);
            });
          }
        """)

@NgProvider(singleton = true)
@NgInject(value = "EventBusService", referenceName = "eventBusService", onParent = true, onSelf = false)
public class EventBusService<J extends EventBusService<J>> implements INgProvider<J>
{

    @Override
    public List<String> onDestroy()
    {
        var s = INgProvider.super.onDestroy();
        s.add("""
                console.log('Cleaning up EventBusService...');
                    this.disconnect();
                
                """);
        return s;
    }

    @Override
    public List<String> decorators()
    {
        List<String> out = INgProvider.super.decorators();
        out.add("@Injectable({\n" +
                "  providedIn: 'root'\n" +
                "})");
        return out;
    }
}
