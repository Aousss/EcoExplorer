import { Link } from 'react-router-dom'
import PageHeader from '../../components/PageHeader'
import { loadModel, type ModelStatus } from '../../ml/classifier'
import { ANIMALS, PLANTS, SPECIES, type Species } from '../../lib/species'
import PhotoPicker from './PhotoPicker'
import { useIdentify } from './useIdentify'
import styles from './Identify.module.css'

function ModelStatusBar({ status }: { status: ModelStatus }) {
  if (status.state === 'ready') return <p className={styles.modelReady}>AI model ready</p>
  if (status.state === 'error') {
    return (
      <p className={styles.modelError} role="alert">
        Couldn&apos;t load the AI model. Check your connection.{' '}
        <button type="button" onClick={loadModel}>
          Retry
        </button>
      </p>
    )
  }
  const percent = status.state === 'loading' && status.progress !== null ? Math.round(status.progress * 100) : null
  return (
    <div className={styles.modelLoading} role="status">
      <span>Preparing the AI model{percent !== null ? `… ${percent}%` : '…'}</span>
      <div className={styles.progressTrack}>
        <div className={styles.progressFill} style={{ width: `${percent ?? 15}%` }} />
      </div>
      <small>First time only (about 12 MB)</small>
    </div>
  )
}

function SpeciesRow({ title, species }: { title: string; species: Species[] }) {
  return (
    <section className={styles.section}>
      <h2 className={styles.sectionTitle}>{title}</h2>
      <div className={styles.carousel}>
        {species.map((s) => (
          <Link key={s.id} to={`/species/${s.id}`} className={styles.speciesCard}>
            <img src={s.image} alt="" loading="lazy" />
            <span>{s.name}</span>
          </Link>
        ))}
      </div>
    </section>
  )
}

// Replaces IdentifyFragment
export default function Identify() {
  const { status, busy, error, identify } = useIdentify()

  return (
    <>
      <PageHeader title="Identify" subtitle="Take or choose a photo of a plant or animal" />

      <section className={styles.panel}>
        <PhotoPicker onPhoto={identify} disabled={busy || status.state === 'error'} />
        {busy ? <p className={styles.busy} role="status">Identifying…</p> : <ModelStatusBar status={status} />}
        {error && <p className={styles.modelError} role="alert">{error}</p>}
      </section>

      <p className={styles.note}>
        EcoExplorer recognises these {SPECIES.length} species. Photos of anything else may get a wrong answer.
      </p>

      <SpeciesRow title="Plants" species={PLANTS} />
      <SpeciesRow title="Animals" species={ANIMALS} />
    </>
  )
}
