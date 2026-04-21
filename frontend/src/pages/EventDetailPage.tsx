import { useParams } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Calendar, MapPin, Ticket, Users, Music } from 'lucide-react'
import clsx from 'clsx'
import toast from 'react-hot-toast'
import { eventsApi } from '@/api/events'
import { EVENT_STATUS_LABELS, EVENT_STATUS_COLORS, GENRE_LABELS } from '@/constants'
import { useAuthStore } from '@/store/authStore'
import { formatDate, formatPrice } from '@/utils/format'
import ReviewsList from '@/components/ReviewsList'
import BandCard from '@/components/BandCard'
import Spinner from '@/components/Spinner'

export default function EventDetailPage() {
  const { id } = useParams<{ id: string }>()
  const eventId = Number(id)
  const { isAuthenticated } = useAuthStore()
  const queryClient = useQueryClient()

  const { data: event, isLoading } = useQuery({
    queryKey: ['event', eventId],
    queryFn: () => eventsApi.getById(eventId),
  })

  const attendMutation = useMutation({
    mutationFn: () => eventsApi.toggleAttend(eventId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['event', eventId] }),
    onError: () => toast.error('Грешка'),
  })

  if (isLoading) return <Spinner />
  if (!event) return <div className="text-center py-20 text-zinc-500">Събитие не е намерено</div>

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <div className="card overflow-hidden mb-6">
        {event.coverImgUrl && (
          <img src={event.coverImgUrl} alt={event.title} className="w-full h-64 object-cover" />
        )}
        <div className="p-6">
          <div className="flex items-start justify-between gap-4 flex-wrap">
            <div>
              <span className={clsx('text-xs px-2 py-0.5 rounded-full', EVENT_STATUS_COLORS[event.status])}>
                {EVENT_STATUS_LABELS[event.status]}
              </span>
              <h1 className="text-3xl font-bold text-zinc-100 mt-2">{event.title}</h1>
            </div>
            {isAuthenticated && event.status === 'UPCOMING' && (
              <button
                onClick={() => attendMutation.mutate()}
                className="btn-primary flex items-center gap-2"
              >
                <Users size={16} /> Ще присъствам
              </button>
            )}
          </div>

          <div className="grid sm:grid-cols-2 gap-3 mt-4 text-sm text-zinc-400">
            <div className="flex items-center gap-2">
              <Calendar size={16} className="text-rock-400" />
              {formatDate(event.eventDate)}
            </div>
            <div className="flex items-center gap-2">
              <MapPin size={16} className="text-rock-400" />
              {event.city}{event.venue ? ` · ${event.venue.name}` : ''}
            </div>
            {event.ticketPrice != null && (
              <div className="flex items-center gap-2">
                <Ticket size={16} className="text-rock-400" />
                {formatPrice(event.ticketPrice)}
              </div>
            )}
            {event.genre && (
              <div className="flex items-center gap-2">
                <Music size={16} className="text-rock-400" />
                {GENRE_LABELS[event.genre]}
              </div>
            )}
            <div className="flex items-center gap-2">
              <Users size={16} />
              {event.interestedCount} интересуват се
            </div>
          </div>

          {event.description && (
            <p className="text-zinc-300 mt-4 leading-relaxed">{event.description}</p>
          )}
        </div>
      </div>

      {event.bands.length > 0 && (
        <div className="mb-6">
          <h2 className="text-xl font-semibold text-zinc-100 mb-4">Участващи банди</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {event.bands.map((band) => <BandCard key={band.id} band={band} />)}
          </div>
        </div>
      )}

      <ReviewsList targetType="EVENT" targetId={eventId} />
    </div>
  )
}
