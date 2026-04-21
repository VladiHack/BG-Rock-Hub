import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Search } from 'lucide-react'
import { venuesApi } from '@/api/venues'
import { PAGE_SIZE } from '@/constants'
import VenueCard from '@/components/VenueCard'
import Pagination from '@/components/Pagination'

export default function VenuesPage() {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [city, setCity] = useState('')

  const { data, isLoading } = useQuery({
    queryKey: ['venues', page, search, city],
    queryFn: () =>
      venuesApi.getAll({
        page,
        size: PAGE_SIZE.CARDS,
        search: search || undefined,
        city: city || undefined,
      }),
  })

  const handleSearch = (v: string) => { setSearch(v); setPage(0) }
  const handleCity   = (v: string) => { setCity(v); setPage(0) }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-zinc-100 mb-6">Клубове и зали</h1>

      <div className="flex flex-wrap gap-3 mb-6">
        <div className="relative flex-1 min-w-48">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-zinc-500" />
          <input
            className="input pl-9"
            placeholder="Търси клубове..."
            value={search}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
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
        <div className="text-center py-20 text-zinc-500">Няма намерени клубове</div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {data?.content.map((venue) => <VenueCard key={venue.id} venue={venue} />)}
          </div>
          {data && (
            <Pagination page={page} totalPages={data.totalPages} isLast={data.last} onChange={setPage} />
          )}
        </>
      )}
    </div>
  )
}
