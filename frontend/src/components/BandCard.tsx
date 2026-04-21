import { Link } from 'react-router-dom'
import { Users, MapPin, Music } from 'lucide-react'
import type { Band } from '@/types'
import { GENRE_LABELS, ROUTES } from '@/constants'
import StarRating from './StarRating'

interface Props {
  band: Band
}

export default function BandCard({ band }: Props) {
  return (
    <Link to={ROUTES.BAND(band.id)} className="card group block">
      <div className="relative h-40 bg-zinc-800 overflow-hidden">
        {band.avatarUrl ? (
          <img
            src={band.avatarUrl}
            alt={band.name}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            loading="lazy"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <Music size={40} className="text-zinc-600" />
          </div>
        )}
        {band.isVerified && (
          <span className="absolute top-2 right-2 bg-rock-600 text-white text-xs px-2 py-0.5 rounded-full">
            Верифицирана
          </span>
        )}
      </div>

      <div className="p-4">
        <h3 className="font-semibold text-zinc-100 truncate">{band.name}</h3>
        <p className="text-rock-400 text-sm mt-0.5">{GENRE_LABELS[band.genre]}</p>

        <div className="flex items-center justify-between mt-2">
          <StarRating rating={Math.round(band.avgRating)} size={14} />
          <span className="text-zinc-500 text-xs">({band.totalRatings})</span>
        </div>

        <div className="flex items-center gap-3 mt-2 text-xs text-zinc-500">
          {band.city && (
            <span className="flex items-center gap-1">
              <MapPin size={12} />
              {band.city}
            </span>
          )}
          <span className="flex items-center gap-1">
            <Users size={12} />
            {band.followersCount}
          </span>
        </div>
      </div>
    </Link>
  )
}
