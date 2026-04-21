import type { Role } from '@/types'

export const ROLE_LABELS: Record<Role, string> = {
  FAN:   'Фен',
  BAND:  'Банда',
  VENUE: 'Клуб',
  ADMIN: 'Администратор',
}

export const ROLE_DESCRIPTIONS: Record<Role, string> = {
  FAN:   'Разглежда, оценява и следва банди',
  BAND:  'Управлява профил на банда и публикува събития',
  VENUE: 'Управлява профил на клуб или зала',
  ADMIN: 'Модерация и управление на платформата',
}

/** Roles available during public registration (ADMIN is granted manually). */
export const REGISTERABLE_ROLES: Role[] = ['FAN', 'BAND', 'VENUE']
