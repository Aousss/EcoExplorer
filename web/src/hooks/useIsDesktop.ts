import { useSyncExternalStore } from 'react'

// A mouse-driven device: AR needs a phone camera, so these get a QR code instead
const query = window.matchMedia('(hover: hover) and (pointer: fine)')

function subscribe(onChange: () => void) {
  query.addEventListener('change', onChange)
  return () => query.removeEventListener('change', onChange)
}

export function useIsDesktop() {
  return useSyncExternalStore(subscribe, () => query.matches)
}
