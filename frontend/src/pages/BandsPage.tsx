import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Search } from 'lucide-react'
import { bandsApi } from '@/api/bands'
import { GENRE_OPTIONS, PAGE_SIZE } from '@/constants'
import type { Genre } from '@/types'
import BandCard from '@/components/BandCard'
import Pagination from '@/components/Pagination'

export default function BandsPage() {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [genre, setGenre] = useState<Genre | ''>('')
  const [city, setCity] = useState('')

  const { data, isLoading } = useQuery({
    queryKey: ['bands', page, search, genre, city],
    queryFn: () =>
      bandsApi.getAll({
        page,
        size: PAGE_SIZE.CARDS,
        search: search || undefined,
        genre: (genre as Genre) || undefined,
        city: city || undefined,
      }),
  })

  const handleSearch = (value: string) => { setSearch(value); setPage(0) }
  const handleGenre  = (value: string) => { setGenre(value as Genre | ''); setPage(0) }
  const handleCity   = (value: string) => { setCity(value); setPage(0) }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-zinc-100 mb-6">Банди</h1>

      <div className="flex flex-wrap gap-3 mb-6">
        <div className="relative flex-1 min-w-48">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-zinc-500" />
          <input
            className="input pl-9"
            placeholder="Търси банди..."
            value={search}
            onChange={(e) => handleSearch(e.target.value)}
          />
        </div>
        <select className="input w-48" value={genre} onChange={(e) => handleGenre(e.target.value)}>
          <option value="">Всички жанрове</option>
          {GENRE_OPTIONS.map(({ value, label }) => (
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
        <SkeletonGrid count={PAGE_SIZE.CARDS} />
      ) : data?.content.length === 0 ? (
        <EmptyState message="Няма намерени банди" />
      ) : (
        <>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {data?.content.map((band) => <BandCard key={band.id} band={band} />)}
          </div>
          {data && (
            <Pagination
              page={page}
              totalPages={data.totalPages}
              isLast={data.last}
              onChange={setPage}
            />
          )}
        </>
      )}
    </div>
  )
}

function SkeletonGrid({ count }: { count: number }) {
  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
      {Array.from({ length: count }).map((_, i) => (
        <div key={i} className="card h-56 animate-pulse bg-zinc-800" />
      ))}
    </div>
  )
}

function EmptyState({ message }: { message: string }) {
  return <div className="text-center py-20 text-zinc-500">{message}</div>
}
