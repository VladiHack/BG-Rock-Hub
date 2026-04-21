import api from './axios'
import type { Band, Venue, PageResponse } from '@/types'

export interface AdminStats {
  totalUsers: number
  totalBands: number
  totalVenues: number
  totalEvents: number
  totalReviews: number
  pendingReviews: number
}

export interface AdminUser {
  id: number
  email: string
  username: string
  role: string
  city: string | null
  isVerified: boolean
  isActive: boolean
  createdAt: string
}

export interface AdminReview {
  id: number
  rating: number
  content: string
  targetType: string
  targetId: number
  reviewerId: number
  reviewerUsername: string
  isApproved: boolean
  createdAt: string
}

export const adminApi = {
  getStats: () =>
    api.get<AdminStats>('/admin/stats').then(r => r.data),

  // Users
  getUsers: (page = 0, size = 20) =>
    api.get<PageResponse<AdminUser>>('/admin/users', { params: { page, size } }).then(r => r.data),
  changeUserRole: (id: number, role: string) =>
    api.put<AdminUser>(`/admin/users/${id}/role`, null, { params: { role } }).then(r => r.data),
  toggleUserActive: (id: number) =>
    api.put<AdminUser>(`/admin/users/${id}/active`).then(r => r.data),

  // Bands
  getBands: (page = 0, size = 20) =>
    api.get<PageResponse<Band>>('/admin/bands', { params: { page, size } }).then(r => r.data),
  verifyBand: (id: number) =>
    api.put<Band>(`/admin/bands/${id}/verify`).then(r => r.data),
  deleteBand: (id: number) =>
    api.delete(`/admin/bands/${id}`),

  // Venues
  getVenues: (page = 0, size = 20) =>
    api.get<PageResponse<Venue>>('/admin/venues', { params: { page, size } }).then(r => r.data),
  verifyVenue: (id: number) =>
    api.put<Venue>(`/admin/venues/${id}/verify`).then(r => r.data),

  // Reviews
  getReviews: (page = 0, size = 20, approved?: boolean) =>
    api.get<PageResponse<AdminReview>>('/admin/reviews', {
      params: { page, size, ...(approved !== undefined && { approved }) },
    }).then(r => r.data),
  approveReview: (id: number) =>
    api.put<AdminReview>(`/admin/reviews/${id}/approve`).then(r => r.data),
  deleteReview: (id: number) =>
    api.delete(`/admin/reviews/${id}`),
}
