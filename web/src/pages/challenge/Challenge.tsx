import { Link } from 'react-router-dom'
import PageHeader from '../../components/PageHeader'
import { BugsIcon, PlantsIcon } from '../../components/icons'
import { useAuth } from '../../context/AuthContext'
import { CATEGORIES, CATEGORY_INFO } from '../../lib/quiz'
import { useQuizResults } from './useQuizResults'
import styles from './Challenge.module.css'

const CATEGORY_ICON = { plants: PlantsIcon, animals: BugsIcon }

// Replaces ChallengeFragment
export default function Challenge() {
  const { user, loading } = useAuth()
  const results = useQuizResults()
  const played = results ? CATEGORIES.filter((c) => results[c]) : []

  return (
    <>
      <PageHeader title="Challenge" />

      <section className={styles.intro}>
        <h2>Let&apos;s Play</h2>
        <p>Play some interesting games to test your knowledge.</p>
      </section>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>Categories</h2>
        <div className={styles.categories}>
          {(['plants', 'animals'] as const).map((category) => {
            const Icon = CATEGORY_ICON[category]
            return (
              <Link key={category} to={`/challenge/${category}`} className={styles.categoryCard}>
                <Icon className={styles.categoryIcon} />
                <span>{CATEGORY_INFO[category].title}</span>
              </Link>
            )
          })}
        </div>
      </section>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>Recent Results</h2>
        {!loading && !user && (
          <p className={styles.empty}>
            <Link to="/login" state={{ from: '/challenge' }}>Log in</Link> to play and save your results.
          </p>
        )}
        {user && results && played.length === 0 && (
          <p className={styles.empty}>No results yet. Be the first to take a challenge!</p>
        )}
        {played.length > 0 && (
          <ul className={styles.results}>
            {played.map((category) => {
              const result = results![category]!
              return (
                <li key={category}>
                  <Link to={`/challenge/${category}`}>
                    <span>
                      <strong>{CATEGORY_INFO[category].title} Quiz</strong>
                      <small>{result.date}</small>
                    </span>
                    <span className={styles.score}>
                      {result.score}/{result.total}
                    </span>
                  </Link>
                </li>
              )
            })}
          </ul>
        )}
      </section>
    </>
  )
}
