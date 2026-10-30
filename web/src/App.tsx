import { Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import RequireAuth from './components/RequireAuth'
import Challenge from './pages/challenge/Challenge'
import ChallengeCategory from './pages/challenge/ChallengeCategory'
import Quiz from './pages/challenge/Quiz'
import Explore from './pages/explore/Explore'
import Placeholder from './pages/Placeholder'
import ForgotPassword from './pages/auth/ForgotPassword'
import Login from './pages/auth/Login'
import Register from './pages/auth/Register'
import FunFactDetail from './pages/home/FunFactDetail'
import Home from './pages/home/Home'
import Identify from './pages/identify/Identify'
import IdentifyResult from './pages/identify/IdentifyResult'
import SpeciesDetail from './pages/identify/SpeciesDetail'
import Feedback from './pages/profile/Feedback'
import Profile from './pages/profile/Profile'

// Route map from docs/WEB_MIGRATION_PLAN.md §8. Each Placeholder is replaced by its real page in a later phase.
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Home – nav_home.xml */}
        <Route index element={<Home />} />
        <Route path="funfacts/:id" element={<FunFactDetail />} />

        {/* Explore – nav_explore.xml */}
        <Route path="explore" element={<Explore />} />

        {/* Identify – nav_identify.xml */}
        <Route path="identify" element={<Identify />} />
        <Route path="identify/result" element={<IdentifyResult />} />
        <Route path="species/:id" element={<SpeciesDetail />} />

        {/* Challenge – nav_challenge.xml */}
        <Route path="challenge" element={<Challenge />} />
        <Route path="challenge/:category" element={<RequireAuth><ChallengeCategory /></RequireAuth>} />
        <Route path="challenge/:category/quiz" element={<RequireAuth><Quiz /></RequireAuth>} />

        {/* Profile & auth – nav_profile.xml + auth activities */}
        <Route path="profile" element={<Profile />} />
        <Route path="feedback" element={<Feedback />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />
        <Route path="forgot-password" element={<ForgotPassword />} />

        <Route
          path="*"
          element={<Placeholder title="Page not found" phase={2} replaces="–" back features={['This page does not exist.']} />}
        />
      </Route>
    </Routes>
  )
}
