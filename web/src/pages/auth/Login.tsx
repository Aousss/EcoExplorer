import { useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { ArrowBackIcon } from '../../components/icons'
import { errorMessage, login } from '../../lib/account'
import styles from './Auth.module.css'

// Replaces LoginActivity
export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  // Pages that require login send the user here with { from } so we can return them afterwards
  const from = (location.state as { from?: string } | null)?.from ?? '/'

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    setBusy(true)
    try {
      await login(email.trim(), password)
      navigate(from, { replace: true })
    } catch (err) {
      setError(errorMessage(err))
      setBusy(false)
    }
  }

  return (
    <div className={styles.screen}>
      <div className={styles.hero}>
        <Link to="/" className={styles.topLink}>
          <ArrowBackIcon /> Home
        </Link>
        <img src="/images/logo.svg" alt="EcoExplorer" className={styles.logo} />
        <h1 className={styles.title}>Login</h1>
      </div>

      <div className={styles.panel}>
        <form className={styles.form} onSubmit={handleSubmit} noValidate>
          <label className={styles.field}>
            Email
            <input className={styles.input} type="email" autoComplete="email" required
              value={email} onChange={(e) => setEmail(e.target.value)} />
          </label>
          <label className={styles.field}>
            Password
            <input className={styles.input} type="password" autoComplete="current-password" required
              value={password} onChange={(e) => setPassword(e.target.value)} />
          </label>
          <Link to="/forgot-password" className={styles.forgot}>
            Forgot Password?
          </Link>

          {error && <p className={styles.error} role="alert">{error}</p>}

          <button type="submit" className={styles.primary} disabled={busy || !email.trim() || !password}>
            {busy ? 'Logging in…' : 'Login'}
          </button>
        </form>

        <div className={styles.divider}>or</div>
        <Link to="/" className={styles.secondary}>
          Continue as Guest
        </Link>

        <p className={styles.footer}>
          Don&apos;t have an account? <Link to="/register" state={{ from }}>Sign Up</Link>
        </p>
      </div>
    </div>
  )
}
