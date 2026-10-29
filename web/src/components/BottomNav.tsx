import { NavLink } from 'react-router-dom'
import type { ComponentType, SVGProps } from 'react'
import { ArIcon, CameraIcon, ChallengeIcon, HomeIcon, PersonIcon } from './icons'
import styles from './BottomNav.module.css'

type Tab = { to: string; label: string; Icon: ComponentType<SVGProps<SVGSVGElement>>; end?: boolean }

// Same order as res/menu/bottom_nav_menu.xml; the centre slot is the camera FAB
const LEFT_TABS: Tab[] = [
  { to: '/', label: 'Home', Icon: HomeIcon, end: true },
  { to: '/explore', label: 'Explore', Icon: ArIcon },
]
const RIGHT_TABS: Tab[] = [
  { to: '/challenge', label: 'Challenge', Icon: ChallengeIcon },
  { to: '/profile', label: 'Profile', Icon: PersonIcon },
]

function TabLink({ to, label, Icon, end }: Tab) {
  return (
    <NavLink to={to} end={end} className={({ isActive }) => `${styles.tab} ${isActive ? styles.active : ''}`}>
      <Icon className={styles.icon} />
      <span>{label}</span>
    </NavLink>
  )
}

export default function BottomNav() {
  return (
    <nav className={styles.nav} aria-label="Main">
      {LEFT_TABS.map((tab) => (
        <TabLink key={tab.to} {...tab} />
      ))}

      <div className={styles.fabSlot}>
        <NavLink
          to="/identify"
          className={({ isActive }) => `${styles.fab} ${isActive ? styles.fabActive : ''}`}
          aria-label="Identify species"
        >
          <CameraIcon />
        </NavLink>
      </div>

      {RIGHT_TABS.map((tab) => (
        <TabLink key={tab.to} {...tab} />
      ))}
    </nav>
  )
}
