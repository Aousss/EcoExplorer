import { Link, useParams } from 'react-router-dom'
import PageHeader from '../../components/PageHeader'
import { ImageMatchIcon, MemoryCardIcon, PuzzleIcon, QuizIcon } from '../../components/icons'
import { CATEGORY_INFO, isCategory } from '../../lib/quiz'
import { useQuizResults } from './useQuizResults'
import styles from './Challenge.module.css'

// Replaces AnimalFragment / PlantFragment and the quiz intro (AnimalsChallengeQuiz / PlantsChallengeQuiz).
// The other three games are listed on Android too but were never built.
export default function ChallengeCategory() {
  const { category } = useParams()
  const results = useQuizResults()

  if (!isCategory(category)) {
    return (
      <>
        <PageHeader title="Challenge" back />
        <p className={styles.empty}>This category doesn&apos;t exist.</p>
      </>
    )
  }

  const info = CATEGORY_INFO[category]
  const last = results?.[category]
  const comingSoon = [
    { title: 'Image Match', text: `Spot the ${info.gameNoun}`, Icon: ImageMatchIcon },
    { title: 'Puzzle Challenge', text: 'Jigsaw puzzle', Icon: PuzzleIcon },
    { title: 'Memory Card', text: `Find matching pairs of ${info.gameNoun}`, Icon: MemoryCardIcon },
  ]

  return (
    <>
      <PageHeader title={`Challenge | ${info.title}`} back />

      <section className={styles.section}>
        <div className={styles.quizCard}>
          <QuizIcon className={styles.gameIcon} />
          <div className={styles.gameText}>
            <strong>Quiz Challenge</strong>
            <span>{info.quizText}</span>
            <small>
              Recent score: {results === null ? '…' : last ? `${last.score}/${last.total}` : 'not played yet'}
            </small>
          </div>
          <Link to={`/challenge/${category}/quiz`} className={styles.start}>
            Start
          </Link>
        </div>

        <h2 className={styles.sectionTitle}>More games</h2>
        <ul className={styles.games}>
          {comingSoon.map(({ title, text, Icon }) => (
            <li key={title} className={styles.gameCard} aria-disabled="true">
              <Icon className={styles.gameIcon} />
              <div className={styles.gameText}>
                <strong>{title}</strong>
                <span>{text}</span>
              </div>
              <span className={styles.soon}>Coming soon</span>
            </li>
          ))}
        </ul>
      </section>
    </>
  )
}
