import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Guitar, Menu, X, LogOut, User } from 'lucide-react'
import { useAuthStore } from '@/store/authStore'
import { ROUTES } from '@/constants'

const NAV_LINKS = [
  { to: ROUTES.BANDS,         label: 'Банди' },
  { to: ROUTES.EVENTS,        label: 'Събития' },
  { to: ROUTES.VENUES,        label: 'Клубове' },
  { to: ROUTES.UNKNOWN_BANDS, label: 'Непознати' },
] as const

export default function Navbar() {
  const [open, setOpen] = useState(false)
  const { isAuthenticated, user, clearAuth } = useAuthStore()
  const navigate = useNavigate()

  const handleLogout = () => {
    clearAuth()
    navigate(ROUTES.HOME)
  }

  const close = () => setOpen(false)

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-zinc-950/90 backdrop-blur-sm border-b border-zinc-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to={ROUTES.HOME} className="flex items-center gap-2 text-rock-500 hover:text-rock-400 transition-colors">
            <Guitar size={28} />
            <span className="font-rock text-lg hidden sm:block">BG Rock Hub</span>
          </Link>

          {/* Desktop links */}
          <div className="hidden md:flex items-center gap-6">
            {NAV_LINKS.map((link) => (
              <Link
                key={link.to}
                to={link.to}
                className="text-zinc-400 hover:text-zinc-100 text-sm font-medium transition-colors"
              >
                {link.label}
              </Link>
            ))}
          </div>

          {/* Desktop auth */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <>
                <Link
                  to={ROUTES.PROFILE}
                  className="flex items-center gap-2 text-zinc-400 hover:text-zinc-100 text-sm transition-colors"
                >
                  <User size={18} />
                  {user?.username}
                </Link>
                <button
                  onClick={handleLogout}
                  className="text-zinc-500 hover:text-rock-400 transition-colors"
                  aria-label="Изход"
                >
                  <LogOut size={18} />
                </button>
              </>
            ) : (
              <>
                <Link to={ROUTES.LOGIN} className="btn-secondary text-sm py-1.5 px-3">Вход</Link>
                <Link to={ROUTES.REGISTER} className="btn-primary text-sm py-1.5 px-3">Регистрация</Link>
              </>
            )}
          </div>

          {/* Mobile burger */}
          <button
            className="md:hidden text-zinc-400"
            onClick={() => setOpen(!open)}
            aria-label="Меню"
          >
            {open ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      {/* Mobile drawer */}
      {open && (
        <div className="md:hidden border-t border-zinc-800 bg-zinc-950 px-4 py-4 space-y-3">
          {NAV_LINKS.map((link) => (
            <Link
              key={link.to}
              to={link.to}
              onClick={close}
              className="block text-zinc-300 hover:text-zinc-100 py-1"
            >
              {link.label}
            </Link>
          ))}
          <div className="pt-3 border-t border-zinc-800 flex gap-3">
            {isAuthenticated ? (
              <button onClick={handleLogout} className="btn-secondary text-sm w-full">Изход</button>
            ) : (
              <>
                <Link to={ROUTES.LOGIN} onClick={close} className="btn-secondary text-sm flex-1 text-center">Вход</Link>
                <Link to={ROUTES.REGISTER} onClick={close} className="btn-primary text-sm flex-1 text-center">Регистрация</Link>
              </>
            )}
          </div>
        </div>
      )}
    </nav>
  )
}
