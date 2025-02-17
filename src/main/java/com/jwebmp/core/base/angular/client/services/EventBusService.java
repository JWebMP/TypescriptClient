package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.NgProvider;
import com.jwebmp.core.base.angular.client.annotations.boot.NgBootEntryComponent;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
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

@NgField(value = "private readonly eventBusService = inject(EventBusService); // Injected EventBus service.", onSelf = false, onParent = true)
//@NgConstructorParameter(value = "private eventBusService : EventBusService", onParent = true, onSelf = false)


@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "inject", reference = "@angular/core", onSelf = false, onParent = true)
@NgImportReference(value = "BehaviorSubject", reference = "rxjs")
@NgImportReference(value = "Subject", reference = "rxjs")
@NgImportReference(value = "Observable", reference = "rxjs")
@NgImportReference(value = "timer", reference = "rxjs")
@NgImportReference(value = "takeUntil", reference = "rxjs/operators")

@NgField("""
        private eventBus?: EventBusType;
          private readonly eventBusUrl: string = '/eventbus'; // Update as needed
          private reconnectAttempts: number = 0;
          private readonly reconnectDelay: number = 5000;
          private readonly maxReconnectAttempts: number = 10;
        
          private registeredListeners = new Set<string>();
          private totalExpectedListeners = 0;
          private listenerReady$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
          private connectionState$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
        
          private destroy$ = new Subject<void>();
          private messageSubjects: Map<string, Subject<any>> = new Map();
          private messageQueue: Array<{ action: string; data: object; eventType: string; event?: any; component?: ElementRef<any> }> = [];
        
        """)

@NgConstructorBody("""
        this.connectionState$.subscribe((isConnected) => {
            if (isConnected) {
              console.log('[EventBus] Connection is ready. Processing queued messages.');
              this.processQueuedMessages();
            }
          });
        """)
@NgConstructorBody("this.initializeEventBus();")


@NgConstructorParameter("private routeLocation: Location")
@NgConstructorParameter("private router: Router")
@NgConstructorParameter("private route: ActivatedRoute")
@NgImportReference(value = "RouterModule, ParamMap,Router", reference = "@angular/router")
@NgImportReference(value = "ActivatedRoute", reference = "@angular/router")

@NgComponentReference(ContextIdService.class)
@NgConstructorParameter("private contextIdService : ContextIdService")

@NgMethod("""
          /**
           * Initializes the EventBus connection.
           */
          private initializeEventBus(): void {
            this.connect(); // Connect to the EventBus
        
            // Set up a listener to monitor the connection state
            if (this.eventBus) {
                this.eventBus.onopen = () => {
                    console.log('[EventBusService] Connection to EventBus established.');
                    this.connectionState$.next(true); // Connection is open
                };
        
                this.eventBus.onclose = () => {
                    console.warn('[EventBusService] Connection to EventBus lost.');
                    this.connectionState$.next(false); // Connection is closed
                    this.reconnect(); // Attempt to reconnect
                };
            }
        
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
            transport: 'websocket', // Use WebSocket transport only
               vertxbus_ping_interval: 30000, // Send pings every 30 seconds
                 vertxbus_reconnect_attempts_max: 99999, // Retry only 5 times
                 vertxbus_reconnect_delay_min: 1000, // Minimum 1-second delay between retry attempts
                 vertxbus_reconnect_delay_max: 10000 // Maximum 10-second delay between retry attempts
        
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
           * Subscribe to an address on the EventBus.
           * @param address Address to subscribe to on EventBus.
           * @returns Observable stream of incoming messages for this address.
           */
          listen(address: string): Observable<any> {
            if (!this.messageSubjects.has(address)) {
                this.messageSubjects.set(address, new Subject<any>());
            }
        
            // Wait for the connection to be ready before registering the handler
            this.connectionState$.pipe(takeUntil(this.destroy$)).subscribe((isConnected) => {
                if (isConnected && this.eventBus) {
                    if (!this.registeredListeners.has(address)) {
                        this.eventBus.registerHandler(address, (error: any, message: any) => {
                            if (error) {
                                console.error(
                                    `[EventBusService] Error while listening to address "${address}":`,
                                    error
                                );
                            } else {
                                // Emit the received message to the subject
                                this.messageSubjects.get(address)?.next(message.body);
                            }
                        });
        
                        console.log(`[EventBusService] Registered handler for address: "${address}"`);
                        this.registeredListeners.add(address); // Track registered listeners
                    }
                }
            });
        
            return this.messageSubjects.get(address)!.asObservable();
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
        getParametersObject() : object {
            try {
                var search = location.search.substring(1);
                return JSON.parse('{"' + decodeURI(search).replace(/"/g, '\\\\"').replace(/&/g, '","').replace(/=/g, '":"') + '"}');
            } catch (err) {
                return {};
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
        /**
         * Unregister a single listener from the EventBus.
         * @param address The address to unregister the handler from.
         */
        unregisterListener(address: string): void {
            if (this.eventBus && this.registeredListeners.has(address)) {
                const subject = this.messageSubjects.get(address);
                if (subject) {
                    subject.complete(); // Complete the Subject to notify all subscribers
                }
                this.eventBus.unregisterHandler(address, (error : any) => {
                    if (error) {
                        console.error(`[EventBusService] Failed to unregister handler for address: "${address}"`, error);
                    } else {
                        console.log(`[EventBusService] Successfully unregistered handler for address: "${address}"`);
                        this.registeredListeners.delete(address); // Remove from registered listeners
                        this.messageSubjects.delete(address); // Remove the subject for the address
                    }
                });
            } else {
                console.warn(`[EventBusService] No active listener registered for address: "${address}"`);
            }
        }""")

@NgMethod("""
        /**
         * Utility to unregister all listeners. Useful for cleanup operations.
         */
        unregisterAllListeners(): void {
            if (this.eventBus && this.registeredListeners.size > 0) {
                Array.from(this.registeredListeners).forEach((address) => {
                    this.unregisterListener(address); // Unregister each listener
                });
            } else {
                console.warn("[EventBusService] No active listeners to unregister.");
            }
        }
        """)

@NgProvider
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
