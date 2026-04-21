export type Role = 'FAN' | 'BAND' | 'VENUE' | 'ADMIN'

export type Genre =
  | 'ROCK' | 'METAL' | 'ALTERNATIVE' | 'PUNK' | 'HARD_ROCK'
  | 'CLASSIC_ROCK' | 'PROGRESSIVE_ROCK' | 'GRUNGE' | 'INDIE_ROCK'
  | 'BLUES_ROCK' | 'FOLK_ROCK' | 'HEAVY_METAL' | 'THRASH_METAL'
  | 'DEATH_METAL' | 'BLACK_METAL' | 'DOOM_METAL' | 'GOTHIC' | 'OTHER'

export type EventStatus = 'UPCOMING' | 'ONGOING' | 'COMPLETED' | 'CANCELLED'

export type ReviewTargetType = 'BAND' | 'EVENT' | 'VENUE'

export interface User {
  id: number
  email: string
  username: string
  role: Role
  avatarUrl?: string
  bio?: string
  city?: string
  isVerified: boolean
  createdAt: string
}

export interface Band {
  id: number
  name: string
  genre: Genre
  description?: string
  city?: string
  foundedYear?: number
  avatarUrl?: string
  spotifyUrl?: string
  youtubeUrl?: string
  facebookUrl?: string
  instagramUrl?: string
  members?: string
  isVerified: boolean
  avgRating: number
  totalRatings: number
  ownerId: number
  ownerUsername: string
  photos: string[]
  followersCount: number
  createdAt: string
}

export interface Venue {
  id: number
  name: string
  address: string
  city: string
  description?: string
  capacity?: number
  phone?: string
  website?: string
  coverImgUrl?: string
  isVerified: boolean
  avgRating: number
  totalRatings: number
  ownerId: number
  ownerUsername: string
  photos: string[]
  createdAt: string
}

export interface Event {
  id: number
  title: string
  description?: string
  eventDate: string
  city: string
  ticketPrice?: number
  coverImgUrl?: string
  status: EventStatus
  genre?: Genre
  interestedCount: number
  avgRating: number
  totalRatings: number
  venue?: Venue
  organizerId: number
  organizerUsername: string
  bands: Band[]
  photos: string[]
  createdAt: string
}

export interface Review {
  id: number
  rating: number
  content?: string
  targetType: ReviewTargetType
  targetId: number
  reviewerId: number
  reviewerUsername: string
  reviewerAvatarUrl?: string
  isApproved: boolean
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  userId: number
  username: string
  email: string
  role: Role
}
