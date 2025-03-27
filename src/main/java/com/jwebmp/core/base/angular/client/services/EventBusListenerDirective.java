package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.NgDirective;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDirective;

import java.util.List;

@NgDirective(value = "[appEventBusListener]", standalone = true)

@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "Input", reference = "@angular/core")
@NgImportReference(value = "inject", reference = "@angular/core", onParent = true)

@NgField("""
          @Input() appEventBusListener!: string | string[]; // The EventBus address(es) to listen on.
          @Input() appEventHandler?: (message: any, address: string) => void; // Optional: Custom handler for incoming messages.
        
          private registeredAddresses: string[] = [] // Tracks addresses managed by this directive.
        
          private registeredHandlers: { address: string; handlerId: string }[] = []; // Tracks registered handler IDs
        
        """)

@NgOnInit("""
              // Validate and normalize `appEventBusListener` input.
              const addresses = this.parseAddresses(this.appEventBusListener);
              if (!addresses || addresses.length === 0) {
                  console.warn('[EventBusListenerDirective] No valid addresses provided to appEventBusListener.');
                  return;
              }
        
              // Register listeners for all addresses
              addresses.forEach((address) => {
                  // Generate a unique handler ID for the current address
                  const handlerId = Math.random().toString(36).substr(2, 9);
        
                  // Register the listener on the EventBus and handle messages
                  this.eventBusService.listen(address, handlerId).subscribe((message) => {
                      this.processMessage(address, message);
                  });
        
                  // Keep track of the address and handler ID for cleanup
                  this.registeredHandlers.push({ address, handlerId });
              });
        """)
@NgMethod("""
                /**
                 * Parse and normalize the `appEventBusListener` input into an array of strings.
                 * @param input The raw input provided to `appEventBusListener`.
                 * @returns An array of normalized addresses.
                 */
                private parseAddresses(input: string | string[]): string[] {
                    if (Array.isArray(input)) {
                        // If input is already an array, return it (filter out empty or invalid values).
                        return input.filter((address) => address && address.trim() !== '');
                    }
        
                    if (typeof input === 'string') {
                        // Handle stringified array format: "['String1', 'String2']"
                        if (input.startsWith("[") && input.endsWith("]")) {
                            try {
                                const parsedArray = JSON.parse(input); // Parse the stringified array.
                                if (Array.isArray(parsedArray)) {
                                    return parsedArray.filter((address) => address && address.trim() !== ''); // Filter empty values.
                                }
                            } catch (error) {
                                console.warn(`[parseAddresses] Failed to parse stringified array: "${input}". Falling back.`);
                            }
                        }
        
                        // Handle comma-separated values: "SingleListenerAddress,SecondAddress"
                        if (input.includes(',')) {
                            return input
                                .split(',')
                                .map((address) => address.trim()) // Remove extra spaces.
                                .filter((address) => address !== ''); // Filter empty values.
                        }
        
                        // Otherwise, treat it as a single string address.
                        return [input.trim()];
                    }
        
                    // Unsupported input type, return an empty array.
                    return [];
                }
        """)

@NgOnDestroy("""
        // Unregister all handlers tracked by this directive
            this.registeredHandlers.forEach(({ address, handlerId }) => {
                console.log(`[EventBusListenerDirective] Unsubscribing handler "${handlerId}" from address: "${address}"`);
                this.eventBusService.unregisterListener(address, handlerId);
            });
            this.registeredHandlers = [];
        
        """)

@NgMethod("""
            /**
             * Process incoming EventBus messages.
             * @param address - The EventBus address the message came from.
             * @param message - The message content.
             */
            private processMessage(address: string, message: any): void {
        //      console.log(address + ' received - ' + message);
           // debugger
            this.processResult(message);
              if (this.appEventHandler) {
                // If a custom handler function is provided, invoke it.
                this.appEventHandler(message.data, address);
              } else {
                // Otherwise, default to simply logging the message.
               /* console.warn(
                  `[EventBusListenerDirective] No handler provided for address "${address}". Message:`,
                  message
                );*/
              }
            }
        
        """)

@NgMethod("""
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
           }""")

@NgComponentReference(EventBusService.class)
@NgComponentReference(ContextIdService.class)
@NgConstructorParameter("private router: Router")
@NgConstructorParameter("private contextIdService : ContextIdService")
@NgImportReference(value = "RouterModule, ParamMap,Router", reference = "@angular/router")
public class EventBusListenerDirective implements INgDirective<EventBusListenerDirective>
{

}
