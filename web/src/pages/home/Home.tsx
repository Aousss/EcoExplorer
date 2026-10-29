import { useEffect, useState, type ComponentType, type SVGProps } from 'react'
import { Link } from 'react-router-dom'
import Avatar from '../../components/Avatar'
import { ArIcon, CameraIcon, ChallengeIcon } from '../../components/icons'
import { useAuth } from '../../context/AuthContext'
import { useOnlineStatus } from '../../hooks/useOnlineStatus'
import { fetchFunFacts, pickRandom, type FunFact } from '../../lib/funfacts'
import styles from './Home.module.css'

type DiscoverCard = { to: string; title: string; text: string; Icon: ComponentType<SVGProps<SVGSVGElement>> }

// Same three cards and wording as home.xml
const DISCOVER: DiscoverCard[] = [
  { to: '/explore', title: 'AR Explore', text: 'Explore the unreality', Icon: ArIcon },
  { to: '/identify', title: 'Identify', text: 'Recognize the plants and animals', Icon: CameraIcon },
  { to: '/challenge', title: 'Challenge', text: 'Challenge yourself', Icon: ChallengeIcon },
]

type FactsState =
  | { status: 'loading' }
  | { status: 'error' }
  | { status: 'ready'; facts: FunFact[]; current: FunFact | null }

// Replaces HomeFragment
export default function Home() {
  const { profile, loading: authLoading } = useAuth()
  const online = useOnlineStatus()
  const [facts, setFacts] = useState<FactsState>({ status: 'loading' })

  useEffect(() => {
    let cancelled = false
    fetchFunFacts()
      .then((all) => !cancelled && setFacts({ status: 'ready', facts: all, current: pickRandom(all) }))
      .catch(() => !cancelled && setFacts({ status: 'error' }))
    return () => {
      cancelled = true
    }
  }, [])

  function showAnotherFact() {
    if (facts.status === 'ready') setFacts({ ...facts, current: pickRandom(facts.facts, facts.current?.id) })
  }

  return (
    <>
      <header className={styles.header}>
        <div className={styles.greeting}>
          {profile ? (
            <>
              <Avatar src={profile.profileImageUrl} className={styles.avatar} />
              <div>
                <p className={styles.welcome}>Welcome,</p>
                <h1 className={styles.name}>{profile.username}</h1>
              </div>
            </>
          ) : (
            <>
              <img src="/images/logo-mark.png" alt="" className={styles.mark} />
              <div>
                <p className={styles.welcome}>Welcome to</p>
                <h1 className={styles.name}>{authLoading ? '…' : 'EcoExplorer'}</h1>
              </div>
            </>
          )}
        </div>
        <span className={`${styles.status} ${online ? styles.online : styles.offline}`}>
          {online ? 'online' : 'offline'}
        </span>
      </header>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>Fun Facts</h2>
        <div className={styles.factCard}>
          {facts.status === 'loading' && <p className={styles.factText}>Loading a fun fact…</p>}
          {facts.status === 'error' && <p className={styles.factText}>Failed to load fun facts.</p>}
          {facts.status === 'ready' && !facts.current && <p className={styles.factText}>No fun facts found.</p>}
          {facts.status === 'ready' && facts.current && (
            <>
              <p className={styles.factText}>{facts.current.text}</p>
              <div className={styles.factActions}>
                <Link to={`/funfacts/${facts.current.id}`} className={styles.learnMore}>
                  Learn More
                </Link>
                {facts.facts.length > 1 && (
                  <button type="button" className={styles.another} onClick={showAnotherFact}>
                    Another fact
                  </button>
                )}
              </div>
            </>
          )}
        </div>
      </section>

      <section className={styles.section}>
        <h2 className={styles.sectionTitle}>Discover</h2>
        <div className={styles.discover}>
          {DISCOVER.map(({ to, title, text, Icon }) => (
            <Link key={to} to={to} className={styles.discoverCard}>
              <Icon className={styles.discoverIcon} />
              <strong>{title}</strong>
              <span>{text}</span>
            </Link>
          ))}
        </div>
      </section>
    </>
  )
}
