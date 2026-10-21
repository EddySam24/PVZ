import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// El build se escribe directo en los recursos estaticos de Spring Boot.
export default defineConfig({
  plugins: [react()],
  build: { outDir: '../src/main/resources/static', emptyOutDir: true },
  server: { port: 5173, proxy: { '/api': 'http://localhost:8080' } }
});
