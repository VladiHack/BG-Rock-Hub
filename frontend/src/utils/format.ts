const BG_LOCALE = 'bg-BG'

export function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString(BG_LOCALE, {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export function formatShortDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString(BG_LOCALE, {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  })
}

export function formatPrice(price: number): string {
  return price === 0 ? 'Безплатно' : `${price} лв`
}
