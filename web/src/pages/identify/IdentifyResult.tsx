import { Link, useLocation } from 'react-router-dom'
import PageHeader from '../../components/PageHeader'
import { CONFIDENCE_THRESHOLD } from '../../ml/classifier'
import { getSpecies } from '../../lib/species'
import PhotoPicker from './PhotoPicker'
import SpeciesInfo from './SpeciesInfo'
import { useIdentify, type IdentifyResultState } from './useIdentify'
import styles from './Identify.module.css'

const percent = (p: number) => `${Math.round(p * 100)}%`

// Replaces IdentifyResults
export default function IdentifyResult() {
  const state = useLocation().state as IdentifyResultState | null
  const { busy, error, identify } = useIdentify()

  const again = (
    <section className={styles.panel}>
      <h2 className={styles.panelTitle}>Take Another Photo</h2>
      <PhotoPicker onPhoto={identify} disabled={busy} />
      {busy && <p className={styles.busy} role="status">Identifying…</p>}
      {error && <p className={styles.modelError} role="alert">{error}</p>}
    </section>
  )

  // Opened directly (e.g. after a page refresh): the photo isn't kept, so start again
  if (!state?.results.length) {
    return (
      <>
        <PageHeader title="Result" back />
        <p className={styles.note}>No photo to show. Take or choose a photo to identify a species.</p>
        {again}
      </>
    )
  }

  const [best, ...others] = state.results
  const species = getSpecies(best.id)!
  const identified = best.probability >= CONFIDENCE_THRESHOLD
  const alternatives = others.filter((r) => r.probability >= 0.05)

  return (
    <>
      <PageHeader title="Result" back />

      <img src={state.imageUrl} alt="Your photo" className={styles.photo} />

      <section className={styles.panel}>
        {identified ? (
          <>
            <span className={styles.detected}>Species detected!</span>
            <h2 className={styles.resultName}>{species.name}</h2>
            <div className={styles.confidence}>
              <div className={styles.progressTrack}>
                <div className={styles.progressFill} style={{ width: percent(best.probability) }} />
              </div>
              <span>{percent(best.probability)} match</span>
            </div>
            <SpeciesInfo species={species} />
            <Link to={`/species/${species.id}`} className={styles.moreLink}>
              Read more about {species.name} ›
            </Link>
          </>
        ) : (
          <>
            <span className={styles.notDetected}>Sorry!</span>
            <h2 className={styles.resultName}>This species can&apos;t be identified.</h2>
            <p className={styles.muted}>
              Closest match: {species.name} ({percent(best.probability)}). Try a clearer photo of one of the{' '}
              <Link to="/identify">16 species EcoExplorer knows</Link>.
            </p>
          </>
        )}

        {alternatives.length > 0 && (
          <p className={styles.muted}>
            Other possibilities:{' '}
            {alternatives.map((r, i) => (
              <span key={r.id}>
                {i > 0 && ', '}
                <Link to={`/species/${r.id}`}>{getSpecies(r.id)!.name}</Link> ({percent(r.probability)})
              </span>
            ))}
          </p>
        )}
      </section>

      {again}
    </>
  )
}
