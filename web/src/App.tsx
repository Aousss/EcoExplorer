import { Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import Placeholder from './pages/Placeholder'

// Route map from docs/WEB_MIGRATION_PLAN.md §8. Each Placeholder is replaced by its real page in a later phase.
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Home – nav_home.xml */}
        <Route
          index
          element={
            <Placeholder
              title="EcoExplorer"
              phase={4}
              replaces="HomeFragment"
              features={['Greeting (guest or username)', 'Online / offline badge', 'Random fun fact', 'Shortcut cards']}
            />
          }
        />
        <Route
          path="funfacts/:id"
          element={
            <Placeholder title="Fun Fact" phase={4} replaces="FunFactsDetailFragment" back
              features={['Fact text, description, image and source']} />
          }
        />

        {/* Explore – nav_explore.xml */}
        <Route
          path="explore"
          element={
            <Placeholder title="Explore" phase={7} replaces="ExploreFragment, WebARFragment*"
              features={['AR: Water Cycle and Learn Animals (8th Wall)', 'Download marker image', 'QR code on desktop']} />
          }
        />

        {/* Identify – nav_identify.xml */}
        <Route
          path="identify"
          element={
            <Placeholder title="Identify" phase={5} replaces="IdentifyFragment"
              features={['Plant and insect lists', 'Take photo or choose from gallery', 'AI identification in the browser']} />
          }
        />
        <Route
          path="identify/result"
          element={
            <Placeholder title="Result" phase={5} replaces="IdentifyResults" back
              features={['Detected species and confidence', 'Identify again']} />
          }
        />
        <Route
          path="species/:id"
          element={
            <Placeholder title="Species" phase={5} replaces="DetailsFragment" back
              features={['Description, scientific name, ecosystem, habitat, role']} />
          }
        />

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
        <Route
          path="profile"
          element={
            <Placeholder title="Profile" phase={3} replaces="ProfileFragment"
              features={['User info and profile picture', 'Help & feedback', 'Log out']} />
          }
        />
        <Route
          path="feedback"
          element={<Placeholder title="Help & Feedback" phase={3} replaces="Feedback" back features={['Feedback form']} />}
        />
        <Route
          path="login"
          element={
            <Placeholder title="Log in" phase={3} replaces="LoginActivity" back
              features={['Email and password', 'Continue as guest']} />
          }
        />
        <Route
          path="register"
          element={<Placeholder title="Register" phase={3} replaces="RegisterActivity" back features={['Create an account']} />}
        />
        <Route
          path="forgot-password"
          element={
            <Placeholder title="Forgot password" phase={3} replaces="ForgotPasswordActivity" back
              features={['Send password reset email']} />
          }
        />

        <Route
          path="*"
          element={<Placeholder title="Page not found" phase={2} replaces="–" back features={['This page does not exist.']} />}
        />
      </Route>
    </Routes>
  )
}
