import { useEffect, useRef, type ReactNode } from 'react'
import styles from './Dialog.module.css'

type Props = {
  title: string
  onClose: () => void
  children: ReactNode
}

// Modal shown with the native <dialog>, so Esc, focus trapping and the backdrop come for free.
// Render it conditionally: it opens when mounted.
export default function Dialog({ title, onClose, children }: Props) {
  const ref = useRef<HTMLDialogElement>(null)

  useEffect(() => {
    const dialog = ref.current
    if (dialog && !dialog.open) dialog.showModal()
  }, [])

  return (
    <dialog
      ref={ref}
      className={styles.dialog}
      aria-labelledby="dialog-title"
      onClose={onClose}
      // A click on the backdrop lands on the <dialog> itself, not on its content
      onClick={(e) => e.target === e.currentTarget && ref.current?.close()}
    >
      <div className={styles.content}>
        <h2 id="dialog-title" className={styles.title}>{title}</h2>
        {children}
        <button type="button" className={styles.close} onClick={() => ref.current?.close()}>
          Close
        </button>
      </div>
    </dialog>
  )
}
