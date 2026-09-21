package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.NgProvider;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.interfaces.INgProvider;
import java.util.List;

/** Generated app-instance translation coordinator. Enable with NgTranslations on the app. */
@NgProvider(singleton = true)
@NgImportReference(value = "Injectable, inject, signal", reference = "@angular/core")
@NgImportReference(value = "HttpClient", reference = "@angular/common/http")
@NgImportReference(value = "getLocaleId", reference = "@angular/common")
@NgImportReference(value = "TranslocoService", reference = "@jsverse/transloco")
@NgImportReference(value = "firstValueFrom, timeout", reference = "rxjs")
@NgImportReference(value = "TranslocoPipe, TranslocoDirective", reference = "@jsverse/transloco", onSelf = false, onParent = true)
@NgImportModule(value = "TranslocoPipe", onSelf = false, onParent = true)
@NgImportModule(value = "TranslocoDirective", onSelf = false, onParent = true)
@NgComponentReference(LocaleService.class)
@NgConstructorParameter(value = "public translationService: TranslationService", onSelf = false, onParent = true)
@NgField("""
        private readonly config = inject<{
            defaultLanguage: string; languages: string[]; namespaces: string[];
            bundleUrl: string; timeoutMs: number;
            sources: {namespace: string; url: string; priority: number; optional: boolean}[];
        }>('JWEBMP_TRANSLATIONS' as any);
        private readonly http = inject(HttpClient);
        private readonly engine = inject(TranslocoService);
        private readonly currentLanguage = signal(this.config.defaultLanguage);
        readonly language = this.currentLanguage.asReadonly();
        private readonly pending = signal(false);
        readonly loading = this.pending.asReadonly();
        private readonly failures = signal<string[]>([]);
        readonly errors = this.failures.asReadonly();
        private readonly contextRevision = signal(0);
        readonly contextVersion = this.contextRevision.asReadonly();
        private selection = 0;
        private readonly bundles = new Map<string, Promise<Record<string, string>>>();
        private readonly bundled = new Map<string, Record<string, string>>();
        private readonly remote = new Map<string, Record<string, string>>();
        private readonly remoteComplete = new Set<string>();
        private readonly requests = new Map<string, Promise<void>>();
        private readonly loadErrors = new Map<string, string[]>();
        private readonly custom = new Map<string, Record<string, string>>();
        """)
@NgMethod("""
        initialize(): Promise<boolean> {
            return this.setLanguage(this.config.defaultLanguage);
        }
        """)
@NgMethod("""
        setLanguage(language: string): Promise<boolean> {
            return this.activate(language);
        }
        """)
@NgMethod("""
        setLanguageAndLocale(language: string, locale: string): Promise<boolean> {
            getLocaleId(locale);
            return this.activate(language, locale);
        }
        """)
@NgMethod("""
        private async activate(language: string, locale?: string): Promise<boolean> {
            this.assertLanguage(language);
            const selection = ++this.selection;
            const context = this.contextVersion();
            this.pending.set(true);
            this.failures.set([]);
            try {
                await Promise.all([...new Set([this.config.defaultLanguage, language])]
                    .map(lang => this.loadLanguage(lang, context)));
                if (selection !== this.selection || context !== this.contextVersion()) return false;
                this.failures.set([...new Set([this.config.defaultLanguage, language])]
                    .flatMap(lang => this.loadErrors.get(lang) ?? []));
                this.publish(language);
                this.engine.setActiveLang(language);
                this.currentLanguage.set(language);
                if (locale !== undefined) this.localeService.setLocale(locale);
                return true;
            } catch (error) {
                if (selection !== this.selection || context !== this.contextVersion()) return false;
                this.failures.update(items => [...items, String(error)]);
                throw error;
            } finally {
                if (selection === this.selection) this.pending.set(false);
            }
        }
        """)
@NgMethod("""
        private loadLanguage(language: string, context: number): Promise<void> {
            const key = context + ':' + language;
            let request = this.requests.get(key);
            if (!request) {
                request = this.fetchLanguage(language, context).finally(() => this.requests.delete(key));
                this.requests.set(key, request);
            }
            return request;
        }
        """)
@NgMethod("""
        private async fetchLanguage(language: string, context: number): Promise<void> {
            await this.loadBundle(language);
            if (context !== this.contextVersion() || this.remoteComplete.has(language)) return;
            const errors: string[] = [];
            const dictionaries = await Promise.all(this.config.sources.map(async source => {
                try {
                    const url = source.url.replaceAll('{language}', encodeURIComponent(language));
                    const data = await firstValueFrom(this.http.get<unknown>(url)
                        .pipe(timeout(this.config.timeoutMs)));
                    return {source, data: this.flatten(data, source.namespace), failed: false};
                } catch (error) {
                    if (!source.optional) throw error;
                    errors.push(source.url + ': ' + String(error));
                    return {source, data: {} as Record<string, string>, failed: true};
                }
            }));
            if (context !== this.contextVersion()) return;
            this.loadErrors.set(language, errors);
            const result: Record<string, string> = Object.create(null);
            const owners = new Map<string, {priority: number; url: string}>();
            for (const item of dictionaries.sort((a, b) => a.source.priority - b.source.priority)) {
                for (const [key, value] of Object.entries(item.data)) {
                    const owner = owners.get(key);
                    if (owner && owner.priority === item.source.priority && result[key] !== value) {
                        throw new Error('Conflicting translation ' + key + ': ' + owner.url + ' / ' + item.source.url);
                    }
                    result[key] = value;
                    owners.set(key, item.source);
                }
            }
            // Failed optional sources are retried on the next selection, not cached forever.
            this.remote.set(language, result);
            if (!dictionaries.some(item => item.failed)) this.remoteComplete.add(language);
        }
        """)
@NgMethod("""
        private loadBundle(language: string): Promise<Record<string, string>> {
            let request = this.bundles.get(language);
            if (!request) {
                const url = this.config.bundleUrl.replace('{language}', encodeURIComponent(language));
                request = firstValueFrom(this.http.get<unknown>(url).pipe(timeout(this.config.timeoutMs)))
                    .then(data => {
                        const dictionary = this.flatten(data);
                        this.bundled.set(language, dictionary);
                        return dictionary;
                    }).catch(error => {
                        this.bundles.delete(language);
                        throw error;
                    });
                this.bundles.set(language, request);
            }
            return request;
        }
        """)
@NgMethod("""
        private publish(language: string, remote?: Record<string, string>): void {
            const fallback = this.config.defaultLanguage;
            const dictionary = {
                ...this.bundled.get(fallback), ...this.remote.get(fallback), ...this.custom.get(fallback),
                ...this.bundled.get(language), ...(remote ?? this.remote.get(language)), ...this.custom.get(language)
            };
            this.engine.setTranslation(dictionary, language, {merge: false});
        }
        """)
@NgMethod("""
        applyTranslations(language: string, namespace: string, data: unknown, context?: number): boolean {
            if (context !== undefined && context !== this.contextVersion()) return false;
            this.assertLanguage(language);
            if (!this.config.namespaces.includes(namespace)) throw new Error('Unknown translation namespace: ' + namespace);
            const dictionary = this.flatten(data, namespace);
            const previous = {...this.custom.get(language)};
            for (const key of Object.keys(previous)) {
                if (key.startsWith(namespace + '.')) delete previous[key];
            }
            this.custom.set(language, {...previous, ...dictionary});
            this.publish(language);
            if (language === this.config.defaultLanguage && language !== this.language()) this.publish(this.language());
            return true;
        }
        """)
@NgMethod("""
        clearContext(): void {
            ++this.selection;
            this.contextRevision.update(value => value + 1);
            this.remote.clear();
            this.remoteComplete.clear();
            this.loadErrors.clear();
            this.custom.clear();
            this.failures.set([]);
            this.pending.set(false);
            // Immediately remove previous user/tenant data, including languages already visited.
            for (const language of this.config.languages) this.publish(language);
        }
        """)
@NgMethod("""
        async reload(): Promise<boolean> {
            this.contextRevision.update(value => value + 1);
            this.remote.clear();
            this.remoteComplete.clear();
            this.loadErrors.clear();
            return this.setLanguage(this.language());
        }
        """)
@NgMethod("""
        private assertLanguage(language: string): void {
            if (!this.config.languages.includes(language)) throw new Error('Unsupported translation language: ' + language);
        }
        """)
@NgMethod("""
        private flatten(data: unknown, prefix = ''): Record<string, string> {
            const result: Record<string, string> = Object.create(null);
            const visit = (value: unknown, path: string): void => {
                if (typeof value === 'string' && path) {
                    if (Object.hasOwn(result, path)) throw new Error('Duplicate translation key: ' + path);
                    result[path] = value;
                    return;
                }
                if (value === null || typeof value !== 'object' || Array.isArray(value)) {
                    throw new Error('Expected translation object or string at ' + path);
                }
                for (const [key, child] of Object.entries(value)) {
                    if (!key || key.split('.').some(part => !part || ['__proto__', 'prototype', 'constructor'].includes(part))) {
                        throw new Error('Invalid translation key: ' + key);
                    }
                    visit(child, path ? path + '.' + key : key);
                }
            };
            if (data === null || typeof data !== 'object' || Array.isArray(data)) throw new Error('Expected translation dictionary');
            visit(data, prefix);
            return result;
        }
        """)
public class TranslationService implements INgProvider<TranslationService>
{
    @Override
    public List<String> decorators()
    {
        return List.of("@Injectable({providedIn: 'root'})");
    }
}
