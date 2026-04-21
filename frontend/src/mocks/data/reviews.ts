import type { Review } from '@/types'

export const MOCK_REVIEWS: Review[] = [
  {
    id: 1, rating: 5, content: 'Легенди! Концертът беше незабравим, енергията на сцената е невероятна.',
    targetType: 'BAND', targetId: 1,
    reviewerId: 30, reviewerUsername: 'rock_fan_ivan', reviewerAvatarUrl: undefined,
    isApproved: true, createdAt: '2025-11-10T18:00:00Z',
  },
  {
    id: 2, rating: 4, content: 'Страхотна банда, живото изпълнение е много по-добро от записите.',
    targetType: 'BAND', targetId: 1,
    reviewerId: 31, reviewerUsername: 'metalhead_bg', reviewerAvatarUrl: undefined,
    isApproved: true, createdAt: '2025-12-05T20:00:00Z',
  },
  {
    id: 3, rating: 5, content: 'Сигнал са неостаряващи. "Хей, момче" на живо те кара да плачеш.',
    targetType: 'BAND', targetId: 2,
    reviewerId: 32, reviewerUsername: 'plovdiv_rocker', reviewerAvatarUrl: undefined,
    isApproved: true, createdAt: '2025-10-22T15:00:00Z',
  },
  {
    id: 4, rating: 5, content: 'Mixtape 5 е домът на рока в България. Звукът е перфектен, атмосферата — магична.',
    targetType: 'VENUE', targetId: 1,
    reviewerId: 33, reviewerUsername: 'sofia_metalhead', reviewerAvatarUrl: undefined,
    isApproved: true, createdAt: '2025-09-15T21:00:00Z',
  },
  {
    id: 5, rating: 4, content: 'Малко тесничко при голям концерт, но атмосферата компенсира всичко!',
    targetType: 'VENUE', targetId: 1,
    reviewerId: 34, reviewerUsername: 'grunger77', reviewerAvatarUrl: undefined,
    isApproved: true, createdAt: '2025-08-30T22:00:00Z',
  },
  {
    id: 6, rating: 5, content: 'Rock Legends Night беше събитието на годината. Три банди, всяка по-добра от предната.',
    targetType: 'EVENT', targetId: 8,
    reviewerId: 35, reviewerUsername: 'classic_rock_fan', reviewerAvatarUrl: undefined,
    isApproved: true, createdAt: '2026-03-09T10:00:00Z',
  },
  {
    id: 7, rating: 4, content: 'FSB са на ниво с всяко изпълнение. Малко кратко шоу, но качеството е безспорно.',
    targetType: 'BAND', targetId: 3,
    reviewerId: 36, reviewerUsername: 'progrock_nerd', reviewerAvatarUrl: undefined,
    isApproved: true, createdAt: '2025-07-18T19:00:00Z',
  },
  {
    id: 8, rating: 5, content: 'Балкан Фюри правят нещо уникално — народна музика и рок в едно. Живото е задължително!',
    targetType: 'BAND', targetId: 9,
    reviewerId: 37, reviewerUsername: 'folk_rock_lover', reviewerAvatarUrl: undefined,
    isApproved: true, createdAt: '2025-06-10T17:00:00Z',
  },
]
