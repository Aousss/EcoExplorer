import { useState, type ChangeEvent, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { ArrowBackIcon } from '../../components/icons'
import { errorMessage, register } from '../../lib/account'
import styles from './Auth.module.css'

// Database keys can't contain . # $ [ ] /, and usernames are shown as @username
const USERNAME_PATTERN = /^[a-zA-Z0-9_]{3,20}$/

const EMPTY_FORM = { username: '', fullname: '', age: '', email: '', password: '', confirmPassword: '' }

function validate(form: typeof EMPTY_FORM): string {
  if (Object.values(form).some((value) => !value.trim())) return 'Please fill in all the fields.'
  if (!USERNAME_PATTERN.test(form.username)) return 'Username must be 3–20 letters, numbers or underscores.'
  const age = Number(form.age)
  if (!Number.isInteger(age) || age < 1 || age > 120) return 'Please enter a valid age.'
  if (form.password.length < 6) return 'Password must be at least 6 characters.'
  if (form.password !== form.confirmPassword) return 'The passwords do not match.'
  return ''
}

// Replaces RegisterActivity
export default function Register() {
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: string } | null)?.from ?? '/'

  const [form, setForm] = useState(EMPTY_FORM)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (key: keyof typeof EMPTY_FORM) => (e: ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [key]: e.target.value })

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    const problem = validate(form)
    if (problem) {
      setError(problem)
      return
    }
    setError('')
    setBusy(true)
    try {
      await register({
        username: form.username.trim(),
        fullname: form.fullname.trim(),
        age: Number(form.age),
        email: form.email.trim(),
        password: form.password,
      })
      // Firebase signs the new user in, so go straight back instead of to the login screen
      navigate(from, { replace: true })
    } catch (err) {
      setError(errorMessage(err))
      setBusy(false)
    }
  }

  return (
    <div className={styles.screen}>
      <div className={styles.hero}>
        <Link to="/login" state={{ from }} className={styles.topLink}>
          <ArrowBackIcon /> Back to login
        </Link>
        <h1 className={styles.title}>Sign Up</h1>
        <p className={styles.lead}>Create an account to save your quiz scores.</p>
      </div>

      <div className={styles.panel}>
        <form className={styles.form} onSubmit={handleSubmit} noValidate>
          <div className={styles.row}>
            <label className={styles.field}>
              Username
              <input className={styles.input} autoComplete="username" value={form.username} onChange={update('username')} />
            </label>
            <label className={styles.field}>
              Age
              <input className={styles.input} type="number" inputMode="numeric" min={1} max={120}
                value={form.age} onChange={update('age')} />
            </label>
          </div>
          <label className={styles.field}>
            Full name
            <input className={styles.input} autoComplete="name" value={form.fullname} onChange={update('fullname')} />
          </label>
          <label className={styles.field}>
            Email
            <input className={styles.input} type="email" autoComplete="email" value={form.email} onChange={update('email')} />
          </label>
          <label className={styles.field}>
            Password
            <input className={styles.input} type="password" autoComplete="new-password"
              value={form.password} onChange={update('password')} />
          </label>
          <label className={styles.field}>
            Confirm password
            <input className={styles.input} type="password" autoComplete="new-password"
              value={form.confirmPassword} onChange={update('confirmPassword')} />
          </label>

          {error && <p className={styles.error} role="alert">{error}</p>}

          <button type="submit" className={styles.primary} disabled={busy}>
            {busy ? 'Creating account…' : 'Sign Up'}
          </button>
        </form>
      </div>
    </div>
  )
}
