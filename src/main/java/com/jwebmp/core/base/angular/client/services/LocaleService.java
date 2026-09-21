package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.NgProvider;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.interfaces.INgProvider;

import java.util.List;

/**
 * Application-instance locale preference. Bind locale() to Angular pipe locale
 * arguments for reactive formatting. Persistence belongs to the consumer profile flow.
 */
@NgProvider(singleton = true)
@NgImportReference(value = "Injectable, inject, LOCALE_ID, signal", reference = "@angular/core")
@NgImportReference(value = "getLocaleId", reference = "@angular/common")
@NgConstructorParameter(value = "public localeService: LocaleService", onParent = true, onSelf = false)
@NgField("""
        readonly defaultLocale = inject(LOCALE_ID);
        private readonly currentLocale = signal(this.defaultLocale);
        readonly locale = this.currentLocale.asReadonly();
        """)
@NgMethod("""
        setLocale(locale: string): void {
            // Validate before updating; missing data leaves the current preference intact.
            getLocaleId(locale);
            this.currentLocale.set(locale);
        }
        """)
@NgMethod("""
        resetLocale(): void {
            this.currentLocale.set(this.defaultLocale);
        }
        """)
public class LocaleService implements INgProvider<LocaleService>
{
    @Override
    public List<String> decorators()
    {
        List<String> out = INgProvider.super.decorators();
        out.add("@Injectable({providedIn: 'root'})");
        return out;
    }
}
