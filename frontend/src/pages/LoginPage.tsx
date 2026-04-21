import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Guitar } from 'lucide-react'
import { useMutation } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/authStore'
import { ROUTES } from '@/constants'

export default function LoginPage() {
  const navigate = useNavigate()
  const { setAuth } = useAuthStore()
  const [form, setForm] = useState({ email: '', password: '' })

  const mutation = useMutation({
    mutationFn: authApi.login,
    onSuccess: (data) => {
      setAuth(
        { accessToken: data.accessToken, refreshToken: data.refreshToken },
        { id: data.userId, username: data.username, email: data.email, role: data.role },
      )
      toast.success(`Добре дошъл, ${data.username}!`)
      navigate(ROUTES.HOME)
    },
    onError: () => toast.error('Невалиден имейл или парола'),
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    mutation.mutate(form)
  }

  const set = (field: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((prev) => ({ ...prev, [field]: e.target.value }))

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Guitar size={40} className="text-rock-500 mx-auto mb-3" />
          <h1 className="text-2xl font-bold text-zinc-100">Вход в BG Rock Hub</h1>
          <p className="text-zinc-500 mt-1">
            Не си регистриран?{' '}
            <Link to={ROUTES.REGISTER} className="text-rock-400 hover:text-rock-300">
              Регистрирай се
            </Link>
          </p>
        </div>

        <form onSubmit={handleSubmit} className="card p-6 space-y-4">
          <div>
            <label className="label">Имейл</label>
            <input
              className="input"
              type="email"
              placeholder="email@example.com"
              required
              value={form.email}
              onChange={set('email')}
            />
          </div>
          <div>
            <label className="label">Парола</label>
            <input
              className="input"
              type="password"
              placeholder="••••••••"
              required
              value={form.password}
              onChange={set('password')}
            />
          </div>
          <button type="submit" className="btn-primary w-full" disabled={mutation.isPending}>
            {mutation.isPending ? 'Влизане...' : 'Вход'}
          </button>
        </form>
      </div>
    </div>
  )
}
