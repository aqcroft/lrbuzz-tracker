// LR Buzz 321 Tracker service worker
// Strategy: network-first for same-origin (index.html, config.js) so hosts
// always get the latest version when online. Cache is only a fallback.
// Bump CACHE when you ship changes so old caches are cleared on next open.

const CACHE = 'lrbuzz-v2026.08';

const CORE = [
  './',
  './index.html',
  './config.js',
  './manifest.json',
  './icon-192.png',
  './icon-512.png',
  './icon-maskable-512.png'
];

// Install: pre-cache core files, take over immediately (auto-update on next use)
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE).then((c) => c.addAll(CORE)).catch(() => {})
  );
  self.skipWaiting();
});

// Allow the page's "Check for updates" button to force activation
self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SKIP_WAITING') self.skipWaiting();
});

// Activate: drop old caches, claim open clients
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys()
      .then((keys) => Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

// Fetch: network-first for same-origin, opportunistic cache for the rest
self.addEventListener('fetch', (event) => {
  const req = event.request;
  if (req.method !== 'GET') return; // never touch POSTs to Google Sheets

  const url = new URL(req.url);

  if (url.origin === self.location.origin) {
    // Same-origin (app shell + config): always try network first, cache as backup
    event.respondWith(
      fetch(req)
        .then((res) => {
          const copy = res.clone();
          caches.open(CACHE).then((c) => c.put(req, copy)).catch(() => {});
          return res;
        })
        .catch(() => caches.match(req).then((r) => r || caches.match('./index.html')))
    );
  } else {
    // Cross-origin (CDN libraries, etc.): network first, fall back to cache if offline
    event.respondWith(
      fetch(req)
        .then((res) => {
          const copy = res.clone();
          caches.open(CACHE).then((c) => c.put(req, copy)).catch(() => {});
          return res;
        })
        .catch(() => caches.match(req))
    );
  }
});
