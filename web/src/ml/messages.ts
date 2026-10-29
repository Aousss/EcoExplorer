// Messages between the page (classifier.ts) and classifier.worker.ts
export type WorkerRequest = { type: 'load' } | { type: 'classify'; id: number; image: Blob }

export type WorkerResponse =
  | { type: 'progress'; loaded: number; total: number }
  | { type: 'ready' }
  | { type: 'result'; id: number; probabilities: number[] }
  | { type: 'error'; id?: number; message: string }
