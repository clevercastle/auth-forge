"use client"

import {useEffect} from "react"
import {useRouter} from "next/navigation"

export default function DashboardPage() {
    const router = useRouter()

    useEffect(() => {
        const token = localStorage.getItem('token')
        if (!token) {
            router.push('/login')
        }
    }, [router])

    return (
        <div className="container py-10">
            <h1 className="text-2xl font-bold mb-6">Dashboard</h1>
            <div className="grid gap-6">
                <div className="rounded-lg border p-6">
                    <h2 className="text-lg font-semibold mb-2">Welcome to your dashboard!</h2>
                    <p className="text-muted-foreground">
                        You have successfully logged in. This is a protected page that only authenticated users can
                        access.
                    </p>
                </div>
                <div className="flex justify-end">
                    <button
                        onClick={() => {
                            localStorage.removeItem('token')
                            router.push('/login')
                        }}
                        className="text-sm text-red-500 hover:text-red-600"
                    >
                        Logout
                    </button>
                </div>
            </div>
        </div>
    )
} 