import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    include: ['**/*.test.ts', '**/*.property.test.ts'],
    exclude: ['**/node_modules/**', '**/dist/**'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: ['**/node_modules/**', '**/dist/**', '**/*.config.ts'],
    },
    globals: true,
    environment: 'node',
    testTimeout: 30000,
  },
})
