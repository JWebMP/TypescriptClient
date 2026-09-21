// Run after mvn test -Dtest=LocaleServiceRenderingTest and installing Angular/TypeScript
// into target/locale-runtime: node src/test/scripts/locale-runtime.mjs
import assert from 'node:assert/strict';
import {readFileSync, writeFileSync} from 'node:fs';
import {createRequire} from 'node:module';
import {fileURLToPath, pathToFileURL} from 'node:url';
const directory = new URL('../../../target/locale-runtime/', import.meta.url);
const require = createRequire(new URL('package.json', directory));
const ts = require('typescript');
const load = name => import(pathToFileURL(require.resolve(name)).href);
await load('@angular/compiler');
const {Injector, LOCALE_ID, computed} = await load('@angular/core');
const {registerLocaleData, DatePipe, DecimalPipe, CurrencyPipe} = await load('@angular/common');
for (const id of ['en-ZA', 'de', 'fr']) {
    registerLocaleData((await load('@angular/common/locales/' + id)).default, id);
}
const file = fileURLToPath(new URL('LocaleService.ts', directory));
const options = {
    noEmit: true, strict: true, experimentalDecorators: true,
    target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.ESNext,
    moduleResolution: ts.ModuleResolutionKind.Bundler
};
const diagnostics = ts.getPreEmitDiagnostics(ts.createProgram([file], options));
assert.equal(diagnostics.length, 0, ts.formatDiagnosticsWithColorAndContext(diagnostics, {
    getCurrentDirectory: () => process.cwd(), getCanonicalFileName: f => f, getNewLine: () => '\n'
}));
const javascript = ts.transpileModule(readFileSync(file, 'utf8'), {compilerOptions: options}).outputText;
const moduleUrl = new URL('LocaleService.mjs', directory);
writeFileSync(moduleUrl, javascript);
const {LocaleService} = await import(moduleUrl.href);
const makeApp = () => Injector.create({providers: [
    {provide: LOCALE_ID, useValue: 'en-ZA'},
    {provide: LocaleService, useFactory: () => new LocaleService()}
]});
const app = makeApp();
const otherApp = makeApp();
const service = app.get(LocaleService);
assert.equal(service.locale(), 'en-ZA');
assert.equal(service.locale.set, undefined);
const date = new DatePipe('en-ZA');
const number = new DecimalPipe('en-ZA');
const currency = new CurrencyPipe('en-ZA');
const formatted = computed(() => [
    date.transform('2026-09-20T12:00:00Z', 'longDate', 'UTC', service.locale()),
    number.transform(1234.5, '1.2-2', service.locale()),
    currency.transform(1234.5, 'ZAR', 'symbol', '1.2-2', service.locale())
]);
const initial = formatted();
service.setLocale('de');
assert.equal(formatted()[0], '20. September 2026');
assert.equal(formatted()[1], '1.234,50');
assert.notEqual(formatted()[2], initial[2]);
service.setLocale('fr');
assert.equal(formatted()[0], '20 septembre 2026');
assert.throws(() => service.setLocale('zz-ZZ'));
assert.equal(service.locale(), 'fr');
assert.equal(otherApp.get(LocaleService).locale(), 'en-ZA');
assert.equal(app.get(LOCALE_ID), 'en-ZA');
service.resetLocale();
assert.deepEqual(formatted(), initial);
app.destroy();
otherApp.destroy();
console.log('Locale runtime passed: strict TypeScript, reactive date/number/currency, repeated switching, invalid locale, isolation, reset.');
