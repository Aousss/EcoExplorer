import { useState } from 'react'
import { PersonIcon } from './icons'

type Props = { src?: string; className?: string }

// Profile picture with the person icon as fallback (no picture, or the image fails to load)
export default function Avatar({ src, className }: Props) {
  const [failedSrc, setFailedSrc] = useState<string>()
  const showImage = src && src !== failedSrc

  return (
    <div className={className}>
      {showImage ? <img src={src} alt="" onError={() => setFailedSrc(src)} /> : <PersonIcon />}
    </div>
  )
}
