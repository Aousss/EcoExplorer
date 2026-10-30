import { useState } from 'react'
import Dialog from '../../components/Dialog'
import PageHeader from '../../components/PageHeader'
import QrCode from '../../components/QrCode'
import { ArIcon } from '../../components/icons'
import { useIsDesktop } from '../../hooks/useIsDesktop'
import { AR_EXPERIENCES, MARKER_SHEET_URL, type ArExperience } from '../../lib/ar'
import styles from './Explore.module.css'

// Replaces ExploreFragment, WebARFragmentSimulation and WebARFragmentLearn.
// The 8th Wall pages open in their own tab, where the browser asks for the camera itself.
export default function Explore() {
  const isDesktop = useIsDesktop()
  const [showGuide, setShowGuide] = useState(false)
  const [qrFor, setQrFor] = useState<ArExperience | null>(null)

  return (
    <>
      <PageHeader title="AR Explore" />

      <section className={styles.guide}>
        <h2>Guideline</h2>
        <p>
          AR starts when your camera sees one of the marker images. Download the sheet, then print it or open it on
          another screen.
        </p>
        {isDesktop && <p>On a computer? Open the sheet here and scan the QR code with your phone.</p>}
        <div className={styles.guideActions}>
          <a href={MARKER_SHEET_URL} download className={styles.download}>
            Download markers
          </a>
          <button type="button" className={styles.howTo} onClick={() => setShowGuide(true)}>
            How to use
          </button>
        </div>
      </section>

      <section className={styles.section}>
        {AR_EXPERIENCES.map((experience) => (
          <article key={experience.id} className={styles.card}>
            <h2 className={styles.cardTitle}>{experience.title}</h2>
            <img src={experience.image} alt="" className={styles.cardImage} />
            <div className={styles.cardBody}>
              <p>{experience.text}</p>
              <small>Marker: {experience.markers.join(' or ')}</small>
              {isDesktop ? (
                <button type="button" className={styles.viewAr} onClick={() => setQrFor(experience)}>
                  <ArIcon /> View in AR
                </button>
              ) : (
                <a href={experience.url} target="_blank" rel="noopener noreferrer" className={styles.viewAr}>
                  <ArIcon /> View in AR
                </a>
              )}
            </div>
          </article>
        ))}
      </section>

      {showGuide && (
        <Dialog title="AR Guide" onClose={() => setShowGuide(false)}>
          <ol className={styles.steps}>
            <li>
              <a href={MARKER_SHEET_URL} download>Download the marker sheet</a>. Print it, or open it on another
              screen.
            </li>
            <li>Tap <strong>View in AR</strong> and allow camera access when your browser asks.</li>
            <li>Point the camera at the matching marker and hold it steady until the scene appears.</li>
          </ol>
          <p className={styles.tip}>Works best in good light, with the whole marker in view.</p>
        </Dialog>
      )}

      {qrFor && (
        <Dialog title={qrFor.title} onClose={() => setQrFor(null)}>
          <p className={styles.tip}>AR needs a phone camera. Scan to open it on your phone:</p>
          <QrCode value={qrFor.url} label={`QR code for ${qrFor.url}`} className={styles.qr} />
          <p className={styles.tip}>
            Then point your phone at the {qrFor.markers.join(' or ')} marker. You can{' '}
            <a href={MARKER_SHEET_URL} target="_blank" rel="noopener noreferrer">open the marker sheet</a> on this
            screen.
          </p>
          <a href={qrFor.url} target="_blank" rel="noopener noreferrer" className={styles.openAnyway}>
            Open on this computer anyway
          </a>
        </Dialog>
      )}
    </>
  )
}
