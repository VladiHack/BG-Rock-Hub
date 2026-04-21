import { Link } from 'react-router-dom'
import { MapPin, Building2 } from 'lucide-react'
import type { Venue } from '@/types'
import { ROUTES } from '@/constants'
import StarRating from './StarRating'

interface Props {
  venue: Venue
}

export default function VenueCard({ venue }: Props) {
  return (
    <Link to={ROUTES.VENUE(venue.id)} className="card group block">
      <div className="relative h-40 bg-zinc-800 overflow-hidden">
        {venue.coverImgUrl ? (
          <img
            src={venue.coverImgUrl}
            alt={venue.name}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            loading="lazy"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <Building2 size={40} className="text-zinc-600" />
          </div>
        )}
        {venue.isVerified && (
          <span className="absolute top-2 right-2 bg-blue-700 text-white text-xs px-2 py-0.5 rounded-full">
            Верифициран
          </span>
        )}
      </div>

      <div className="p-4">
        <h3 className="font-semibold text-zinc-100 truncate">{venue.name}</h3>
        <div className="flex items-center gap-1 text-xs text-zinc-500 mt-1">
          <MapPin size={12} />
          {venue.city}
        </div>
        <div className="flex items-center justify-between mt-2">
          <StarRating rating={Math.round(venue.avgRating)} size={14} />
          <span className="text-zinc-500 text-xs">({venue.totalRatings})</span>
        </div>
      </div>
    </Link>
  )
}
