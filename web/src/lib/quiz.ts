// quizzes/{category}/{qN} → questionText, choices[4], answer
// users/{uid}/results/quiz/{category} → score, total, date, time (same as AnimalsQuizQuestions.saveScoreToUser)
import { get, onValue, ref, set } from 'firebase/database'
import { db } from '../firebase'

export const CATEGORIES = ['animals', 'plants'] as const
export type Category = (typeof CATEGORIES)[number]

export const CATEGORY_INFO: Record<Category, { title: string; quizText: string; gameNoun: string }> = {
  animals: { title: 'Animals', quizText: 'Guess the animals', gameNoun: 'animals' },
  plants: { title: 'Plants', quizText: 'Guess the plants', gameNoun: 'plants' },
}

export const isCategory = (value: string | undefined): value is Category =>
  CATEGORIES.includes(value as Category)

export type Question = { id: string; questionText: string; choices: string[]; answer: string }

export type QuizResult = { score: number; total: number; date: string; time: string }

export async function fetchQuestions(category: Category): Promise<Question[]> {
  const snapshot = await get(ref(db, `quizzes/${category}`))
  const questions: Question[] = []
  snapshot.forEach((child) => {
    const { questionText, choices, answer } = child.val()
    questions.push({ id: child.key!, questionText, choices: Object.values(choices ?? {}), answer })
  })
  return questions
}

/** Live results for every category the user has played */
export function subscribeResults(uid: string, onChange: (results: Partial<Record<Category, QuizResult>>) => void) {
  return onValue(ref(db, `users/${uid}/results/quiz`), (snapshot) => onChange(snapshot.val() ?? {}))
}

export function saveResult(uid: string, category: Category, score: number, total: number) {
  const now = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  const result: QuizResult = {
    score,
    total,
    date: `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`,
    time: `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`,
  }
  return set(ref(db, `users/${uid}/results/quiz/${category}`), result)
}
