import { Link } from 'react-router-dom'
import { Calendar, MapPin, Ticket, Users } from 'lucide-react'
import clsx from 'clsx'
import type { Event } from '@/types'
import { EVENT_STATUS_LABELS, EVENT_STATUS_COLORS, ROUTES } from '@/constants'
import { formatDate, formatPrice } from '@/utils/format'

interface Props {
  event: Event
}

export default function EventCard({ event }: Props) {
  return (
    <Link to={ROUTES.EVENT(event.id)} className="card group block">
      <div className="relative h-40 bg-zinc-800 overflow-hidden">
        {event.coverImgUrl ? (
          <img
            src={event.coverImgUrl}
            alt={event.title}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
            loading="lazy"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-rock-900 to-zinc-900">
            <Calendar size={40} className="text-zinc-600" />
          </div>
        )}
        <span className={clsx('absolute top-2 left-2 text-xs px-2 py-0.5 rounded-full', EVENT_STATUS_COLORS[event.status])}>
          {EVENT_STATUS_LABELS[event.status]}
        </span>
      </div>

      <div className="p-4">
        <h3 className="font-semibold text-zinc-100 truncate">{event.title}</h3>

        <div className="mt-2 space-y-1 text-xs text-zinc-500">
          <div className="flex items-center gap-1">
            <Calendar size={12} />
            {formatDate(event.eventDate)}
          </div>
          <div className="flex items-center gap-1">
            <MapPin size={12} />
            {event.city}{event.venue ? ` · ${event.venue.name}` : ''}
          </div>
          <div className="flex items-center justify-between">
            <span className="flex items-center gap-1">
              <Users size={12} />
              {event.interestedCount} интересуват се
            </span>
            {event.ticketPrice != null && (
              <span className="flex items-center gap-1 text-rock-400">
                <Ticket size={12} />
                {formatPrice(event.ticketPrice)}
              </span>
            )}
          </div>
        </div>
      </div>
    </Link>
  )
}
