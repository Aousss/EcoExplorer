// Loads quizzes and fun facts (tools/emulator-seed.json, a copy of the public app content) into the
// local Database emulator. Run after `firebase emulators:start`:  node tools/seed-emulator.mjs
import { readFile } from 'node:fs/promises'

const NAMESPACE = 'ecoexplorer-u2001264-default-rtdb'
const seed = JSON.parse(await readFile(new URL('./emulator-seed.json', import.meta.url), 'utf8'))

for (const [path, value] of Object.entries(seed)) {
  // "Bearer owner" is the emulator's admin token, so the write bypasses database.rules.json
  const response = await fetch(`http://127.0.0.1:9000/${path}.json?ns=${NAMESPACE}`, {
    method: 'PUT',
    headers: { Authorization: 'Bearer owner' },
    body: JSON.stringify(value),
  })
  console.log(`${path}: ${response.ok ? 'seeded' : `failed (HTTP ${response.status})`}`)
}
