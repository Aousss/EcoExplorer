import { useNavigate } from 'react-router-dom'
import { ArrowBackIcon } from './icons'
import styles from './PageHeader.module.css'

type Props = {
  title: string
  subtitle?: string
  /** Show a back arrow (detail screens in the Android app have one) */
  back?: boolean
}

export default function PageHeader({ title, subtitle, back }: Props) {
  const navigate = useNavigate()

  return (
    <header className={styles.header}>
      {back && (
        <button type="button" className={styles.back} onClick={() => navigate(-1)} aria-label="Back">
          <ArrowBackIcon />
        </button>
      )}
      <div>
        <h1 className={styles.title}>{title}</h1>
        {subtitle && <p className={styles.subtitle}>{subtitle}</p>}
      </div>
    </header>
  )
}
