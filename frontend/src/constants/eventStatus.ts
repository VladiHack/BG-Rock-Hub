import type { EventStatus } from '@/types'

export const EVENT_STATUS_LABELS: Record<EventStatus, string> = {
  UPCOMING:  'Предстоящо',
  ONGOING:   'В момента',
  COMPLETED: 'Завършило',
  CANCELLED: 'Отменено',
}

export const EVENT_STATUS_COLORS: Record<EventStatus, string> = {
  UPCOMING:  'bg-green-900 text-green-300',
  ONGOING:   'bg-yellow-900 text-yellow-300',
  COMPLETED: 'bg-zinc-700 text-zinc-400',
  CANCELLED: 'bg-red-900 text-red-400',
}

export const EVENT_STATUS_OPTIONS = (
  Object.entries(EVENT_STATUS_LABELS) as [EventStatus, string][]
).map(([value, label]) => ({ value, label }))
