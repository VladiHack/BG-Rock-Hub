import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Star, Send } from 'lucide-react'
import toast from 'react-hot-toast'
import type { ReviewTargetType } from '@/types'
import { PAGE_SIZE } from '@/constants'
import { reviewsApi } from '@/api/reviews'
import { useAuthStore } from '@/store/authStore'
import { formatShortDate } from '@/utils/format'
import StarRating from './StarRating'

interface Props {
  targetType: ReviewTargetType
  targetId: number
}

export default function ReviewsList({ targetType, targetId }: Props) {
  const { isAuthenticated } = useAuthStore()
  const queryClient = useQueryClient()
  const [rating, setRating] = useState(5)
  const [content, setContent] = useState('')

  const queryKey = ['reviews', targetType, targetId]

  const { data } = useQuery({
    queryKey,
    queryFn: () => reviewsApi.getForTarget(targetType, targetId, 0, PAGE_SIZE.REVIEWS),
  })

  const createMutation = useMutation({
    mutationFn: reviewsApi.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey })
      setContent('')
      setRating(5)
      toast.success('Ревюто е добавено!')
    },
    onError: (err: any) =>
      toast.error(err.response?.data?.message ?? 'Грешка при добавяне на ревю'),
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    createMutation.mutate({ rating, content, targetType, targetId })
  }

  return (
    <div className="card p-6">
      <h2 className="text-xl font-semibold text-zinc-100 mb-4 flex items-center gap-2">
        <Star size={20} className="text-yellow-400" />
        Ревюта
        {data && (
          <span className="text-zinc-500 text-sm font-normal">({data.totalElements})</span>
        )}
      </h2>

      {isAuthenticated && (
        <form onSubmit={handleSubmit} className="mb-6 p-4 bg-zinc-800/50 rounded-lg space-y-3">
          <div>
            <p className="text-sm text-zinc-400 mb-2">Твоята оценка:</p>
            <StarRating rating={rating} interactive onChange={setRating} size={24} />
          </div>
          <textarea
            className="input resize-none"
            rows={3}
            placeholder="Напиши ревю (по желание)..."
            value={content}
            onChange={(e) => setContent(e.target.value)}
          />
          <button
            type="submit"
            className="btn-primary flex items-center gap-2 text-sm"
            disabled={createMutation.isPending}
          >
            <Send size={14} />
            Публикувай
          </button>
        </form>
      )}

      <div className="space-y-4">
        {data?.content.map((review) => (
          <div key={review.id} className="border-b border-zinc-800 pb-4 last:border-0">
            <div className="flex items-center justify-between mb-1">
              <div className="flex items-center gap-2">
                <div className="w-7 h-7 rounded-full bg-zinc-700 overflow-hidden flex items-center justify-center text-xs text-zinc-400">
                  {review.reviewerAvatarUrl ? (
                    <img src={review.reviewerAvatarUrl} alt="" className="w-full h-full object-cover" />
                  ) : (
                    review.reviewerUsername[0].toUpperCase()
                  )}
                </div>
                <span className="text-sm font-medium text-zinc-200">{review.reviewerUsername}</span>
              </div>
              <div className="flex items-center gap-2">
                <StarRating rating={review.rating} size={13} />
                <span className="text-xs text-zinc-600">{formatShortDate(review.createdAt)}</span>
              </div>
            </div>
            {review.content && <p className="text-zinc-400 text-sm mt-1">{review.content}</p>}
          </div>
        ))}

        {data?.content.length === 0 && (
          <p className="text-zinc-600 text-sm text-center py-4">
            Все още няма ревюта. Бъди първи!
          </p>
        )}
      </div>
    </div>
  )
}
