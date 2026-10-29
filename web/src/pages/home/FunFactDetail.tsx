import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import PageHeader from '../../components/PageHeader'
import { fetchFunFact, type FunFact } from '../../lib/funfacts'
import styles from './FunFactDetail.module.css'

/** "nationalgeographic.com" for a web link, null if the source is plain text */
function hostname(source: string): string | null {
  if (!/^https?:\/\//.test(source)) return null
  try {
    return new URL(source).hostname.replace(/^www\./, '')
  } catch {
    return null
  }
}

// Replaces FunFactsDetailFragment. Loads by id, so the page also works when opened from a shared link.
export default function FunFactDetail() {
  const { id = '' } = useParams()
  const [fact, setFact] = useState<FunFact | null | undefined>(undefined)
  const [failed, setFailed] = useState(false)

  useEffect(() => {
    let cancelled = false
    fetchFunFact(id)
      .then((result) => !cancelled && setFact(result))
      .catch(() => !cancelled && setFailed(true))
    return () => {
      cancelled = true
    }
  }, [id])

  return (
    <>
      <PageHeader title="Fun Facts" back />
      <article className={styles.body}>
        {failed && <p className={styles.muted}>Failed to load this fun fact.</p>}
        {!failed && fact === undefined && <p className={styles.muted}>Loading…</p>}
        {fact === null && <p className={styles.muted}>This fun fact doesn&apos;t exist.</p>}
        {fact && (
          <>
            <h2 className={styles.title}>{fact.text}</h2>
            {fact.imageUrl && (
              <img
                src={fact.imageUrl}
                alt=""
                className={styles.image}
                onError={(e) => (e.currentTarget.hidden = true)}
              />
            )}
            {fact.description && <p className={styles.description}>{fact.description}</p>}
            {fact.source && (
              <p className={styles.source}>
                Source:{' '}
                {hostname(fact.source) ? (
                  <a href={fact.source} target="_blank" rel="noreferrer">
                    {hostname(fact.source)}
                  </a>
                ) : (
                  fact.source
                )}
              </p>
            )}
          </>
        )}
      </article>
    </>
  )
}
