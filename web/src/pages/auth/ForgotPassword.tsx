import { useState, type FormEvent } from 'react'
import { Link } from 'react-router-dom'
import { ArrowBackIcon } from '../../components/icons'
import { errorMessage, resetPassword } from '../../lib/account'
import styles from './Auth.module.css'

// Replaces ForgotPasswordActivity (two states: enter email, then "check your inbox")
export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [sentTo, setSentTo] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError('')
    setBusy(true)
    try {
      await resetPassword(email.trim())
      setSentTo(email.trim())
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className={styles.screen}>
      <div className={styles.hero}>
        <Link to="/login" className={styles.topLink}>
          <ArrowBackIcon /> Back to login
        </Link>
        <h1 className={styles.title}>{sentTo ? 'Check Your Inbox' : 'Forgot Password'}</h1>
      </div>

      <div className={styles.panel}>
        {sentTo ? (
          <div className={styles.notice}>
            <p>
              A password reset link has been sent to <strong>{sentTo}</strong>.
              <br />
              Please check your spam folder if you don&apos;t see it in your inbox.
            </p>
            <Link to="/login" className={styles.secondary}>
              Back to login
            </Link>
          </div>
        ) : (
          <form className={styles.form} onSubmit={handleSubmit} noValidate>
            <p className={styles.lead}>Enter your email to receive a password reset link.</p>
            <label className={styles.field}>
              Email
              <input className={styles.input} type="email" autoComplete="email"
                value={email} onChange={(e) => setEmail(e.target.value)} />
            </label>

            {error && <p className={styles.error} role="alert">{error}</p>}

            <button type="submit" className={styles.primary} disabled={busy || !email.trim()}>
              {busy ? 'Sending…' : 'Send Reset Link'}
            </button>
          </form>
        )}
      </div>
    </div>
  )
}
