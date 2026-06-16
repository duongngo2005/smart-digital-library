import { useEffect } from "react";
import { useAuthStore } from "../store/useAuthStore"

interface Props{
    children: React.ReactNode
}

export default function AuthProvider({children}: Props){
    const initialize = useAuthStore((s) => s.initialize);
    const isInitialized = useAuthStore((s) => s.isInitialized);

    useEffect(() => {
        initialize();
    }, [initialize])

    if(!isInitialized){
        return (
            <div style={{display: "flex", justifyContent: "center", alignItems: "center", height: "100vh"}}>
                <span>Loading...</span>
            </div>
        )
    }

    return <>{children}</>
}