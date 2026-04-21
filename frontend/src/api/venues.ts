import { api } from './axios'
import type { Venue, PageResponse } from '@/types'

interface VenueFilters {
  page?: number
  size?: number
  city?: string
  search?: string
}

export const venuesApi = {
  getAll: (params?: VenueFilters) =>
    api.get<PageResponse<Venue>>('/venues', { params }).then((r) => r.data),

  getById: (id: number) =>
    api.get<Venue>(`/venues/${id}`).then((r) => r.data),

  create: (data: Partial<Venue>) =>
    api.post<Venue>('/venues', data).then((r) => r.data),

  update: (id: number, data: Partial<Venue>) =>
    api.put<Venue>(`/venues/${id}`, data).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/venues/${id}`),
}
