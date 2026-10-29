import { useEffect, useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import { subscribeResults, type Category, type QuizResult } from '../../lib/quiz'

/** The signed-in user's latest quiz result per category; null while loading or for guests */
export function useQuizResults() {
  const { user } = useAuth()
  const [results, setResults] = useState<Partial<Record<Category, QuizResult>> | null>(null)

  useEffect(() => {
    if (!user) {
      setResults(null)
      return
    }
    return subscribeResults(user.uid, setResults)
  }, [user])

  return results
}
