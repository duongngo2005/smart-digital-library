import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore'
import type {Role} from '../types/user'

interface Props{
    allowedRoles: Role[]
}

export default function RoleRoutes({ allowedRoles }: Props){
    const user = useAuthStore((s) => s.user);

    if(!user || !allowedRoles.includes(user.role)){
        return <Navigate to="/" replace/>
    }

    return <Outlet/>
}