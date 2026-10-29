// Runs the species model off the main thread so the page stays responsive.
import * as ort from 'onnxruntime-web/wasm'
import wasmUrl from 'onnxruntime-web/ort-wasm-simd-threaded.wasm?url'
import wasmModuleUrl from 'onnxruntime-web/ort-wasm-simd-threaded.mjs?url'
import { IMAGE_SIZE, imageToTensorData, softmax } from './preprocess'
import type { WorkerRequest, WorkerResponse } from './messages'

const MODEL_URL = '/models/model.onnx'
// Bump when model.onnx changes so browsers download the new one
const MODEL_CACHE = 'ecoexplorer-model-v1'

ort.env.wasm.wasmPaths = { wasm: wasmUrl, mjs: wasmModuleUrl }
// Multi-threading needs cross-origin isolation headers, which the site doesn't use
ort.env.wasm.numThreads = self.crossOriginIsolated ? Math.min(4, navigator.hardwareConcurrency) : 1

let session: Promise<ort.InferenceSession> | null = null

const post = (message: WorkerResponse) => self.postMessage(message)

async function downloadModel(): Promise<Uint8Array> {
  const cache = 'caches' in self ? await caches.open(MODEL_CACHE).catch(() => null) : null
  let response = await cache?.match(MODEL_URL)
  if (!response) {
    response = await fetch(MODEL_URL)
    if (!response.ok) throw new Error(`Model download failed (HTTP ${response.status})`)
    await cache?.put(MODEL_URL, response.clone()).catch(() => undefined)
  }

  const total = Number(response.headers.get('content-length')) || 0
  const reader = response.body!.getReader()
  const chunks: Uint8Array[] = []
  let loaded = 0
  for (;;) {
    const { done, value } = await reader.read()
    if (done) break
    chunks.push(value)
    loaded += value.length
    post({ type: 'progress', loaded, total })
  }

  const model = new Uint8Array(loaded)
  let offset = 0
  for (const chunk of chunks) {
    model.set(chunk, offset)
    offset += chunk.length
  }
  return model
}

function loadSession() {
  session ??= downloadModel().then((model) => ort.InferenceSession.create(model, { executionProviders: ['wasm'] }))
  session.catch(() => (session = null)) // allow a retry after a failure
  return session
}

self.onmessage = async ({ data: request }: MessageEvent<WorkerRequest>) => {
  try {
    if (request.type === 'load') {
      await loadSession()
      post({ type: 'ready' })
      return
    }

    const model = await loadSession()
    const input = new ort.Tensor('float32', await imageToTensorData(request.image), [1, 3, IMAGE_SIZE, IMAGE_SIZE])
    const output = await model.run({ [model.inputNames[0]]: input })
    const logits = output[model.outputNames[0]].data as Float32Array
    post({ type: 'result', id: request.id, probabilities: softmax(logits) })
  } catch (error) {
    post({
      type: 'error',
      id: request.type === 'classify' ? request.id : undefined,
      message: error instanceof Error ? error.message : String(error),
    })
  }
}
