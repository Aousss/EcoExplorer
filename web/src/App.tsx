import { Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
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
        <Route
          path="explore"
          element={
            <Placeholder title="Explore" phase={7} replaces="ExploreFragment, WebARFragment*"
              features={['AR: Water Cycle and Learn Animals (8th Wall)', 'Download marker image', 'QR code on desktop']} />
          }
        />

        {/* Identify – nav_identify.xml */}
        <Route path="identify" element={<Identify />} />
        <Route path="identify/result" element={<IdentifyResult />} />
        <Route path="species/:id" element={<SpeciesDetail />} />

        {/* Challenge – nav_challenge.xml */}
        <Route
          path="challenge"
          element={
            <Placeholder title="Challenge" phase={6} replaces="ChallengeFragment"
              features={['Animals and Plants categories', 'Recent results']} />
          }
        />
        <Route
          path="challenge/:category"
          element={
            <Placeholder title="Quiz" phase={6} replaces="AnimalFragment / PlantFragment, *ChallengeQuiz" back
              features={['Quiz intro and last score', 'Start quiz']} />
          }
        />
        <Route
          path="challenge/:category/quiz"
          element={
            <Placeholder title="Quiz" phase={6} replaces="AnimalsQuizQuestions / PlantsQuizQuestions" back
              features={['Multiple-choice questions', 'Score saved to your profile']} />
          }
        />

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
