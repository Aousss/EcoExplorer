import { useMemo } from 'react'
import { encode } from 'uqr'

type Props = {
  value: string
  label: string
  className?: string
}

// Always black on white (even in dark mode) so every phone camera can read it
export default function QrCode({ value, label, className }: Props) {
  const { size, path } = useMemo(() => {
    const { data, size } = encode(value, { ecc: 'M', border: 2 })
    let path = ''
    data.forEach((row, y) => row.forEach((dark, x) => dark && (path += `M${x} ${y}h1v1h-1z`)))
    return { size, path }
  }, [value])

  return (
    <svg viewBox={`0 0 ${size} ${size}`} role="img" aria-label={label} className={className} shapeRendering="crispEdges">
      <rect width={size} height={size} fill="#fff" />
      <path d={path} fill="#000" />
    </svg>
  )
}
