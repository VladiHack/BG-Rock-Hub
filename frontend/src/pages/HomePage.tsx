import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { Guitar, Music, Calendar, MapPin, ChevronRight } from 'lucide-react'
import { bandsApi } from '@/api/bands'
import { eventsApi } from '@/api/events'
import { ROUTES, PAGE_SIZE } from '@/constants'
import BandCard from '@/components/BandCard'
import EventCard from '@/components/EventCard'

const HERO_STATS = [
  { icon: <Music size={20} />,    label: 'Банди',    desc: 'Рок изпълнители' },
  { icon: <Calendar size={20} />, label: 'Концерти', desc: 'Предстоящи събития' },
  { icon: <MapPin size={20} />,   label: 'Клубове',  desc: 'Рок заведения' },
] as const

export default function HomePage() {
  const { data: topBands } = useQuery({
    queryKey: ['bands', 'top', PAGE_SIZE.TOP_BANDS],
    queryFn: () => bandsApi.getTopRated(PAGE_SIZE.TOP_BANDS),
  })

  const { data: unknownBands } = useQuery({
    queryKey: ['bands', 'unknown', 3],
    queryFn: () => bandsApi.getUnknown(3),
  })

  const { data: upcomingEvents } = useQuery({
    queryKey: ['events', 'upcoming', PAGE_SIZE.TOP_BANDS],
    queryFn: () => eventsApi.getUpcoming({ size: PAGE_SIZE.TOP_BANDS }),
  })

  return (
    <div>
      {/* Hero */}
      <section className="relative overflow-hidden bg-gradient-to-b from-rock-950 via-zinc-950 to-zinc-950 py-24 px-4">
        <div className="relative max-w-4xl mx-auto text-center">
          <Guitar size={64} className="text-rock-500 mx-auto mb-6" />
          <h1 className="font-rock text-4xl sm:text-6xl text-white mb-4 leading-tight">
            BG Rock Hub
          </h1>
          <p className="text-xl text-zinc-300 mb-2">Единният рок център на България</p>
          <p className="text-zinc-500 mb-8 max-w-2xl mx-auto">
            Открий банди, следи концерти, оценявай клубове и се свържи с рок общността.
          </p>
          <div className="flex flex-wrap justify-center gap-3">
            <Link to={ROUTES.BANDS} className="btn-primary text-base py-3 px-6">
              Открий банди
            </Link>
            <Link to={ROUTES.EVENTS} className="btn-secondary text-base py-3 px-6">
              Предстоящи събития
            </Link>
          </div>
        </div>
      </section>

      {/* Stats bar */}
      <section className="border-y border-zinc-800 bg-zinc-900/50">
        <div className="max-w-7xl mx-auto px-4 py-8 grid grid-cols-3 divide-x divide-zinc-800 text-center">
          {HERO_STATS.map((s) => (
            <div key={s.label} className="py-4">
              <div className="flex justify-center text-rock-400 mb-1">{s.icon}</div>
              <p className="font-semibold text-zinc-100">{s.label}</p>
              <p className="text-xs text-zinc-500">{s.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Top Bands */}
      {topBands && topBands.length > 0 && (
        <section className="max-w-7xl mx-auto px-4 py-12">
          <SectionHeader title="Топ банди" linkTo={ROUTES.BANDS} />
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
            {topBands.map((band) => <BandCard key={band.id} band={band} />)}
          </div>
        </section>
      )}

      {/* Unknown Bands Spotlight */}
      {unknownBands && unknownBands.length > 0 && (
        <section className="bg-zinc-900/50 py-12">
          <div className="max-w-7xl mx-auto px-4">
            <SectionHeader
              title="Непознати банди"
              subtitle="Открий следващата голяма банда"
              linkTo={ROUTES.UNKNOWN_BANDS}
            />
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {unknownBands.map((band) => <BandCard key={band.id} band={band} />)}
            </div>
          </div>
        </section>
      )}

      {/* Upcoming Events */}
      {upcomingEvents && upcomingEvents.content.length > 0 && (
        <section className="max-w-7xl mx-auto px-4 py-12">
          <SectionHeader title="Предстоящи концерти" linkTo={ROUTES.EVENTS} />
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {upcomingEvents.content.map((event) => <EventCard key={event.id} event={event} />)}
          </div>
        </section>
      )}
    </div>
  )
}

interface SectionHeaderProps {
  title: string
  subtitle?: string
  linkTo: string
}

function SectionHeader({ title, subtitle, linkTo }: SectionHeaderProps) {
  return (
    <div className="flex items-center justify-between mb-6">
      <div>
        <h2 className="text-2xl font-bold text-zinc-100">{title}</h2>
        {subtitle && <p className="text-zinc-500 text-sm mt-1">{subtitle}</p>}
      </div>
      <Link to={linkTo} className="text-rock-400 hover:text-rock-300 text-sm flex items-center gap-1">
        Виж всички <ChevronRight size={16} />
      </Link>
    </div>
  )
}
