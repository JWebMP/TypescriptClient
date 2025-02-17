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
@NgImportReference(value = "inject", reference = "@angular/core")

@NgField("""
          @Input() appEventBusListener!: string | string[]; // The EventBus address(es) to listen on.
          @Input() appEventHandler?: (message: any, address: string) => void; // Optional: Custom handler for incoming messages.
        
          private registeredAddresses: string[] = []; // Tracks addresses managed by this directive.
        
        """)

@NgOnInit("""
           // Validate `appEventBusListener` input.
            if (!this.appEventBusListener) {
              console.warn(
                '[EventBusListenerDirective] No address provided to appEventBusListener. Please specify a single address or an array.'
              );
              return;
            }
        
            // Convert input into an array of addresses.
            const addresses = Array.isArray(this.appEventBusListener)
              ? this.appEventBusListener
              : [this.appEventBusListener];
        
            // Register total listeners in the EventBus service.
            this.eventBusService.waitForListeners(addresses.length);
        
            // Setup subscriptions for each address.
            addresses.forEach((address) => {
              if (!address || address.trim() === '') {
                console.warn(
                  `[EventBusListenerDirective] Skipping invalid EventBus address: "${address}"`
                );
                return;
              }
        
              this.registeredAddresses.push(address);
        
              // Subscribe to EventBus messages for this address via the service.
              this.eventBusService.listen(address).subscribe({
                next: (message: any) => this.processMessage(address, message),
                error: (error: any) =>
                  console.error(
                    `[EventBusListenerDirective] Error listening on address "${address}":`,
                    error
                  ),
              });
        
              console.log(
                `[EventBusListenerDirective] Listening for messages on address: "${address}"`
              );
            });
        
        """)

@NgOnDestroy("""
        
              // Cleanup logic when the directive is destroyed.
              if (this.registeredAddresses.length > 0) {
                  this.registeredAddresses.forEach((address) => {
                      console.log(
                          `[EventBusListenerDirective] Unsubscribing from address: "${address}"`
                      );
                      // Properly unregister each address from the Event Bus
                      this.eventBusService.unregisterListener(address);
                  });
              }
        
              // Reset the registration tracking list.
              this.registeredAddresses = [];
        
        """)

@NgMethod("""
          /**
           * Process incoming EventBus messages.
           * @param address - The EventBus address the message came from.
           * @param message - The message content.
           */
          private processMessage(address: string, message: any): void {
            console.log(address + ' received - ' + message);
          debugger
          this.processResult(message);
            if (this.appEventHandler) {
              // If a custom handler function is provided, invoke it.
              this.appEventHandler(message.data, address);
            } else {
              // Otherwise, default to simply logging the message.
              console.warn(
                `[EventBusListenerDirective] No handler provided for address "${address}". Message:`,
                message
              );
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
