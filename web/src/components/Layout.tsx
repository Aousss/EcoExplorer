import { Outlet, matchPath, useLocation } from 'react-router-dom'
import BottomNav from './BottomNav'
import styles from './Layout.module.css'

// Screens that hide the bottom nav, as MainActivity does for the quiz and auth screens
const FULL_SCREEN_ROUTES = ['/challenge/:category/quiz', '/login', '/register', '/forgot-password']

export default function Layout() {
  const { pathname } = useLocation()
  const showNav = !FULL_SCREEN_ROUTES.some((pattern) => matchPath(pattern, pathname))

  return (
    <div className={styles.app}>
      <main className={showNav ? styles.withNav : undefined}>
        <Outlet />
      </main>
      {showNav && <BottomNav />}
    </div>
  )
}
