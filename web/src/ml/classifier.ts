// Page-side API for the species classifier. One shared worker; the model loads once per session
// (and is kept in the browser's Cache Storage after the first download).
import { speciesForClass, type Species } from '../lib/species'
import type { WorkerRequest, WorkerResponse } from './messages'

/** Below this probability the result screen says the species couldn't be identified (as on Android) */
export const CONFIDENCE_THRESHOLD = 0.5

export type Prediction = { species: Species; probability: number }

export type ModelStatus =
  | { state: 'idle' }
  | { state: 'loading'; progress: number | null }
  | { state: 'ready' }
  | { state: 'error'; message: string }

let worker: Worker | null = null
let status: ModelStatus = { state: 'idle' }
const statusListeners = new Set<(status: ModelStatus) => void>()
const pending = new Map<number, { resolve: (p: number[]) => void; reject: (e: Error) => void }>()
let nextId = 1

function setStatus(next: ModelStatus) {
  status = next
  statusListeners.forEach((listener) => listener(status))
}

function getWorker() {
  if (worker) return worker
  worker = new Worker(new URL('./classifier.worker.ts', import.meta.url), { type: 'module' })
  worker.onmessage = ({ data: message }: MessageEvent<WorkerResponse>) => {
    switch (message.type) {
      case 'progress':
        setStatus({ state: 'loading', progress: message.total ? message.loaded / message.total : null })
        break
      case 'ready':
        setStatus({ state: 'ready' })
        break
      case 'result':
        pending.get(message.id)?.resolve(message.probabilities)
        pending.delete(message.id)
        break
      case 'error':
        if (message.id === undefined) {
          setStatus({ state: 'error', message: message.message })
        } else {
          pending.get(message.id)?.reject(new Error(message.message))
          pending.delete(message.id)
        }
        break
    }
  }
  return worker
}

const send = (request: WorkerRequest) => getWorker().postMessage(request)

/** Starts downloading the model if it isn't loaded or loading yet */
export function loadModel() {
  if (status.state === 'loading' || status.state === 'ready') return
  setStatus({ state: 'loading', progress: null })
  send({ type: 'load' })
}

export function getModelStatus() {
  return status
}

export function subscribeModelStatus(listener: (status: ModelStatus) => void) {
  statusListeners.add(listener)
  return () => {
    statusListeners.delete(listener)
  }
}

/** All 16 species, most likely first */
export async function classify(image: Blob): Promise<Prediction[]> {
  loadModel()
  const id = nextId++
  const probabilities = await new Promise<number[]>((resolve, reject) => {
    pending.set(id, { resolve, reject })
    send({ type: 'classify', id, image })
  })
  return probabilities
    .map((probability, index) => ({ species: speciesForClass(index), probability }))
    .sort((a, b) => b.probability - a.probability)
}
