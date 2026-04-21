import { Link } from 'react-router-dom'
import { Guitar } from 'lucide-react'
import { ROUTES } from '@/constants'

export default function NotFoundPage() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-8rem)] text-center px-4">
      <Guitar size={64} className="text-zinc-700 mb-4" />
      <h1 className="text-6xl font-rock text-rock-600 mb-2">404</h1>
      <p className="text-xl text-zinc-400 mb-6">Страницата не е намерена</p>
      <Link to={ROUTES.HOME} className="btn-primary">Към началната страница</Link>
    </div>
  )
}
