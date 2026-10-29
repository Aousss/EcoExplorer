import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  optimizeDeps: {
    // Only imported by the classifier worker; pre-bundle it so the dev server doesn't reload the page on first use
    include: ['onnxruntime-web/wasm'],
  },
})
