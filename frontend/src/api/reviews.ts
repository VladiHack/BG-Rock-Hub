import { api } from './axios'
import type { Review, PageResponse, ReviewTargetType } from '@/types'

interface CreateReviewPayload {
  rating: number
  content?: string
  targetType: ReviewTargetType
  targetId: number
}

export const reviewsApi = {
  getForTarget: (targetType: ReviewTargetType, targetId: number, page = 0, size = 10) =>
    api
      .get<PageResponse<Review>>('/reviews', { params: { targetType, targetId, page, size } })
      .then((r) => r.data),

  create: (data: CreateReviewPayload) =>
    api.post<Review>('/reviews', data).then((r) => r.data),

  update: (id: number, data: CreateReviewPayload) =>
    api.put<Review>(`/reviews/${id}`, data).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/reviews/${id}`),
}
