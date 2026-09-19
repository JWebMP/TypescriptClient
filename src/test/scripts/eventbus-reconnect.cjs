// Run: node src/test/scripts/eventbus-reconnect.cjs <Angular app with node_modules> [EventBusService.java]
// Exercises the Java-authored TypeScript against real RxJS, StompJS and a local WebSocket broker.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { createRequire } = require('node:module');
const { test } = require('node:test');
const app = path.resolve(process.argv[2]);
const appRequire = createRequire(path.join(app, 'package.json'));
const ts = appRequire('typescript');
const stomp = appRequire('@stomp/stompjs');
const rx = appRequire('rxjs');
const { WebSocket, WebSocketServer } = appRequire('ws');
const sourcePath = process.argv[3] || path.resolve(__dirname, '../../main/java/com/jwebmp/core/base/angular/client/services/EventBusService.java');
const java = fs.readFileSync(sourcePath, 'utf8').replace(/\/\*[\s\S]*?\*\//g, '');
const decode = value => value.replace(/\\([\\"nrt])/g, (_, c) => ({ '\\': '\\', '"': '"', n: '\n', r: '\r', t: '\t' }[c]));
const blocks = name => [...java.matchAll(new RegExp('@' + name + '\\("""([\\s\\S]*?)"""\\)', 'g'))].map(m => decode(m[1])).join('\n');
const constructorBodies = [...java.matchAll(/@NgConstructorBody\(("""[\s\S]*?"""|"(?:\\.|[^"\\])*"(?:\s*\+\s*"(?:\\.|[^"\\])*")*)\)/g)]
    .map(m => m[1].startsWith('"""') ? decode(m[1].slice(3, -3)) : [...m[1].matchAll(/"((?:\\.|[^"\\])*)"/g)].map(s => decode(s[1])).join('')).join('\n');
const source = `
import { Client, StompSubscription, ReconnectionTimeMode, TickerStrategy } from '@stomp/stompjs';
import { BehaviorSubject, Subject, Observable, takeUntil, filter, take, timer } from 'rxjs';
type ElementRef<T> = { nativeElement: T };
type Location = { path(): string; getState(): object };
type Router = { navigateByUrl(url: string): void };
type ActivatedRoute = object;
type ContextIdService = { setContextId(id: string): void; contextId(): string; getContextIdObservable(): Observable<string> };
export class EventBusService {
${blocks('NgField')}
constructor(private routeLocation: Location, private router: Router, private route: ActivatedRoute, private contextIdService: ContextIdService) {
${constructorBodies}
}
${blocks('NgMethod')}
ngOnDestroy() { ${blocks('NgOnDestroy')} }
}`;

// Type-check the actual annotation bodies against the installed STOMP/RxJS declarations.
const virtualFile = path.join(app, 'eventbus-reconnect-check.ts');
const options = { noEmit: true, target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.ESNext,
    moduleResolution: ts.ModuleResolutionKind.Bundler, skipLibCheck: true, strict: true, strictPropertyInitialization: false };
const host = ts.createCompilerHost(options);
const originalGet = host.getSourceFile.bind(host);
host.getSourceFile = (file, language, ...rest) => path.resolve(file) === virtualFile
    ? ts.createSourceFile(file, source, language, true) : originalGet(file, language, ...rest);
const diagnostics = ts.getPreEmitDiagnostics(ts.createProgram([virtualFile], options, host));
// Optional only for reproducing runtime failures in historical sources with unrelated TS errors.
if (!process.env.EVENTBUS_SKIP_TYPECHECK) {
    assert.equal(diagnostics.length, 0, ts.formatDiagnosticsWithColorAndContext(diagnostics, host));
}
const js = ts.transpileModule(source, { compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS } }).outputText;
const pause = ms => new Promise(resolve => setTimeout(resolve, ms));
async function until(predicate, label) {
    const deadline = Date.now() + 4000;
    while (!predicate()) {
        assert.ok(Date.now() < deadline, `Timed out: ${label}`);
        await pause(10);
    }
}

async function fixture(t, { ignoreFirstConnect = false } = {}) {
    const server = new WebSocketServer({ port: 0, host: '127.0.0.1' });
    await new Promise(resolve => server.once('listening', resolve));
    const connections = [];
    let allowConnect = true;
    let heartbeats = true;
    server.on('connection', socket => {
        const connection = { socket, subscriptions: new Map(), frames: [], connected: false };
        connections.push(connection);
        socket.on('message', bytes => {
            for (const raw of bytes.toString().split('\0')) {
                const frame = raw.replace(/^\n+/, '');
                if (!frame) continue;
                const [header, body] = frame.split('\n\n');
                const [command, ...lines] = header.split('\n');
                const headers = Object.fromEntries(lines.map(line => [line.slice(0, line.indexOf(':')), line.slice(line.indexOf(':') + 1)]));
                connection.frames.push({ command, headers, body });
                if (command === 'CONNECT' && allowConnect && !(ignoreFirstConnect && connections.length === 1)) {
                    connection.connected = true;
                    socket.send('CONNECTED\nversion:1.2\nheart-beat:20,20\n\n\0');
                }
                if (command === 'SUBSCRIBE') connection.subscriptions.set(headers.id, headers.destination);
                if (command === 'UNSUBSCRIBE') connection.subscriptions.delete(headers.id);
            }
        });
    });
    const heartbeatTimer = setInterval(() => {
        for (const c of connections) if (heartbeats && c.connected && c.socket.readyState === WebSocket.OPEN) c.socket.send('\n');
    }, 20);
    const clients = [];
    class TestClient extends stomp.Client {
        constructor(config) {
            super({ ...config, brokerURL: `ws://127.0.0.1:${server.address().port}`,
                // Accelerate time, retaining the production callbacks and retry/heartbeat mechanisms.
                reconnectDelay: 30, maxReconnectDelay: 120, connectionTimeout: 100,
                heartbeatIncoming: 50, heartbeatOutgoing: 50, webSocketFactory: () => new WebSocket(`ws://127.0.0.1:${server.address().port}`) });
            clients.push(this);
        }
    }
    const exports = {};
    const location = { protocol: 'http:', host: 'localhost', search: '', hash: '' };
    vm.runInNewContext(js, { exports, require: name => name === '@stomp/stompjs' ? { ...stomp, Client: TestClient } : appRequire(name),
        console: { log() {}, warn() {}, error() {} }, setTimeout, clearTimeout, setInterval, clearInterval,
        window: { location, localStorage: {}, sessionStorage: {} }, location, history: { state: {} }, navigator: {}, Date });
    const context = new rx.BehaviorSubject('');
    const service = new exports.EventBusService({ path: () => '/', getState: () => ({}) }, {}, {}, {
        setContextId: id => context.next(id), contextId: () => context.value, getContextIdObservable: () => context.asObservable()
    });
    t.after(async () => {
        service.disconnect();
        await Promise.all(clients.map(client => client.deactivate({ force: true })));
        clearInterval(heartbeatTimer);
        server.clients.forEach(socket => socket.terminate());
        await new Promise(resolve => server.close(resolve));
    });
    const latest = () => connections[connections.length - 1];
    const ready = () => service.connectionState$.value && latest()?.subscriptions.size >= 8;
    const publish = (address, data) => {
        for (const [id, destination] of latest().subscriptions) {
            if (destination === `/toStomp/${address}`) latest().socket.send(`MESSAGE\nsubscription:${id}\nmessage-id:test\ndestination:${destination}\n\n${JSON.stringify(data)}\0`);
        }
    };
    return { service, clients, connections, context, latest, ready, publish,
        setAllowConnect: value => { allowConnect = value; }, setHeartbeats: value => { heartbeats = value; } };
}

test('raw socket closes restore public/private channels and queued data through three cycles', async t => {
    const f = await fixture(t);
    const received = [];
    f.service.listen('updates', 'handler').subscribe(value => received.push(value));
    await until(() => f.ready() && f.latest().subscriptions.size === 10, 'initial subscriptions');
    for (let cycle = 0; cycle < 3; cycle++) {
        f.setAllowConnect(false);
        f.latest().socket.terminate();
        await until(() => !f.service.connectionState$.value, 'raw close marks offline');
        f.service.send('data', { cycle }, 'updates');
        assert.equal(f.service.messageQueue.length, 1);
        f.service.listen('removed', 'temporary');
        f.service.unregisterListener('removed', 'temporary');
        f.setAllowConnect(true);
        await until(() => f.ready() && f.latest().frames.some(frame => frame.command === 'SEND'), 'reconnect and flush');
        const frames = f.latest().frames;
        assert.ok(frames.findIndex(frame => frame.command === 'SEND') > frames.map(frame => frame.command).lastIndexOf('SUBSCRIBE'));
        assert.equal(f.latest().subscriptions.size, 10);
        assert.equal(f.service.messageQueue.length, 0);
        f.publish('updates', cycle);
        f.publish(`${f.service.guid}.updates`, cycle);
        await until(() => received.length === (cycle + 1) * 2, 'public and private delivery');
    }
    assert.equal(f.clients.length, 1, 'one client owns every retry');
});

test('connection timeouts retry using the same client', async t => {
    const f = await fixture(t, { ignoreFirstConnect: true });
    await until(f.ready, 'recovery after missing CONNECTED frame');
    assert.ok(f.connections.length >= 2);
    assert.equal(f.clients.length, 1);
});

test('heartbeat loss recovers subscriptions and data delivery', async t => {
    const f = await fixture(t);
    let received = false;
    f.service.listen('updates', 'handler').subscribe(() => { received = true; });
    await until(f.ready, 'initial connection');
    const count = f.connections.length;
    f.setHeartbeats(false);
    await until(() => f.connections.length > count, 'heartbeat recovery');
    f.setHeartbeats(true);
    await until(() => f.ready() && f.latest().subscriptions.size === 10, 'restored channels');
    f.publish('updates', 'resumed');
    await until(() => received, 'delivery after heartbeat timeout');
});

test('repeated context IDs preserve private subscriptions', async t => {
    const f = await fixture(t);
    await until(f.ready, 'initial connection');
    f.context.next('server-context');
    await until(() => f.latest().subscriptions.size === 12, 'server context channels');
    f.context.next('server-context');
    await pause(40);
    assert.equal(f.latest().subscriptions.size, 12);
});

test('shutdown during reconnect cancels future attempts', async t => {
    const f = await fixture(t);
    await until(f.ready, 'initial connection');
    f.latest().socket.terminate();
    await until(() => !f.service.connectionState$.value, 'offline state');
    f.service.ngOnDestroy();
    const count = f.connections.length;
    await pause(250);
    assert.equal(f.connections.length, count);
    assert.equal(f.clients[0].active, false);
    assert.equal(f.service.messageSubjects.size, 0);
});
