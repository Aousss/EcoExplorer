import { useParams } from 'react-router-dom'
import PageHeader from '../../components/PageHeader'
import { getSpecies } from '../../lib/species'
import SpeciesInfo from './SpeciesInfo'
import styles from './Identify.module.css'

// Replaces DetailsFragment. Loads by id, so links to a species can be shared.
export default function SpeciesDetail() {
  const { id = '' } = useParams()
  const species = getSpecies(id)

  if (!species) {
    return (
      <>
        <PageHeader title="Details" back />
        <p className={styles.note}>This species isn&apos;t in EcoExplorer.</p>
      </>
    )
  }

  return (
    <>
      <PageHeader title="Details" back />
      <img src={species.image} alt={species.name} className={styles.photo} />
      <section className={styles.panel}>
        <span className={styles.category}>{species.category === 'plant' ? 'Plant' : 'Animal'}</span>
        <h2 className={styles.resultName}>{species.name}</h2>
        <p className={styles.description}>{species.description}</p>
        <SpeciesInfo species={species} />
      </section>
    </>
  )
}
