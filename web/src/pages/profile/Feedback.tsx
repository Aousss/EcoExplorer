import PageHeader from '../../components/PageHeader'
import styles from './Feedback.module.css'

// Same Google Form as Feedback.java, in embedded mode
const FORM_URL = 'https://docs.google.com/forms/d/e/1FAIpQLSeEp7wElg_GSNcLCfLZoIlJdkqP5cZeFoxBOHbV_dOWvsw6Kw/viewform'

// Replaces Feedback (WebView fragment)
export default function Feedback() {
  return (
    <>
      <PageHeader title="Help & Feedback" back />
      <iframe className={styles.form} src={`${FORM_URL}?embedded=true`} title="EcoExplorer feedback form" />
      <p className={styles.fallback}>
        Form not loading?{' '}
        <a href={FORM_URL} target="_blank" rel="noreferrer">
          Open it in a new tab
        </a>
      </p>
    </>
  )
}
