import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';
import { fileURLToPath, URL } from 'node:url';

const buildCommit = process.env.VERCEL_GIT_COMMIT_SHA ?? 'local';

export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    {
      name: 'build-metadata',
      transformIndexHtml() {
        return [
          {
            tag: 'meta',
            attrs: { name: 'build-commit', content: buildCommit },
            injectTo: 'head',
          },
        ];
      },
    },
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 3000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
    },
  },
});
