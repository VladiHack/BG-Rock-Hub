interface Props {
  page: number
  totalPages: number
  isLast: boolean
  onChange: (page: number) => void
}

export default function Pagination({ page, totalPages, isLast, onChange }: Props) {
  if (totalPages <= 1) return null

  return (
    <div className="flex justify-center items-center gap-2 mt-8">
      <button
        className="btn-secondary px-4 py-2 text-sm"
        disabled={page === 0}
        onClick={() => onChange(page - 1)}
      >
        Предишна
      </button>
      <span className="text-zinc-400 text-sm px-4">
        {page + 1} / {totalPages}
      </span>
      <button
        className="btn-secondary px-4 py-2 text-sm"
        disabled={isLast}
        onClick={() => onChange(page + 1)}
      >
        Следваща
      </button>
    </div>
  )
}
