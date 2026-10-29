import { useEffect, useState, useSyncExternalStore } from 'react'
import { useNavigate } from 'react-router-dom'
import { classify, getModelStatus, loadModel, subscribeModelStatus } from '../../ml/classifier'

/** Passed to /identify/result in router state */
export type IdentifyResultState = {
  imageUrl: string
  results: { id: string; probability: number }[]
}

// Only the latest photo is kept; older object URLs are released
let lastImageUrl: string | null = null

export function useModelStatus() {
  return useSyncExternalStore(subscribeModelStatus, getModelStatus)
}

// Shared by the Identify and Result pages (Result has its own "Take Another Photo")
export function useIdentify() {
  const navigate = useNavigate()
  const status = useModelStatus()
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  // Start downloading the model as soon as an Identify screen opens
  useEffect(loadModel, [])

  async function identify(file: File) {
    setBusy(true)
    setError('')
    try {
      const predictions = await classify(file)
      if (lastImageUrl) URL.revokeObjectURL(lastImageUrl)
      lastImageUrl = URL.createObjectURL(file)
      const state: IdentifyResultState = {
        imageUrl: lastImageUrl,
        results: predictions.slice(0, 3).map(({ species, probability }) => ({ id: species.id, probability })),
      }
      navigate('/identify/result', { state })
    } catch {
      setError("Couldn't read this photo. Please try another image.")
    } finally {
      setBusy(false)
    }
  }

  return { status, busy, error, identify }
}
