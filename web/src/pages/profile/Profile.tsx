import { useRef, useState, type ChangeEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import PageHeader from '../../components/PageHeader'
import { PersonIcon } from '../../components/icons'
import { useAuth } from '../../context/AuthContext'
import { errorMessage, logout, uploadProfilePicture } from '../../lib/account'
import styles from './Profile.module.css'

const MAX_PICTURE_BYTES = 5 * 1024 * 1024

// Replaces ProfileFragment
export default function Profile() {
  const { user, profile, loading } = useAuth()
  const navigate = useNavigate()
  const fileInput = useRef<HTMLInputElement>(null)
  const [uploadStatus, setUploadStatus] = useState('')

  async function handlePicture(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]
    event.target.value = '' // allow choosing the same file again
    if (!file || !user) return
    if (!file.type.startsWith('image/')) return setUploadStatus('Please choose an image file.')
    if (file.size > MAX_PICTURE_BYTES) return setUploadStatus('Please choose an image under 5 MB.')

    setUploadStatus('Uploading…')
    try {
      await uploadProfilePicture(user.uid, file)
      setUploadStatus('Profile picture updated.')
    } catch (err) {
      setUploadStatus(errorMessage(err))
    }
  }

  async function handleLogout() {
    await logout()
    navigate('/login', { replace: true })
  }

  const displayName = loading ? '…' : profile ? `@${profile.username}` : 'Guest'

  return (
    <>
      <PageHeader title="Profile" />

      <section className={styles.identity}>
        <div className={styles.avatar}>
          {profile?.profileImageUrl ? <img src={profile.profileImageUrl} alt="" /> : <PersonIcon />}
        </div>
        <p className={styles.name}>{displayName}</p>

        {user && (
          <>
            <button type="button" className={styles.changePicture} onClick={() => fileInput.current?.click()}>
              Change Profile Picture
            </button>
            <input ref={fileInput} type="file" accept="image/*" hidden onChange={handlePicture} />
            {uploadStatus && <p className={styles.status} role="status">{uploadStatus}</p>}
          </>
        )}
      </section>

      {!loading && !user && (
        <Link to="/login" state={{ from: '/profile' }} className={`${styles.card} ${styles.loginCard}`}>
          <strong>Sign in</strong> to your account!
          <span className={styles.chevron}>›</span>
        </Link>
      )}

      {profile && (
        <dl className={`${styles.card} ${styles.info}`}>
          <dt>Username</dt>
          <dd>{profile.username}</dd>
          <dt>Full Name</dt>
          <dd>{profile.fullname}</dd>
          <dt>Age</dt>
          <dd>{profile.age}</dd>
          <dt>Email</dt>
          <dd>{profile.email}</dd>
        </dl>
      )}

      <div className={styles.card}>
        <Link to="/feedback" className={styles.row}>
          Help &amp; Feedback
          <span className={styles.chevron}>›</span>
        </Link>
        {user && (
          <button type="button" className={`${styles.row} ${styles.logout}`} onClick={handleLogout}>
            Log out
          </button>
        )}
      </div>
    </>
  )
}
