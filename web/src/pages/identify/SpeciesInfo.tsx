import type { Species } from '../../lib/species'
import styles from './Identify.module.css'

// The four fields shown on both the result and the details screens (same labels as identify_results.xml)
export default function SpeciesInfo({ species }: { species: Species }) {
  return (
    <dl className={styles.info}>
      <dt>Scientific Name</dt>
      <dd className={styles.scientific}>{species.scientificName}</dd>
      <dt>Ecosystem</dt>
      <dd>{species.ecosystem}</dd>
      <dt>Habitat</dt>
      <dd>{species.habitat}</dd>
      <dt>Role in Ecosystems</dt>
      <dd>{species.role}</dd>
    </dl>
  )
}
