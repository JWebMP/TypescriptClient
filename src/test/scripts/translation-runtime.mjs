// First run mvn test -Dtest=LocaleServiceRenderingTest.
// Install Angular 21, TypeScript 5.9, RxJS 7, @jsverse/transloco@8.4.0 and
// @jsverse/transloco-messageformat@8.4.0 into target/locale-runtime, then run this file.
import assert from 'node:assert/strict';
import {readFileSync, writeFileSync, mkdirSync, copyFileSync} from 'node:fs';
import {createRequire} from 'node:module';
import {fileURLToPath, pathToFileURL} from 'node:url';
const directory = new URL('../../../target/locale-runtime/', import.meta.url);
const require = createRequire(new URL('package.json', directory));
const ts = require('typescript');
const load = name => import(pathToFileURL(require.resolve(name)).href);
await load('@angular/compiler');
const {Injector, LOCALE_ID, inject} = await load('@angular/core');
const {registerLocaleData} = await load('@angular/common');
const {HttpClient} = await load('@angular/common/http');
const {TranslocoService, provideTransloco} = await load('@jsverse/transloco');
const {provideTranslocoMessageformat} = await load('@jsverse/transloco-messageformat');
const {of, throwError, Subject} = await load('rxjs');
registerLocaleData((await load('@angular/common/locales/en-ZA')).default, 'en-ZA');
registerLocaleData((await load('@angular/common/locales/de')).default, 'de');
const files = [];
for (const name of ['LocaleService', 'TranslationService']) {
    const folder = new URL('generated/' + name + '/', directory);
    mkdirSync(folder, {recursive: true});
    const file = new URL(name + '.ts', folder);
    copyFileSync(new URL(name + '.ts', directory), file);
    files.push(fileURLToPath(file));
}
const options = {noEmit: true, strict: true, experimentalDecorators: true,
    target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.ESNext,
    moduleResolution: ts.ModuleResolutionKind.Bundler};
const diagnostics = ts.getPreEmitDiagnostics(ts.createProgram(files, options));
assert.equal(diagnostics.length, 0, ts.formatDiagnosticsWithColorAndContext(diagnostics, {
    getCurrentDirectory: () => process.cwd(), getCanonicalFileName: f => f, getNewLine: () => '\n'
}));
for (const file of files) {
    const javascript = ts.transpileModule(readFileSync(file, 'utf8'), {compilerOptions: options}).outputText
        .replace("'../LocaleService/LocaleService'", "'../LocaleService/LocaleService.mjs'");
    writeFileSync(file.replace(/\.ts$/, '.mjs'), javascript);
}
const {LocaleService} = await import(new URL('generated/LocaleService/LocaleService.mjs', directory).href);
const {TranslationService} = await import(new URL('generated/TranslationService/TranslationService.mjs', directory).href);
const defaults = {
    defaultLanguage: 'en', languages: ['en', 'de', 'fr'], namespaces: ['orders'],
    bundleUrl: '/bundles/{language}.json', timeoutMs: 1000, sources: []
};
const bundles = {
    '/bundles/en.json': {'orders.save': 'Save', 'orders.cancel': 'Cancel',
        'orders.welcome': 'Hello {{name}}', 'orders.count': '{count, plural, one {# item} other {# items}}'},
    '/bundles/de.json': {'orders.save': 'Speichern'},
    '/bundles/fr.json': {'orders.save': 'Enregistrer'}
};
function app(config = {}, responses = {}) {
    const calls = [];
    const values = {...bundles, ...responses};
    const injector = Injector.create({providers: [
        {provide: LOCALE_ID, useValue: 'en-ZA'},
        provideTransloco({config: {defaultLang: 'en', availableLangs: ['en', 'de', 'fr'], reRenderOnLangChange: true}}),
        provideTranslocoMessageformat(), TranslocoService,
        {provide: 'JWEBMP_TRANSLATIONS', useValue: {...defaults, ...config}},
        {provide: HttpClient, useValue: {get(url) {
            calls.push(url);
            const value = values[url];
            if (value instanceof Subject) return value;
            return value === undefined || value instanceof Error ? throwError(() => value ?? new Error('404 ' + url)) : of(value);
        }}},
        {provide: LocaleService, useFactory: () => new LocaleService()},
        {provide: TranslationService, useFactory: () => new TranslationService(inject(LocaleService))}
    ]});
    return {injector, service: injector.get(TranslationService), engine: injector.get(TranslocoService),
        locale: injector.get(LocaleService), calls, values};
}
const first = app();
assert.equal(await first.service.initialize(), true);
assert.equal(first.engine.translate('orders.welcome', {name: 'Marc'}), 'Hello Marc');
assert.equal(first.engine.translate('orders.count', {count: 2}), '2 items');
assert.equal(await first.service.setLanguage('de'), true);
assert.equal(first.engine.translate('orders.save'), 'Speichern');
assert.equal(first.engine.translate('orders.cancel'), 'Cancel');
assert.equal(first.locale.locale(), 'en-ZA');
await first.service.setLanguageAndLocale('de', 'de');
assert.equal(first.locale.locale(), 'de');
assert.throws(() => first.service.setLanguageAndLocale('en', 'zz-ZZ'));
assert.equal(first.service.language(), 'de');
const context = first.service.contextVersion();
const emitted = [];
const subscription = first.engine.selectTranslate('orders.save').subscribe(value => emitted.push(value));
first.service.applyTranslations('de', 'orders', {save: 'Custom'}, context);
assert.equal(emitted.at(-1), 'Custom');
first.service.applyTranslations('de', 'orders', {other: 'Other'}, context);
assert.equal(first.engine.translate('orders.save'), 'Speichern');
first.service.clearContext();
assert.equal(first.service.applyTranslations('de', 'orders', {save: 'Stale'}, context), false);
assert.equal(first.engine.translate('orders.save'), 'Speichern');
assert.equal(first.calls.filter(url => url === '/bundles/de.json').length, 1);
subscription.unsubscribe();
const second = app();
await second.service.initialize();
assert.equal(second.service.language(), 'en');
assert.equal(second.engine.translate('orders.save'), 'Save');

const remoteSource = {namespace: 'orders', url: '/rest/{language}', priority: 100, optional: true};
const remote = app({sources: [remoteSource]}, {'/rest/en': {save: 'Remote save'}});
await remote.service.setLanguage('de');
assert.equal(remote.engine.translate('orders.save'), 'Speichern');
assert.ok(remote.service.errors().length > 0);
remote.values['/rest/de'] = {save: 'Remote DE'};
await remote.service.setLanguage('de');
assert.equal(remote.engine.translate('orders.save'), 'Remote DE');
remote.service.applyTranslations('de', 'orders', {save: 'Profile DE'}, remote.service.contextVersion());
assert.equal(remote.engine.translate('orders.save'), 'Profile DE');
remote.service.clearContext();
assert.equal(remote.engine.translate('orders.save'), 'Speichern');

const pendingGerman = new Subject();
const race = app({}, {'/bundles/de.json': pendingGerman});
await race.service.initialize();
const oldSelection = race.service.setLanguage('de');
assert.equal(await race.service.setLanguage('fr'), true);
pendingGerman.next({'orders.save': 'Late DE'});
pendingGerman.complete();
assert.equal(await oldSelection, false);
assert.equal(race.service.language(), 'fr');
assert.equal(race.engine.translate('orders.save'), 'Enregistrer');

const pendingRemote = new Subject();
const tenant = app({sources: [remoteSource]}, {'/rest/en': pendingRemote});
const oldContext = tenant.service.initialize();
await new Promise(resolve => setImmediate(resolve));
tenant.service.clearContext();
pendingRemote.next({save: 'Previous tenant'});
pendingRemote.complete();
assert.equal(await oldContext, false);
assert.equal(tenant.engine.translate('orders.save'), 'Save');

const required = app({sources: [{...remoteSource, optional: false}]});
await assert.rejects(required.service.initialize());
assert.equal(required.service.loading(), false);
const conflict = app({sources: [remoteSource, {...remoteSource, url: '/other/{language}'}]}, {
    '/rest/en': {save: 'One'}, '/other/en': {save: 'Two'}
});
await assert.rejects(conflict.service.initialize(), /Conflicting translation/);
for (const instance of [first, second, remote, race, tenant, required, conflict]) instance.injector.destroy();
console.log('Translation runtime passed: strict TS, interpolation, ICU plurals, fallback, reactive overrides, reset, isolation, request races, optional/required HTTP failures and source conflicts.');
