import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ArrowBackIcon } from '../../components/icons'
import { useAuth } from '../../context/AuthContext'
import { CATEGORY_INFO, fetchQuestions, isCategory, saveResult, type Category, type Question } from '../../lib/quiz'
import styles from './Quiz.module.css'

type Phase =
  | { name: 'loading' }
  | { name: 'error' }
  | { name: 'answering' }
  | { name: 'saving' }
  | { name: 'done'; saveFailed: boolean }

// Replaces AnimalsQuizQuestions / PlantsQuizQuestions (one component for both categories).
// Answers are stored per question and scored once on submit, so going back and forth
// can't count a question twice (bug 2 in the Android version).
export default function Quiz() {
  const { category } = useParams()
  return isCategory(category) ? <QuizRun key={category} category={category} /> : <p className={styles.message}>Unknown quiz.</p>
}

function QuizRun({ category }: { category: Category }) {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [questions, setQuestions] = useState<Question[]>([])
  const [answers, setAnswers] = useState<(string | null)[]>([])
  const [index, setIndex] = useState(0)
  const [phase, setPhase] = useState<Phase>({ name: 'loading' })

  useEffect(() => {
    let cancelled = false
    fetchQuestions(category)
      .then((loaded) => {
        if (cancelled) return
        setQuestions(loaded)
        setAnswers(loaded.map(() => null))
        setPhase(loaded.length ? { name: 'answering' } : { name: 'error' })
      })
      .catch(() => !cancelled && setPhase({ name: 'error' }))
    return () => {
      cancelled = true
    }
  }, [category])

  const score = questions.filter((q, i) => answers[i] === q.answer).length
  const answeredCount = answers.filter((a) => a !== null).length

  function leave() {
    const inProgress = phase.name === 'answering' && answeredCount > 0
    if (!inProgress || window.confirm('Leave the quiz? Your answers will be lost.')) navigate(`/challenge/${category}`)
  }

  async function submit() {
    if (answeredCount < questions.length && !window.confirm('Some questions are unanswered. Submit anyway?')) return
    setPhase({ name: 'saving' })
    try {
      await saveResult(user!.uid, category, score, questions.length)
      setPhase({ name: 'done', saveFailed: false })
    } catch {
      setPhase({ name: 'done', saveFailed: true })
    }
  }

  function restart() {
    setAnswers(questions.map(() => null))
    setIndex(0)
    setPhase({ name: 'answering' })
  }

  const header = (
    <header className={styles.header}>
      <button type="button" className={styles.close} onClick={leave} aria-label="Leave quiz">
        <ArrowBackIcon />
      </button>
      <div>
        <h1>{CATEGORY_INFO[category].title} Quiz</h1>
        <p>Answer and get points</p>
      </div>
    </header>
  )

  if (phase.name === 'loading') {
    return (
      <div className={styles.screen}>
        {header}
        <p className={styles.message}>Loading questions…</p>
      </div>
    )
  }

  if (phase.name === 'error') {
    return (
      <div className={styles.screen}>
        {header}
        <p className={styles.message}>Couldn&apos;t load the questions. Check your connection and try again.</p>
      </div>
    )
  }

  if (phase.name === 'done') {
    return (
      <div className={styles.screen}>
        {header}
        <section className={styles.summary}>
          <p className={styles.scoreLabel}>Your score</p>
          <p className={styles.score}>
            {score}/{questions.length}
          </p>
          <p className={styles.message}>
            {phase.saveFailed
              ? "Your score couldn't be saved. Check your connection."
              : score === questions.length
                ? 'Perfect! Score saved.'
                : 'Score saved. Keep exploring and try again!'}
          </p>
          {phase.saveFailed && (
            <button type="button" className={styles.secondary} onClick={submit}>
              Try saving again
            </button>
          )}
        </section>

        <ol className={styles.review}>
          {questions.map((q, i) => {
            const correct = answers[i] === q.answer
            return (
              <li key={q.id} className={correct ? styles.right : styles.wrong}>
                <p>{q.questionText}</p>
                <small>
                  {correct ? '✓ ' : '✗ '}
                  Your answer: {answers[i] ?? 'not answered'}
                  {!correct && <> · Correct: {q.answer}</>}
                </small>
              </li>
            )
          })}
        </ol>

        <div className={styles.actions}>
          <button type="button" className={styles.secondary} onClick={restart}>
            Play again
          </button>
          <Link to="/challenge" className={styles.primary}>
            Back to Challenge
          </Link>
        </div>
      </div>
    )
  }

  const question = questions[index]
  const isLast = index === questions.length - 1

  return (
    <div className={styles.screen}>
      {header}

      <div className={styles.progress}>
        <span>
          Question {index + 1}/{questions.length}
        </span>
        <div className={styles.track}>
          <div style={{ width: `${((index + 1) / questions.length) * 100}%` }} />
        </div>
      </div>

      <section className={styles.question}>
        <h2 id="question">{question.questionText}</h2>
        <div role="radiogroup" aria-labelledby="question" className={styles.choices}>
          {question.choices.map((choice) => (
            <button
              key={choice}
              type="button"
              role="radio"
              aria-checked={answers[index] === choice}
              className={styles.choice}
              onClick={() => setAnswers(answers.map((a, i) => (i === index ? choice : a)))}
            >
              {choice}
            </button>
          ))}
        </div>
      </section>

      <div className={styles.actions}>
        {index > 0 && (
          <button type="button" className={styles.secondary} onClick={() => setIndex(index - 1)}>
            Previous
          </button>
        )}
        {isLast ? (
          <button type="button" className={styles.primary} onClick={submit} disabled={phase.name === 'saving'}>
            {phase.name === 'saving' ? 'Saving…' : 'Submit'}
          </button>
        ) : (
          <button type="button" className={styles.primary} onClick={() => setIndex(index + 1)}>
            Next
          </button>
        )}
      </div>
    </div>
  )
}
