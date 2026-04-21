import { useQuery } from '@tanstack/react-query'
import { User as UserIcon, MapPin, Calendar, Shield } from 'lucide-react'
import { api } from '@/api/axios'
import { ROLE_LABELS } from '@/constants'
import { formatShortDate } from '@/utils/format'
import type { User } from '@/types'
import Spinner from '@/components/Spinner'

export default function ProfilePage() {
  const { data: profile, isLoading } = useQuery({
    queryKey: ['profile'],
    queryFn: () => api.get<User>('/users/me').then((r) => r.data),
  })

  if (isLoading) return <Spinner />
  if (!profile) return null

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      <div className="card p-6">
        <div className="flex items-start gap-4">
          <div className="w-20 h-20 rounded-full bg-zinc-700 flex items-center justify-center overflow-hidden flex-shrink-0">
            {profile.avatarUrl ? (
              <img src={profile.avatarUrl} alt="" className="w-full h-full object-cover" />
            ) : (
              <UserIcon size={32} className="text-zinc-400" />
            )}
          </div>

          <div className="flex-1">
            <h1 className="text-2xl font-bold text-zinc-100">{profile.username}</h1>
            <p className="text-zinc-500 text-sm">{profile.email}</p>

            <div className="flex flex-wrap gap-3 mt-2 text-sm text-zinc-400">
              <span className="flex items-center gap-1 bg-rock-900 text-rock-300 px-2 py-0.5 rounded-full text-xs">
                <Shield size={12} />
                {ROLE_LABELS[profile.role]}
              </span>
              {profile.city && (
                <span className="flex items-center gap-1">
                  <MapPin size={14} />
                  {profile.city}
                </span>
              )}
              <span className="flex items-center gap-1">
                <Calendar size={14} />
                Регистриран {formatShortDate(profile.createdAt)}
              </span>
            </div>
          </div>
        </div>

        {profile.bio && (
          <p className="text-zinc-300 mt-4 leading-relaxed border-t border-zinc-800 pt-4">
            {profile.bio}
          </p>
        )}
      </div>
    </div>
  )
}
