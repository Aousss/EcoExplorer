import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { VitePWA } from 'vite-plugin-pwa'

export default defineConfig({
  plugins: [
    react(),
    VitePWA({
      // New versions install in the background and are used from the next visit
      registerType: 'autoUpdate',
      manifest: {
        name: 'EcoExplorer',
        short_name: 'EcoExplorer',
        description: 'Identify plants and insects, explore ecosystems in AR, and test your knowledge.',
        lang: 'en',
        start_url: '/',
        scope: '/',
        display: 'standalone',
        orientation: 'portrait',
        theme_color: '#01331a',
        background_color: '#01331a',
        categories: ['education'],
        icons: [
          { src: '/icons/icon-192.png', sizes: '192x192', type: 'image/png' },
          { src: '/icons/icon-512.png', sizes: '512x512', type: 'image/png' },
          { src: '/icons/icon-maskable-512.png', sizes: '512x512', type: 'image/png', purpose: 'maskable' },
        ],
        shortcuts: [
          { name: 'Identify', url: '/identify', icons: [{ src: '/icons/icon-192.png', sizes: '192x192' }] },
          { name: 'AR Explore', url: '/explore', icons: [{ src: '/icons/icon-192.png', sizes: '192x192' }] },
          { name: 'Challenge', url: '/challenge', icons: [{ src: '/icons/icon-192.png', sizes: '192x192' }] },
        ],
      },
      workbox: {
        // The app shell and images are cached on install (about 1.5 MB). The 15 MB WASM runtime is cached
        // the first time Identify runs, and the classifier worker keeps the model in its own cache.
        globPatterns: ['**/*.{js,mjs,css,html,png,svg,jpg,jpeg}'],
        navigateFallback: '/index.html',
        // Firebase Hosting's reserved URLs (e.g. /__/auth/handler) must reach the network
        navigateFallbackDenylist: [/^\/__\//],
        cleanupOutdatedCaches: true,
        runtimeCaching: [
          {
            urlPattern: ({ url }) => url.pathname.endsWith('.wasm'),
            handler: 'CacheFirst',
            options: { cacheName: 'ecoexplorer-wasm', expiration: { maxEntries: 1 } },
          },
          {
            urlPattern: ({ url }) => url.pathname.startsWith('/ar/'),
            handler: 'StaleWhileRevalidate',
            options: { cacheName: 'ecoexplorer-ar' },
          },
        ],
      },
    }),
  ],
  optimizeDeps: {
    // Only imported by the classifier worker; pre-bundle it so the dev server doesn't reload the page on first use
    include: ['onnxruntime-web/wasm'],
  },
})
