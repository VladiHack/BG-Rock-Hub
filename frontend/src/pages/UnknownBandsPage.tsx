import { useQuery } from '@tanstack/react-query'
import { Sparkles } from 'lucide-react'
import { bandsApi } from '@/api/bands'
import { PAGE_SIZE } from '@/constants'
import BandCard from '@/components/BandCard'
import Spinner from '@/components/Spinner'

export default function UnknownBandsPage() {
  const { data: bands, isLoading } = useQuery({
    queryKey: ['bands', 'unknown', PAGE_SIZE.SPOTLIGHT],
    queryFn: () => bandsApi.getUnknown(PAGE_SIZE.SPOTLIGHT),
  })

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="flex items-center gap-3 mb-2">
        <Sparkles size={28} className="text-rock-400" />
        <h1 className="text-3xl font-bold text-zinc-100">Непознати банди</h1>
      </div>
      <p className="text-zinc-500 mb-8">
        Открий следващата голяма банда — преди всички останали.
      </p>

      {isLoading ? (
        <Spinner />
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          {bands?.map((band) => <BandCard key={band.id} band={band} />)}
        </div>
      )}
    </div>
  )
}
