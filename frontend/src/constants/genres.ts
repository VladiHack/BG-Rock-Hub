import type { Genre } from '@/types'

export const GENRE_LABELS: Record<Genre, string> = {
  ROCK:             'Rock',
  METAL:            'Metal',
  ALTERNATIVE:      'Alternative',
  PUNK:             'Punk',
  HARD_ROCK:        'Hard Rock',
  CLASSIC_ROCK:     'Classic Rock',
  PROGRESSIVE_ROCK: 'Progressive Rock',
  GRUNGE:           'Grunge',
  INDIE_ROCK:       'Indie Rock',
  BLUES_ROCK:       'Blues Rock',
  FOLK_ROCK:        'Folk Rock',
  HEAVY_METAL:      'Heavy Metal',
  THRASH_METAL:     'Thrash Metal',
  DEATH_METAL:      'Death Metal',
  BLACK_METAL:      'Black Metal',
  DOOM_METAL:       'Doom Metal',
  GOTHIC:           'Gothic',
  OTHER:            'Друго',
}

export const GENRE_OPTIONS = (Object.entries(GENRE_LABELS) as [Genre, string][]).map(
  ([value, label]) => ({ value, label }),
)
