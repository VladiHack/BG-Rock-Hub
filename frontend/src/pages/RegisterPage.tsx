import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Guitar } from 'lucide-react'
import { useMutation } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/store/authStore'
import { ROUTES, ROLE_LABELS, ROLE_DESCRIPTIONS, REGISTERABLE_ROLES } from '@/constants'
import type { Role } from '@/types'

export default function RegisterPage() {
  const navigate = useNavigate()
  const { setAuth } = useAuthStore()
  const [form, setForm] = useState({
    email: '', username: '', password: '', role: 'FAN' as Role, city: '',
  })

  const mutation = useMutation({
    mutationFn: authApi.register,
    onSuccess: (data) => {
      setAuth(
        { accessToken: data.accessToken, refreshToken: data.refreshToken },
        { id: data.userId, username: data.username, email: data.email, role: data.role },
      )
      toast.success('Добре дошъл в BG Rock Hub!')
      navigate(ROUTES.HOME)
    },
    onError: (err: any) =>
      toast.error(err.response?.data?.message ?? 'Грешка при регистрация'),
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    mutation.mutate(form)
  }

  const set = (field: keyof typeof form) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) =>
      setForm((prev) => ({ ...prev, [field]: e.target.value }))

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <Guitar size={40} className="text-rock-500 mx-auto mb-3" />
          <h1 className="text-2xl font-bold text-zinc-100">Регистрация</h1>
          <p className="text-zinc-500 mt-1">
            Вече имаш акаунт?{' '}
            <Link to={ROUTES.LOGIN} className="text-rock-400 hover:text-rock-300">Влез</Link>
          </p>
        </div>

        <form onSubmit={handleSubmit} className="card p-6 space-y-4">
          <div>
            <label className="label">Имейл</label>
            <input className="input" type="email" required value={form.email} onChange={set('email')} />
          </div>
          <div>
            <label className="label">Потребителско име</label>
            <input className="input" minLength={3} maxLength={50} required value={form.username} onChange={set('username')} />
          </div>
          <div>
            <label className="label">Парола</label>
            <input className="input" type="password" minLength={6} required value={form.password} onChange={set('password')} />
          </div>
          <div>
            <label className="label">Тип акаунт</label>
            <select className="input" value={form.role} onChange={set('role')}>
              {REGISTERABLE_ROLES.map((role) => (
                <option key={role} value={role}>
                  {ROLE_LABELS[role]} — {ROLE_DESCRIPTIONS[role]}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Град (по желание)</label>
            <input className="input" placeholder="София, Пловдив..." value={form.city} onChange={set('city')} />
          </div>
          <button type="submit" className="btn-primary w-full" disabled={mutation.isPending}>
            {mutation.isPending ? 'Регистрация...' : 'Регистрирай се'}
          </button>
        </form>
      </div>
    </div>
  )
}
