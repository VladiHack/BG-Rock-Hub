import { Routes, Route } from 'react-router-dom'
import { ROUTES } from '@/constants'
import Navbar from '@/components/Navbar'
import Footer from '@/components/Footer'
import ProtectedRoute from '@/components/ProtectedRoute'
import HomePage from '@/pages/HomePage'
import LoginPage from '@/pages/LoginPage'
import RegisterPage from '@/pages/RegisterPage'
import BandsPage from '@/pages/BandsPage'
import BandDetailPage from '@/pages/BandDetailPage'
import UnknownBandsPage from '@/pages/UnknownBandsPage'
import EventsPage from '@/pages/EventsPage'
import EventDetailPage from '@/pages/EventDetailPage'
import VenuesPage from '@/pages/VenuesPage'
import VenueDetailPage from '@/pages/VenueDetailPage'
import ProfilePage from '@/pages/ProfilePage'
import NotFoundPage from '@/pages/NotFoundPage'

export default function App() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 pt-16">
        <Routes>
          <Route path={ROUTES.HOME}          element={<HomePage />} />
          <Route path={ROUTES.LOGIN}         element={<LoginPage />} />
          <Route path={ROUTES.REGISTER}      element={<RegisterPage />} />
          <Route path={ROUTES.BANDS}         element={<BandsPage />} />
          <Route path={ROUTES.UNKNOWN_BANDS} element={<UnknownBandsPage />} />
          <Route path="/bands/:id"           element={<BandDetailPage />} />
          <Route path={ROUTES.EVENTS}        element={<EventsPage />} />
          <Route path="/events/:id"          element={<EventDetailPage />} />
          <Route path={ROUTES.VENUES}        element={<VenuesPage />} />
          <Route path="/venues/:id"          element={<VenueDetailPage />} />
          <Route
            path={ROUTES.PROFILE}
            element={<ProtectedRoute><ProfilePage /></ProtectedRoute>}
          />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </main>
      <Footer />
    </div>
  )
}
