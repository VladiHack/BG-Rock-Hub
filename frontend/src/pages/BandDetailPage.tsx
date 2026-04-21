import { useParams } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Users, MapPin, Calendar, ExternalLink as ExternalLinkIcon, Music, Heart } from 'lucide-react'
import toast from 'react-hot-toast'
import { bandsApi } from '@/api/bands'
import { GENRE_LABELS } from '@/constants'
import { useAuthStore } from '@/store/authStore'
import StarRating from '@/components/StarRating'
import ReviewsList from '@/components/ReviewsList'
import Spinner from '@/components/Spinner'

export default function BandDetailPage() {
  const { id } = useParams<{ id: string }>()
  const bandId = Number(id)
  const { isAuthenticated } = useAuthStore()
  const queryClient = useQueryClient()

  const { data: band, isLoading } = useQuery({
    queryKey: ['band', bandId],
    queryFn: () => bandsApi.getById(bandId),
  })

  const { data: followData } = useQuery({
    queryKey: ['band', bandId, 'following'],
    queryFn: () => bandsApi.isFollowing(bandId),
    enabled: isAuthenticated,
  })

  const followMutation = useMutation({
    mutationFn: () => bandsApi.toggleFollow(bandId),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ['band', bandId] })
      queryClient.invalidateQueries({ queryKey: ['band', bandId, 'following'] })
      toast.success(data.following ? 'Следвате тази банда' : 'Спряхте да следвате')
    },
  })

  if (isLoading) return <Spinner />
  if (!band) return <div className="text-center py-20 text-zinc-500">Банда не е намерена</div>

  const isFollowing = followData?.following ?? false

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      {/* Header card */}
      <div className="card overflow-hidden mb-6">
        <div className="h-48 bg-gradient-to-r from-rock-950 to-zinc-900 relative">
          {band.avatarUrl && (
            <img src={band.avatarUrl} alt={band.name} className="w-full h-full object-cover opacity-40" />
          )}
          <div className="absolute inset-0 flex items-end p-6">
            <div className="flex items-end gap-4 w-full">
              <Avatar url={band.avatarUrl} name={band.name} />
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <h1 className="text-3xl font-bold text-white">{band.name}</h1>
                  {band.isVerified && (
                    <span className="bg-rock-600 text-white text-xs px-2 py-0.5 rounded-full">
                      Верифицирана
                    </span>
                  )}
                </div>
                <p className="text-rock-400 font-medium">{GENRE_LABELS[band.genre]}</p>
              </div>
              {isAuthenticated && (
                <button
                  onClick={() => followMutation.mutate()}
                  className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                    isFollowing
                      ? 'bg-rock-800 text-rock-300 hover:bg-rock-900'
                      : 'bg-rock-600 text-white hover:bg-rock-700'
                  }`}
                >
                  <Heart size={16} className={isFollowing ? 'fill-current' : ''} />
                  {isFollowing ? 'Следван' : 'Следвай'}
                </button>
              )}
            </div>
          </div>
        </div>

        <div className="p-6">
          <div className="flex flex-wrap gap-4 text-sm text-zinc-400 mb-4">
            {band.city && (
              <span className="flex items-center gap-1"><MapPin size={14} />{band.city}</span>
            )}
            {band.foundedYear && (
              <span className="flex items-center gap-1"><Calendar size={14} />Основана {band.foundedYear}</span>
            )}
            <span className="flex items-center gap-1"><Users size={14} />{band.followersCount} последователи</span>
            <span className="flex items-center gap-1">
              <StarRating rating={Math.round(band.avgRating)} size={14} />
              <span className="ml-1">{band.avgRating.toFixed(1)} ({band.totalRatings})</span>
            </span>
          </div>

          {band.description && (
            <p className="text-zinc-300 leading-relaxed mb-4">{band.description}</p>
          )}
          {band.members && (
            <p className="text-zinc-400 text-sm">
              <span className="text-zinc-500">Членове: </span>{band.members}
            </p>
          )}

          <div className="flex gap-3 mt-4">
            {band.spotifyUrl && (
              <SocialLink label="Spotify" href={band.spotifyUrl} color="text-green-400 hover:text-green-300" />
            )}
            {band.youtubeUrl && (
              <SocialLink label="YouTube" href={band.youtubeUrl} color="text-red-400 hover:text-red-300" />
            )}
            {band.facebookUrl && (
              <SocialLink label="Facebook" href={band.facebookUrl} color="text-blue-400 hover:text-blue-300" />
            )}
          </div>
        </div>
      </div>

      <ReviewsList targetType="BAND" targetId={bandId} />
    </div>
  )
}

function Avatar({ url, name }: { url?: string; name: string }) {
  return (
    <div className="w-20 h-20 rounded-xl bg-zinc-800 border-2 border-zinc-700 overflow-hidden flex-shrink-0 flex items-center justify-center">
      {url
        ? <img src={url} alt={name} className="w-full h-full object-cover" />
        : <Music size={32} className="text-zinc-600" />
      }
    </div>
  )
}

function SocialLink({ href, label, color }: { href: string; label: string; color: string }) {
  return (
    <a
      href={href}
      target="_blank"
      rel="noopener noreferrer"
      className={`flex items-center gap-1 text-sm ${color}`}
    >
      <ExternalLinkIcon size={14} /> {label}
    </a>
  )
}
