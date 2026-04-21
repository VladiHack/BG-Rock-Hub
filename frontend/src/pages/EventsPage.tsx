import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Search } from 'lucide-react'
import { eventsApi } from '@/api/events'
import { EVENT_STATUS_OPTIONS, PAGE_SIZE } from '@/constants'
import type { EventStatus } from '@/types'
import EventCard from '@/components/EventCard'
import Pagination from '@/components/Pagination'

export default function EventsPage() {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [status, setStatus] = useState<EventStatus | ''>('')
  const [city, setCity] = useState('')

  const { data, isLoading } = useQuery({
    queryKey: ['events', page, search, status, city],
    queryFn: () =>
      eventsApi.getAll({
        page,
        size: PAGE_SIZE.CARDS,
        search: search || undefined,
        status: (status as EventStatus) || undefined,
        city: city || undefined,
      }),
  })

  const handleSearch = (v: string) => { setSearch(v); setPage(0) }
  const handleStatus = (v: string) => { setStatus(v as EventStatus | ''); setPage(0) }
  const handleCity   = (v: string) => { setCity(v); setPage(0) }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-zinc-100 mb-6">Събития</h1>

      <div className="flex flex-wrap gap-3 mb-6">
        <div className="relative flex-1 min-w-48">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-zinc-500" />
          <input
            className="input pl-9"
            placeholder="Търси събития..."
            value={search}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
        <select className="input w-44" value={status} onChange={(e) => handleStatus(e.target.value)}>
          <option value="">Всички статуси</option>
          {EVENT_STATUS_OPTIONS.map(({ value, label }) => (
            <option key={value} value={value}>{label}</option>
          ))}
        </select>
        <input
          className="input w-36"
          placeholder="Град"
          value={city}
          onChange={(e) => handleCity(e.target.value)}
        />
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="card h-56 animate-pulse bg-zinc-800" />
          ))}
        </div>
      ) : data?.content.length === 0 ? (
        <div className="text-center py-20 text-zinc-500">Няма намерени събития</div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {data?.content.map((event) => <EventCard key={event.id} event={event} />)}
          </div>
          {data && (
            <Pagination page={page} totalPages={data.totalPages} isLast={data.last} onChange={setPage} />
          )}
        </>
      )}
    </div>
  )
}
