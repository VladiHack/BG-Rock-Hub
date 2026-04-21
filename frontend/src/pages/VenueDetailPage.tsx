import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { MapPin, Phone, Globe, Users, Building2 } from 'lucide-react'
import { venuesApi } from '@/api/venues'
import ReviewsList from '@/components/ReviewsList'
import StarRating from '@/components/StarRating'
import Spinner from '@/components/Spinner'

export default function VenueDetailPage() {
  const { id } = useParams<{ id: string }>()
  const venueId = Number(id)

  const { data: venue, isLoading } = useQuery({
    queryKey: ['venue', venueId],
    queryFn: () => venuesApi.getById(venueId),
  })

  if (isLoading) return <Spinner />
  if (!venue) return <div className="text-center py-20 text-zinc-500">Клубът не е намерен</div>

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <div className="card overflow-hidden mb-6">
        {venue.coverImgUrl ? (
          <img src={venue.coverImgUrl} alt={venue.name} className="w-full h-56 object-cover" />
        ) : (
          <div className="w-full h-56 bg-zinc-800 flex items-center justify-center">
            <Building2 size={48} className="text-zinc-600" />
          </div>
        )}

        <div className="p-6">
          <div className="flex items-start justify-between gap-4">
            <div>
              <h1 className="text-3xl font-bold text-zinc-100">{venue.name}</h1>
              {venue.isVerified && (
                <span className="bg-blue-700 text-white text-xs px-2 py-0.5 rounded-full mt-1 inline-block">
                  Верифициран
                </span>
              )}
            </div>
            <div className="text-right">
              <StarRating rating={Math.round(venue.avgRating)} size={20} />
              <p className="text-zinc-500 text-xs mt-1">
                {venue.avgRating.toFixed(1)} · {venue.totalRatings} ревюта
              </p>
            </div>
          </div>

          <div className="grid sm:grid-cols-2 gap-3 mt-4 text-sm text-zinc-400">
            <div className="flex items-center gap-2">
              <MapPin size={16} className="text-rock-400" />
              {venue.address}, {venue.city}
            </div>
            {venue.capacity && (
              <div className="flex items-center gap-2">
                <Users size={16} className="text-rock-400" />
                Капацитет: {venue.capacity}
              </div>
            )}
            {venue.phone && (
              <div className="flex items-center gap-2">
                <Phone size={16} className="text-rock-400" />
                {venue.phone}
              </div>
            )}
            {venue.website && (
              <a
                href={venue.website}
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center gap-2 text-rock-400 hover:text-rock-300"
              >
                <Globe size={16} /> Уебсайт
              </a>
            )}
          </div>

          {venue.description && (
            <p className="text-zinc-300 mt-4 leading-relaxed">{venue.description}</p>
          )}
        </div>
      </div>

      <ReviewsList targetType="VENUE" targetId={venueId} />
    </div>
  )
}
