// After the focused Java tests and translation-runtime.mjs, install matching
// @angular/compiler-cli into target/locale-runtime and run this script.
import {readFileSync, writeFileSync, mkdirSync, copyFileSync} from 'node:fs';
import {fileURLToPath} from 'node:url';
import {spawnSync} from 'node:child_process';
import assert from 'node:assert/strict';
const runtime = new URL('../../../target/locale-runtime/', import.meta.url);
const generated = new URL('../../../../angular/target/translation-integration/', import.meta.url);
const output = new URL('aot/', runtime);
mkdirSync(output, {recursive: true});
copyFileSync(new URL('app.config.ts', generated), new URL('app.config.ts', output));
const componentPath = readFileSync(new URL('component-path.txt', generated), 'utf8');
const component = new URL(componentPath, output);
mkdirSync(new URL('./', component), {recursive: true});
copyFileSync(new URL('TranslationComponent.ts', generated), component);
copyFileSync(new URL('TranslationComponent.html', generated), new URL('TranslationComponent.html', component));
writeFileSync(new URL('TranslationComponent.scss', component), '');
for (const name of ['LocaleService', 'TranslationService']) {
    const target = new URL('com/jwebmp/core/base/angular/client/services/' + name + '/', output);
    mkdirSync(target, {recursive: true});
    copyFileSync(new URL(name + '.ts', runtime), new URL(name + '.ts', target));
}
writeFileSync(new URL('tsconfig.json', output), JSON.stringify({
    compilerOptions: {strict: true, target: 'ES2022', module: 'ES2022', moduleResolution: 'bundler',
        experimentalDecorators: true, outDir: './dist'},
    angularCompilerOptions: {strictTemplates: true, strictInjectionParameters: true},
    files: ['app.config.ts', componentPath]
}));
const result = spawnSync(process.execPath, [
    fileURLToPath(new URL('node_modules/@angular/compiler-cli/bundles/src/bin/ngc.js', runtime)),
    '-p', fileURLToPath(new URL('tsconfig.json', output))
], {encoding: 'utf8'});
assert.equal(result.status, 0, result.stdout + result.stderr);
console.log('Angular AOT passed for generated bootstrap, TranslationService, LocaleService and translation component template.');
