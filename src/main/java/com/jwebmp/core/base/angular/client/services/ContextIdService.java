package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.NgProvider;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.interfaces.INgProvider;

import java.util.List;

@NgProvider
@NgImportReference(value = "BehaviorSubject,Observable", reference = "rxjs")
@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgField("private readonly storageKey = 'contextId';")
@NgField("private contextIdSubject: BehaviorSubject<string | null>;")
@NgOnDestroy("")
@NgConstructorBody("""
        // Initialize the subject with the current sessionStorage value
            const initialContextId = sessionStorage.getItem(this.storageKey);
            this.contextIdSubject = new BehaviorSubject<string | null>(initialContextId);
        
            // Listen for `storage` event to update for cross-tab changes
            window.addEventListener('storage', this.handleStorageEvent.bind(this));
        """)
@NgMethod("""
          // Get an observable for contextId updates
             getContextIdObservable(): Observable<string | null> {
               return this.contextIdSubject.asObservable();
             }
        
             // Update contextId both in BehaviorSubject and sessionStorage
             setContextId(newContextId: string): void {
               sessionStorage.setItem(this.storageKey, newContextId);
               this.contextIdSubject.next(newContextId); // Emit to all subscribers
             }
        
             // Clear contextId and update BehaviorSubject
             clearContextId(): void {
               sessionStorage.removeItem(this.storageKey);
               this.contextIdSubject.next(null); // Emit null to subscribers
             }
        
             // Cleanup event listener (e.g., if service is destroyed, which is unlikely in app-wide scope)
             ngOnDestroy(): void {
               window.removeEventListener('storage', this.handleStorageEvent.bind(this));
             }
        
             // Handle `storage` event from other tabs/windows
             private handleStorageEvent(event: StorageEvent): void {
               if (event.storageArea === sessionStorage && event.key === this.storageKey) {
                 this.contextIdSubject.next(event.newValue); // Emit updated value to subscribers
               }
             }
        
        
        """)
@NgConstructorParameter(value = "private contextIdService : ContextIdService", onParent = true, onSelf = false)
public class ContextIdService implements INgProvider<ContextIdService> {
    @Override
    public List<String> decorators() {
        List<String> out = INgProvider.super.decorators();
        out.add("@Injectable({\n" +
                "  providedIn: 'root'\n" +
                "})");
        return out;
    }
}
