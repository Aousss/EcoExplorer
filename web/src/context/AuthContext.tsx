import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { onAuthStateChanged, type User } from 'firebase/auth'
import { onValue, ref } from 'firebase/database'
import { auth, db } from '../firebase'
import type { UserProfile } from '../lib/account'

type AuthState = {
  /** Firebase Auth user, null for guests */
  user: User | null
  /** users/{uid} from the Realtime Database, null for guests or while loading */
  profile: UserProfile | null
  /** True until Firebase has restored the session on page load */
  loading: boolean
}

const AuthContext = createContext<AuthState>({ user: null, profile: null, loading: true })

// Replaces UserViewModel + the SharedPreferences username cache (UserUtils)
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(
    () =>
      onAuthStateChanged(auth, (nextUser) => {
        setUser(nextUser)
        if (!nextUser) {
          setProfile(null)
          setLoading(false)
        }
      }),
    [],
  )

  // Live profile updates, e.g. after a new profile picture is uploaded
  useEffect(() => {
    if (!user) return
    return onValue(
      ref(db, `users/${user.uid}`),
      (snapshot) => {
        setProfile(snapshot.val())
        setLoading(false)
      },
      () => setLoading(false),
    )
  }, [user])

  return <AuthContext.Provider value={{ user, profile, loading }}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
