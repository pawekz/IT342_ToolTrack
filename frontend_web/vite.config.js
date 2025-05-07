import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    tailwindcss(),
    react()
  ],
  preview: {
    host: '0.0.0.0',
    port: 4173,
    allowedHosts: ['tooltrack-frontend-hteudjc6beaqhudr.southeastasia-01.azurewebsites.net']
  },
  define: {
    global: 'window', // this line fixes `global is not defined`
  }
})
