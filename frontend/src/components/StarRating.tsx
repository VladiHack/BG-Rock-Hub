import { Star } from 'lucide-react'
import clsx from 'clsx'

interface Props {
  rating: number
  max?: number
  size?: number
  interactive?: boolean
  onChange?: (value: number) => void
}

export default function StarRating({ rating, max = 5, size = 16, interactive = false, onChange }: Props) {
  return (
    <div className="flex items-center gap-0.5">
      {Array.from({ length: max }, (_, i) => i + 1).map((star) => (
        <Star
          key={star}
          size={size}
          className={clsx(
            'transition-colors',
            star <= rating ? 'text-yellow-400 fill-yellow-400' : 'text-zinc-600',
            interactive && 'cursor-pointer hover:text-yellow-300',
          )}
          onClick={() => interactive && onChange?.(star)}
        />
      ))}
    </div>
  )
}
