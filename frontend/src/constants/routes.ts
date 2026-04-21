export const ROUTES = {
  HOME:           '/',
  LOGIN:          '/login',
  REGISTER:       '/register',
  PROFILE:        '/profile',

  BANDS:          '/bands',
  BAND:           (id: number | string) => `/bands/${id}`,
  UNKNOWN_BANDS:  '/bands/unknown',

  EVENTS:         '/events',
  EVENT:          (id: number | string) => `/events/${id}`,

  VENUES:         '/venues',
  VENUE:          (id: number | string) => `/venues/${id}`,

  ADMIN:          '/admin',
} as const
