import { Link } from 'react-router-dom'
import { Guitar } from 'lucide-react'
import { ROUTES } from '@/constants'

const FOOTER_LINKS = [
  { to: ROUTES.BANDS,  label: 'Банди' },
  { to: ROUTES.EVENTS, label: 'Събития' },
  { to: ROUTES.VENUES, label: 'Клубове' },
] as const

export default function Footer() {
  return (
    <footer className="bg-zinc-900 border-t border-zinc-800 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-2 text-rock-500">
            <Guitar size={24} />
            <span className="font-rock text-base">BG Rock Hub</span>
          </div>

          <div className="flex gap-6 text-sm text-zinc-500">
            {FOOTER_LINKS.map((link) => (
              <Link key={link.to} to={link.to} className="hover:text-zinc-300 transition-colors">
                {link.label}
              </Link>
            ))}
          </div>

          <p className="text-zinc-600 text-sm">© {new Date().getFullYear()} BG Rock Hub</p>
        </div>
      </div>
    </footer>
  )
}
