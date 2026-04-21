import { http, HttpResponse, delay } from 'msw'
import type { PageResponse } from '@/types'
import { MOCK_BANDS } from './data/bands'
import { MOCK_VENUES } from './data/venues'
import { MOCK_EVENTS } from './data/events'
import { MOCK_REVIEWS } from './data/reviews'

// ─── mock users for admin panel ─────────────────────────────────────────────
const MOCK_USERS = [
  { id: 1,  email: 'admin@bgrockHub.bg',    username: 'admin',          role: 'ADMIN',  city: 'София',   isVerified: true,  isActive: true,  createdAt: '2024-01-01T00:00:00Z' },
  { id: 2,  email: 'kontra@bgrockHub.bg',   username: 'kontra_official',role: 'BAND',   city: 'София',   isVerified: true,  isActive: true,  createdAt: '2024-01-10T10:00:00Z' },
  { id: 3,  email: 'signal@bgrockHub.bg',   username: 'signal_bg',      role: 'BAND',   city: 'Пловдив', isVerified: true,  isActive: true,  createdAt: '2024-01-12T10:00:00Z' },
  { id: 4,  email: 'mixtape@bgrockHub.bg',  username: 'mixtape5_venue', role: 'VENUE',  city: 'София',   isVerified: true,  isActive: true,  createdAt: '2024-01-15T10:00:00Z' },
  { id: 99, email: 'demo@bgrockHub.bg',     username: 'demo_user',      role: 'FAN',    city: 'София',   isVerified: true,  isActive: true,  createdAt: '2024-02-01T10:00:00Z' },
  { id: 5,  email: 'rockfan@example.com',   username: 'rockfan_varna',  role: 'FAN',    city: 'Варна',   isVerified: false, isActive: true,  createdAt: '2024-03-05T10:00:00Z' },
  { id: 6,  email: 'banned@example.com',    username: 'spammer123',     role: 'FAN',    city: null,      isVerified: false, isActive: false, createdAt: '2024-04-01T10:00:00Z' },
]

// ─── helpers ────────────────────────────────────────────────────────────────

const LAG_MS = 300 // simulate realistic network latency

function paginate<T>(items: T[], page = 0, size = 12): PageResponse<T> {
  const start = page * size
  const content = items.slice(start, start + size)
  const totalPages = Math.ceil(items.length / size)
  return {
    content,
    page,
    size,
    totalElements: items.length,
    totalPages,
    last: page >= totalPages - 1,
  }
}

// ─── handlers ───────────────────────────────────────────────────────────────

export const handlers = [

  // ── Auth ────────────────────────────────────────────────────────────────

  http.post('/api/auth/register', async ({ request }) => {
    await delay(LAG_MS)
    const body = await request.json() as any
    return HttpResponse.json({
      accessToken: 'mock-access-token',
      refreshToken: 'mock-refresh-token',
      userId: 99,
      username: body.username,
      email: body.email,
      role: body.role ?? 'FAN',
    })
  }),

  http.post('/api/auth/login', async ({ request }) => {
    await delay(LAG_MS)
    const body = await request.json() as any
    // admin@bgrockHub.bg → влиза като ADMIN за демо на admin панела
    const isAdmin = body.email === 'admin@bgrockHub.bg'
    return HttpResponse.json({
      accessToken: 'mock-access-token',
      refreshToken: 'mock-refresh-token',
      userId: isAdmin ? 1 : 99,
      username: isAdmin ? 'admin' : 'demo_user',
      email: isAdmin ? 'admin@bgrockHub.bg' : 'demo@bgrockHub.bg',
      role: isAdmin ? 'ADMIN' : 'FAN',
    })
  }),

  // ── Users ───────────────────────────────────────────────────────────────

  http.get('/api/users/me', async () => {
    await delay(LAG_MS)
    return HttpResponse.json({
      id: 99,
      email: 'demo@bgrockHub.bg',
      username: 'demo_user',
      role: 'FAN',
      bio: 'Рок фен от Sofia. Обичам метъл и алтернатива.',
      city: 'София',
      isVerified: true,
      createdAt: '2024-01-01T10:00:00Z',
    })
  }),

  // ── Bands ───────────────────────────────────────────────────────────────

  http.get('/api/bands', async ({ request }) => {
    await delay(LAG_MS)
    const url = new URL(request.url)
    const page   = Number(url.searchParams.get('page') ?? 0)
    const size   = Number(url.searchParams.get('size') ?? 12)
    const genre  = url.searchParams.get('genre')
    const city   = url.searchParams.get('city')
    const search = url.searchParams.get('search')?.toLowerCase()

    let bands = [...MOCK_BANDS]
    if (genre)  bands = bands.filter(b => b.genre === genre)
    if (city)   bands = bands.filter(b => b.city?.toLowerCase().includes(city.toLowerCase()))
    if (search) bands = bands.filter(b =>
      b.name.toLowerCase().includes(search) ||
      b.description?.toLowerCase().includes(search),
    )

    return HttpResponse.json(paginate(bands, page, size))
  }),

  http.get('/api/bands/top', async ({ request }) => {
    await delay(LAG_MS)
    const url = new URL(request.url)
    const limit = Number(url.searchParams.get('limit') ?? 6)
    const sorted = [...MOCK_BANDS].sort((a, b) => b.avgRating - a.avgRating)
    return HttpResponse.json(sorted.slice(0, limit))
  }),

  http.get('/api/bands/unknown', async ({ request }) => {
    await delay(LAG_MS)
    const url = new URL(request.url)
    const limit = Number(url.searchParams.get('limit') ?? 10)
    const unknown = MOCK_BANDS.filter(b => b.totalRatings < 20)
    return HttpResponse.json(unknown.slice(0, limit))
  }),

  http.get('/api/bands/:id', async ({ params }) => {
    await delay(LAG_MS)
    const band = MOCK_BANDS.find(b => b.id === Number(params.id))
    if (!band) return new HttpResponse(null, { status: 404 })
    return HttpResponse.json(band)
  }),

  http.get('/api/bands/:id/following', async () => {
    await delay(LAG_MS)
    return HttpResponse.json({ following: false })
  }),

  http.post('/api/bands/:id/follow', async () => {
    await delay(LAG_MS)
    return HttpResponse.json({ following: true, followersCount: 42 })
  }),

  // ── Events ──────────────────────────────────────────────────────────────

  http.get('/api/events/upcoming', async ({ request }) => {
    await delay(LAG_MS)
    const url = new URL(request.url)
    const page = Number(url.searchParams.get('page') ?? 0)
    const size = Number(url.searchParams.get('size') ?? 6)
    const upcoming = MOCK_EVENTS.filter(e => e.status === 'UPCOMING')
      .sort((a, b) => new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime())
    return HttpResponse.json(paginate(upcoming, page, size))
  }),

  http.get('/api/events', async ({ request }) => {
    await delay(LAG_MS)
    const url    = new URL(request.url)
    const page   = Number(url.searchParams.get('page') ?? 0)
    const size   = Number(url.searchParams.get('size') ?? 12)
    const status = url.searchParams.get('status')
    const city   = url.searchParams.get('city')
    const search = url.searchParams.get('search')?.toLowerCase()

    let events = [...MOCK_EVENTS]
    if (status) events = events.filter(e => e.status === status)
    if (city)   events = events.filter(e => e.city.toLowerCase().includes(city.toLowerCase()))
    if (search) events = events.filter(e => e.title.toLowerCase().includes(search))

    events.sort((a, b) => new Date(a.eventDate).getTime() - new Date(b.eventDate).getTime())
    return HttpResponse.json(paginate(events, page, size))
  }),

  http.get('/api/events/:id', async ({ params }) => {
    await delay(LAG_MS)
    const event = MOCK_EVENTS.find(e => e.id === Number(params.id))
    if (!event) return new HttpResponse(null, { status: 404 })
    return HttpResponse.json(event)
  }),

  http.post('/api/events/:id/attend', async ({ params }) => {
    await delay(LAG_MS)
    const event = MOCK_EVENTS.find(e => e.id === Number(params.id))
    if (!event) return new HttpResponse(null, { status: 404 })
    return HttpResponse.json({ ...event, interestedCount: event.interestedCount + 1 })
  }),

  // ── Venues ──────────────────────────────────────────────────────────────

  http.get('/api/venues', async ({ request }) => {
    await delay(LAG_MS)
    const url    = new URL(request.url)
    const page   = Number(url.searchParams.get('page') ?? 0)
    const size   = Number(url.searchParams.get('size') ?? 12)
    const city   = url.searchParams.get('city')
    const search = url.searchParams.get('search')?.toLowerCase()

    let venues = [...MOCK_VENUES]
    if (city)   venues = venues.filter(v => v.city.toLowerCase().includes(city.toLowerCase()))
    if (search) venues = venues.filter(v =>
      v.name.toLowerCase().includes(search) ||
      v.city.toLowerCase().includes(search),
    )

    return HttpResponse.json(paginate(venues, page, size))
  }),

  http.get('/api/venues/:id', async ({ params }) => {
    await delay(LAG_MS)
    const venue = MOCK_VENUES.find(v => v.id === Number(params.id))
    if (!venue) return new HttpResponse(null, { status: 404 })
    return HttpResponse.json(venue)
  }),

  // ── Reviews ─────────────────────────────────────────────────────────────

  http.get('/api/reviews', async ({ request }) => {
    await delay(LAG_MS)
    const url        = new URL(request.url)
    const targetType = url.searchParams.get('targetType')
    const targetId   = Number(url.searchParams.get('targetId'))
    const page       = Number(url.searchParams.get('page') ?? 0)
    const size       = Number(url.searchParams.get('size') ?? 10)

    const filtered = MOCK_REVIEWS.filter(
      r => r.targetType === targetType && r.targetId === targetId,
    )
    return HttpResponse.json(paginate(filtered, page, size))
  }),

  // ── Admin ───────────────────────────────────────────────────────────────

  http.get('/api/admin/stats', async () => {
    await delay(LAG_MS)
    return HttpResponse.json({
      totalUsers: MOCK_USERS.length,
      totalBands: MOCK_BANDS.length,
      totalVenues: MOCK_VENUES.length,
      totalEvents: MOCK_EVENTS.length,
      totalReviews: MOCK_REVIEWS.length,
      pendingReviews: MOCK_REVIEWS.filter(r => !r.isApproved).length,
    })
  }),

  http.get('/api/admin/users', async () => {
    await delay(LAG_MS)
    return HttpResponse.json(paginate(MOCK_USERS, 0, 50))
  }),

  http.put('/api/admin/users/:id/role', async ({ params, request }) => {
    await delay(LAG_MS)
    const url = new URL(request.url)
    const role = url.searchParams.get('role')
    const user = MOCK_USERS.find(u => u.id === Number(params.id))
    if (!user) return new HttpResponse(null, { status: 404 })
    user.role = role ?? user.role
    return HttpResponse.json(user)
  }),

  http.put('/api/admin/users/:id/active', async ({ params }) => {
    await delay(LAG_MS)
    const user = MOCK_USERS.find(u => u.id === Number(params.id))
    if (!user) return new HttpResponse(null, { status: 404 })
    user.isActive = !user.isActive
    return HttpResponse.json(user)
  }),

  http.get('/api/admin/bands', async () => {
    await delay(LAG_MS)
    return HttpResponse.json(paginate(MOCK_BANDS, 0, 50))
  }),

  http.put('/api/admin/bands/:id/verify', async ({ params }) => {
    await delay(LAG_MS)
    const band = MOCK_BANDS.find(b => b.id === Number(params.id))
    if (!band) return new HttpResponse(null, { status: 404 })
    band.isVerified = !band.isVerified
    return HttpResponse.json(band)
  }),

  http.delete('/api/admin/bands/:id', async ({ params }) => {
    await delay(LAG_MS)
    const idx = MOCK_BANDS.findIndex(b => b.id === Number(params.id))
    if (idx !== -1) MOCK_BANDS.splice(idx, 1)
    return new HttpResponse(null, { status: 204 })
  }),

  http.get('/api/admin/venues', async () => {
    await delay(LAG_MS)
    return HttpResponse.json(paginate(MOCK_VENUES, 0, 50))
  }),

  http.put('/api/admin/venues/:id/verify', async ({ params }) => {
    await delay(LAG_MS)
    const venue = MOCK_VENUES.find(v => v.id === Number(params.id))
    if (!venue) return new HttpResponse(null, { status: 404 })
    venue.isVerified = !venue.isVerified
    return HttpResponse.json(venue)
  }),

  http.get('/api/admin/reviews', async () => {
    await delay(LAG_MS)
    return HttpResponse.json(paginate(MOCK_REVIEWS, 0, 50))
  }),

  http.put('/api/admin/reviews/:id/approve', async ({ params }) => {
    await delay(LAG_MS)
    const review = MOCK_REVIEWS.find(r => r.id === Number(params.id))
    if (!review) return new HttpResponse(null, { status: 404 })
    review.isApproved = !review.isApproved
    return HttpResponse.json(review)
  }),

  http.delete('/api/admin/reviews/:id', async ({ params }) => {
    await delay(LAG_MS)
    const idx = MOCK_REVIEWS.findIndex(r => r.id === Number(params.id))
    if (idx !== -1) MOCK_REVIEWS.splice(idx, 1)
    return new HttpResponse(null, { status: 204 })
  }),

  http.post('/api/reviews', async ({ request }) => {
    await delay(LAG_MS)
    const body = await request.json() as any
    const newReview = {
      id: Date.now(),
      rating: body.rating,
      content: body.content,
      targetType: body.targetType,
      targetId: body.targetId,
      reviewerId: 99,
      reviewerUsername: 'demo_user',
      isApproved: true,
      createdAt: new Date().toISOString(),
    }
    MOCK_REVIEWS.push(newReview)
    return HttpResponse.json(newReview, { status: 201 })
  }),
]
