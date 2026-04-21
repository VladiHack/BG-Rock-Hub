import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  Users, Music, MapPin, Star, BarChart3,
  CheckCircle, XCircle, Trash2, ShieldCheck,
} from 'lucide-react'
import { adminApi } from '@/api/admin'
import Spinner from '@/components/Spinner'
import toast from 'react-hot-toast'
import { ROLE_LABELS } from '@/constants'
import type { Role } from '@/types'

type Tab = 'stats' | 'users' | 'bands' | 'venues' | 'reviews'

// ─── Stat Card ──────────────────────────────────────────────────────────────

function StatCard({ label, value, icon: Icon, color }: {
  label: string; value: number; icon: React.ElementType; color: string
}) {
  return (
    <div className="card p-5 flex items-center gap-4">
      <div className={`p-3 rounded-lg ${color}`}>
        <Icon size={22} className="text-white" />
      </div>
      <div>
        <p className="text-2xl font-bold text-zinc-100">{value.toLocaleString()}</p>
        <p className="text-sm text-zinc-400">{label}</p>
      </div>
    </div>
  )
}

// ─── Badge ──────────────────────────────────────────────────────────────────

function Badge({ active, labelTrue, labelFalse }: {
  active: boolean; labelTrue: string; labelFalse: string
}) {
  return (
    <span className={`px-2 py-0.5 rounded text-xs font-medium ${
      active ? 'bg-green-900 text-green-300' : 'bg-zinc-700 text-zinc-400'
    }`}>
      {active ? labelTrue : labelFalse}
    </span>
  )
}

// ─── AdminPage ───────────────────────────────────────────────────────────────

export default function AdminPage() {
  const [tab, setTab] = useState<Tab>('stats')
  const [reviewFilter, setReviewFilter] = useState<boolean | undefined>(undefined)
  const qc = useQueryClient()

  const TABS: { key: Tab; label: string; icon: React.ElementType }[] = [
    { key: 'stats',   label: 'Статистики',   icon: BarChart3 },
    { key: 'users',   label: 'Потребители',  icon: Users },
    { key: 'bands',   label: 'Банди',        icon: Music },
    { key: 'venues',  label: 'Клубове',      icon: MapPin },
    { key: 'reviews', label: 'Ревюта',       icon: Star },
  ]

  // ── Data queries ─────────────────────────────────────────────────────────

  const { data: stats, isLoading: statsLoading } =
    useQuery({ queryKey: ['admin-stats'], queryFn: adminApi.getStats })

  const { data: users, isLoading: usersLoading } =
    useQuery({ queryKey: ['admin-users'], queryFn: () => adminApi.getUsers(0, 50), enabled: tab === 'users' })

  const { data: bands, isLoading: bandsLoading } =
    useQuery({ queryKey: ['admin-bands'], queryFn: () => adminApi.getBands(0, 50), enabled: tab === 'bands' })

  const { data: venues, isLoading: venuesLoading } =
    useQuery({ queryKey: ['admin-venues'], queryFn: () => adminApi.getVenues(0, 50), enabled: tab === 'venues' })

  const { data: reviews, isLoading: reviewsLoading } =
    useQuery({
      queryKey: ['admin-reviews', reviewFilter],
      queryFn: () => adminApi.getReviews(0, 50, reviewFilter),
      enabled: tab === 'reviews',
    })

  // ── Mutations ────────────────────────────────────────────────────────────

  const changeRole = useMutation({
    mutationFn: ({ id, role }: { id: number; role: string }) => adminApi.changeUserRole(id, role),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['admin-users'] }); toast.success('Ролята е обновена') },
    onError: () => toast.error('Грешка при промяна на роля'),
  })

  const toggleActive = useMutation({
    mutationFn: (id: number) => adminApi.toggleUserActive(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['admin-users'] }); toast.success('Статусът е обновен') },
    onError: () => toast.error('Грешка'),
  })

  const verifyBand = useMutation({
    mutationFn: (id: number) => adminApi.verifyBand(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['admin-bands'] }); toast.success('Статусът на банда е обновен') },
    onError: () => toast.error('Грешка'),
  })

  const deleteBand = useMutation({
    mutationFn: (id: number) => adminApi.deleteBand(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['admin-bands'] }); toast.success('Банда е изтрита') },
    onError: () => toast.error('Грешка при изтриване'),
  })

  const verifyVenue = useMutation({
    mutationFn: (id: number) => adminApi.verifyVenue(id),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['admin-venues'] }); toast.success('Статусът на клуб е обновен') },
    onError: () => toast.error('Грешка'),
  })

  const approveReview = useMutation({
    mutationFn: (id: number) => adminApi.approveReview(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['admin-reviews'] })
      qc.invalidateQueries({ queryKey: ['admin-stats'] })
      toast.success('Ревюто е обновено')
    },
    onError: () => toast.error('Грешка'),
  })

  const deleteReview = useMutation({
    mutationFn: (id: number) => adminApi.deleteReview(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['admin-reviews'] })
      qc.invalidateQueries({ queryKey: ['admin-stats'] })
      toast.success('Ревюто е изтрито')
    },
    onError: () => toast.error('Грешка'),
  })

  // ── Render ───────────────────────────────────────────────────────────────

  return (
    <div className="max-w-7xl mx-auto px-4 py-10">
      {/* Header */}
      <div className="flex items-center gap-3 mb-8">
        <ShieldCheck size={32} className="text-rock-500" />
        <div>
          <h1 className="text-3xl font-rock text-zinc-100">Администрация</h1>
          <p className="text-zinc-500 text-sm mt-1">Управление на платформата</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-8 border-b border-zinc-800 overflow-x-auto">
        {TABS.map(({ key, label, icon: Icon }) => (
          <button
            key={key}
            onClick={() => setTab(key)}
            className={`flex items-center gap-2 px-4 py-3 text-sm font-medium whitespace-nowrap border-b-2 transition-colors ${
              tab === key
                ? 'border-rock-500 text-rock-400'
                : 'border-transparent text-zinc-500 hover:text-zinc-300'
            }`}
          >
            <Icon size={16} />
            {label}
          </button>
        ))}
      </div>

      {/* ── Stats tab ─────────────────────────────────────────────────────── */}
      {tab === 'stats' && (
        statsLoading ? <Spinner /> : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
            <StatCard label="Потребители"  value={stats?.totalUsers ?? 0}    icon={Users}   color="bg-blue-600" />
            <StatCard label="Банди"        value={stats?.totalBands ?? 0}    icon={Music}   color="bg-rock-600" />
            <StatCard label="Клубове"      value={stats?.totalVenues ?? 0}   icon={MapPin}  color="bg-purple-600" />
            <StatCard label="Събития"      value={stats?.totalEvents ?? 0}   icon={BarChart3} color="bg-green-600" />
            <StatCard label="Ревюта"       value={stats?.totalReviews ?? 0}  icon={Star}    color="bg-yellow-600" />
            <StatCard label="За одобрение" value={stats?.pendingReviews ?? 0} icon={XCircle} color="bg-red-600" />
          </div>
        )
      )}

      {/* ── Users tab ─────────────────────────────────────────────────────── */}
      {tab === 'users' && (
        usersLoading ? <Spinner /> : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-zinc-800 text-zinc-400 text-left">
                  <th className="pb-3 pr-4">Потребител</th>
                  <th className="pb-3 pr-4">Имейл</th>
                  <th className="pb-3 pr-4">Роля</th>
                  <th className="pb-3 pr-4">Статус</th>
                  <th className="pb-3">Действия</th>
                </tr>
              </thead>
              <tbody>
                {users?.content.map(u => (
                  <tr key={u.id} className="border-b border-zinc-800/50 hover:bg-zinc-900/40">
                    <td className="py-3 pr-4 text-zinc-100 font-medium">{u.username}</td>
                    <td className="py-3 pr-4 text-zinc-400">{u.email}</td>
                    <td className="py-3 pr-4">
                      <select
                        value={u.role}
                        onChange={e => changeRole.mutate({ id: u.id, role: e.target.value })}
                        className="bg-zinc-800 border border-zinc-700 text-zinc-200 rounded px-2 py-1 text-xs"
                      >
                        {(['FAN', 'BAND', 'VENUE', 'ADMIN'] as Role[]).map(r => (
                          <option key={r} value={r}>{ROLE_LABELS[r]}</option>
                        ))}
                      </select>
                    </td>
                    <td className="py-3 pr-4">
                      <Badge active={u.isActive} labelTrue="Активен" labelFalse="Деактивиран" />
                    </td>
                    <td className="py-3">
                      <button
                        onClick={() => toggleActive.mutate(u.id)}
                        className={`text-xs px-3 py-1 rounded transition-colors ${
                          u.isActive
                            ? 'bg-zinc-700 hover:bg-red-900 text-zinc-300 hover:text-red-300'
                            : 'bg-zinc-700 hover:bg-green-900 text-zinc-300 hover:text-green-300'
                        }`}
                      >
                        {u.isActive ? 'Деактивирай' : 'Активирай'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      )}

      {/* ── Bands tab ─────────────────────────────────────────────────────── */}
      {tab === 'bands' && (
        bandsLoading ? <Spinner /> : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-zinc-800 text-zinc-400 text-left">
                  <th className="pb-3 pr-4">Банда</th>
                  <th className="pb-3 pr-4">Жанр</th>
                  <th className="pb-3 pr-4">Град</th>
                  <th className="pb-3 pr-4">Верифицирана</th>
                  <th className="pb-3 pr-4">Рейтинг</th>
                  <th className="pb-3">Действия</th>
                </tr>
              </thead>
              <tbody>
                {bands?.content.map(b => (
                  <tr key={b.id} className="border-b border-zinc-800/50 hover:bg-zinc-900/40">
                    <td className="py-3 pr-4 text-zinc-100 font-medium">{b.name}</td>
                    <td className="py-3 pr-4 text-zinc-400">{b.genre}</td>
                    <td className="py-3 pr-4 text-zinc-400">{b.city ?? '—'}</td>
                    <td className="py-3 pr-4">
                      <Badge active={b.isVerified} labelTrue="Да" labelFalse="Не" />
                    </td>
                    <td className="py-3 pr-4 text-zinc-300">⭐ {b.avgRating?.toFixed(1)}</td>
                    <td className="py-3 flex gap-2">
                      <button
                        onClick={() => verifyBand.mutate(b.id)}
                        title={b.isVerified ? 'Отмени верификация' : 'Верифицирай'}
                        className="p-1.5 rounded hover:bg-zinc-700 text-zinc-400 hover:text-green-400 transition-colors"
                      >
                        <CheckCircle size={16} />
                      </button>
                      <button
                        onClick={() => {
                          if (confirm(`Изтриване на "${b.name}"?`)) deleteBand.mutate(b.id)
                        }}
                        title="Изтрий"
                        className="p-1.5 rounded hover:bg-zinc-700 text-zinc-400 hover:text-red-400 transition-colors"
                      >
                        <Trash2 size={16} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      )}

      {/* ── Venues tab ────────────────────────────────────────────────────── */}
      {tab === 'venues' && (
        venuesLoading ? <Spinner /> : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-zinc-800 text-zinc-400 text-left">
                  <th className="pb-3 pr-4">Клуб</th>
                  <th className="pb-3 pr-4">Град</th>
                  <th className="pb-3 pr-4">Капацитет</th>
                  <th className="pb-3 pr-4">Верифициран</th>
                  <th className="pb-3 pr-4">Рейтинг</th>
                  <th className="pb-3">Действия</th>
                </tr>
              </thead>
              <tbody>
                {venues?.content.map(v => (
                  <tr key={v.id} className="border-b border-zinc-800/50 hover:bg-zinc-900/40">
                    <td className="py-3 pr-4 text-zinc-100 font-medium">{v.name}</td>
                    <td className="py-3 pr-4 text-zinc-400">{v.city}</td>
                    <td className="py-3 pr-4 text-zinc-400">{v.capacity ?? '—'}</td>
                    <td className="py-3 pr-4">
                      <Badge active={v.isVerified} labelTrue="Да" labelFalse="Не" />
                    </td>
                    <td className="py-3 pr-4 text-zinc-300">⭐ {v.avgRating?.toFixed(1)}</td>
                    <td className="py-3">
                      <button
                        onClick={() => verifyVenue.mutate(v.id)}
                        title={v.isVerified ? 'Отмени верификация' : 'Верифицирай'}
                        className="p-1.5 rounded hover:bg-zinc-700 text-zinc-400 hover:text-green-400 transition-colors"
                      >
                        <CheckCircle size={16} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )
      )}

      {/* ── Reviews tab ───────────────────────────────────────────────────── */}
      {tab === 'reviews' && (
        <div>
          {/* Filter */}
          <div className="flex gap-2 mb-5">
            {[
              { label: 'Всички',       value: undefined },
              { label: 'Одобрени',     value: true },
              { label: 'За одобрение', value: false },
            ].map(opt => (
              <button
                key={String(opt.value)}
                onClick={() => setReviewFilter(opt.value)}
                className={`text-sm px-3 py-1.5 rounded transition-colors ${
                  reviewFilter === opt.value
                    ? 'bg-rock-600 text-white'
                    : 'bg-zinc-800 text-zinc-400 hover:text-zinc-200'
                }`}
              >
                {opt.label}
              </button>
            ))}
          </div>

          {reviewsLoading ? <Spinner /> : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-zinc-800 text-zinc-400 text-left">
                    <th className="pb-3 pr-4">Автор</th>
                    <th className="pb-3 pr-4">Обект</th>
                    <th className="pb-3 pr-4">Рейтинг</th>
                    <th className="pb-3 pr-4">Съдържание</th>
                    <th className="pb-3 pr-4">Статус</th>
                    <th className="pb-3">Действия</th>
                  </tr>
                </thead>
                <tbody>
                  {reviews?.content.map(r => (
                    <tr key={r.id} className="border-b border-zinc-800/50 hover:bg-zinc-900/40">
                      <td className="py-3 pr-4 text-zinc-100">{r.reviewerUsername}</td>
                      <td className="py-3 pr-4 text-zinc-400 text-xs">
                        {r.targetType} #{r.targetId}
                      </td>
                      <td className="py-3 pr-4 text-yellow-400">{'★'.repeat(r.rating)}</td>
                      <td className="py-3 pr-4 text-zinc-400 max-w-xs truncate">
                        {r.content || '—'}
                      </td>
                      <td className="py-3 pr-4">
                        <Badge active={r.isApproved} labelTrue="Одобрено" labelFalse="Чакащо" />
                      </td>
                      <td className="py-3 flex gap-2">
                        <button
                          onClick={() => approveReview.mutate(r.id)}
                          title={r.isApproved ? 'Отмени одобрение' : 'Одобри'}
                          className="p-1.5 rounded hover:bg-zinc-700 text-zinc-400 hover:text-green-400 transition-colors"
                        >
                          <CheckCircle size={16} />
                        </button>
                        <button
                          onClick={() => {
                            if (confirm('Изтриване на ревюто?')) deleteReview.mutate(r.id)
                          }}
                          title="Изтрий"
                          className="p-1.5 rounded hover:bg-zinc-700 text-zinc-400 hover:text-red-400 transition-colors"
                        >
                          <Trash2 size={16} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
