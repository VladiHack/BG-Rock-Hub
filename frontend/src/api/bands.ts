import { api } from './axios'
import type { Band, PageResponse, Genre } from '@/types'

interface BandFilters {
  page?: number
  size?: number
  genre?: Genre
  city?: string
  search?: string
}

interface FollowResponse {
  following: boolean
  followersCount: number
}

export const bandsApi = {
  getAll: (params?: BandFilters) =>
    api.get<PageResponse<Band>>('/bands', { params }).then((r) => r.data),

  getById: (id: number) =>
    api.get<Band>(`/bands/${id}`).then((r) => r.data),

  getTopRated: (limit: number) =>
    api.get<Band[]>('/bands/top', { params: { limit } }).then((r) => r.data),

  getUnknown: (limit: number) =>
    api.get<Band[]>('/bands/unknown', { params: { limit } }).then((r) => r.data),

  create: (data: Partial<Band>) =>
    api.post<Band>('/bands', data).then((r) => r.data),

  update: (id: number, data: Partial<Band>) =>
    api.put<Band>(`/bands/${id}`, data).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/bands/${id}`),

  toggleFollow: (id: number) =>
    api.post<FollowResponse>(`/bands/${id}/follow`).then((r) => r.data),

  isFollowing: (id: number) =>
    api.get<{ following: boolean }>(`/bands/${id}/following`).then((r) => r.data),
}
