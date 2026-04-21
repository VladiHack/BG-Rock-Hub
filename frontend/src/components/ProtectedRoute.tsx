import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { ROUTES } from '@/constants'

interface Props {
  children: ReactNode
  requiredRole?: string
}

export default function ProtectedRoute({ children, requiredRole }: Props) {
  const { isAuthenticated, user } = useAuthStore()

  if (!isAuthenticated) return <Navigate to={ROUTES.LOGIN} replace />
  if (requiredRole && user?.role !== requiredRole) return <Navigate to={ROUTES.HOME} replace />

  return <>{children}</>
}
