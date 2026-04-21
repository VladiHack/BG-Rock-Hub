import { api } from './axios'
import type { Event, PageResponse, EventStatus } from '@/types'

interface EventFilters {
  page?: number
  size?: number
  status?: EventStatus
  city?: string
  search?: string
}

export const eventsApi = {
  getAll: (params?: EventFilters) =>
    api.get<PageResponse<Event>>('/events', { params }).then((r) => r.data),

  getUpcoming: (params?: { page?: number; size?: number }) =>
    api.get<PageResponse<Event>>('/events/upcoming', { params }).then((r) => r.data),

  getById: (id: number) =>
    api.get<Event>(`/events/${id}`).then((r) => r.data),

  create: (data: Partial<Event> & { bandIds?: number[]; venueId?: number }) =>
    api.post<Event>('/events', data).then((r) => r.data),

  update: (id: number, data: Partial<Event>) =>
    api.put<Event>(`/events/${id}`, data).then((r) => r.data),

  toggleAttend: (id: number) =>
    api.post<Event>(`/events/${id}/attend`).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/events/${id}`),
}
