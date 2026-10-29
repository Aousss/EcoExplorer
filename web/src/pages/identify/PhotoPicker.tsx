import { useRef, type ChangeEvent } from 'react'
import { CameraIcon } from '../../components/icons'
import styles from './Identify.module.css'

type Props = { onPhoto: (file: File) => void; disabled?: boolean }

// Replaces the camera / gallery Intents. On phones "Take Photo" opens the camera;
// on desktop both buttons open the file picker.
export default function PhotoPicker({ onPhoto, disabled }: Props) {
  const cameraInput = useRef<HTMLInputElement>(null)
  const galleryInput = useRef<HTMLInputElement>(null)

  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]
    event.target.value = '' // allow picking the same photo again
    if (file) onPhoto(file)
  }

  return (
    <div className={styles.picker}>
      <button type="button" className={styles.pickPrimary} disabled={disabled} onClick={() => cameraInput.current?.click()}>
        <CameraIcon /> Take Photo
      </button>
      <button type="button" className={styles.pickSecondary} disabled={disabled} onClick={() => galleryInput.current?.click()}>
        Select From Gallery
      </button>
      <input ref={cameraInput} type="file" accept="image/*" capture="environment" hidden onChange={handleChange} />
      <input ref={galleryInput} type="file" accept="image/*" hidden onChange={handleChange} />
    </div>
  )
}
