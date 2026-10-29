import PageHeader from '../components/PageHeader'
import styles from './Placeholder.module.css'

type Props = {
  title: string
  /** Migration phase that builds this screen (docs/WEB_MIGRATION_PLAN.md §10) */
  phase: number
  /** Android class this screen replaces */
  replaces: string
  features: string[]
  back?: boolean
}

// Temporary screen used until each phase replaces it with the real page
export default function Placeholder({ title, phase, replaces, features, back }: Props) {
  return (
    <>
      <PageHeader title={title} back={back} />
      <section className={styles.card}>
        <span className={styles.badge}>Coming in Phase {phase}</span>
        <p className={styles.replaces}>
          Replaces <code>{replaces}</code>
        </p>
        <ul className={styles.list}>
          {features.map((feature) => (
            <li key={feature}>{feature}</li>
          ))}
        </ul>
      </section>
    </>
  )
}
