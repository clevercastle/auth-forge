const API_BASE_URL = 'http://localhost:8080';

export interface LoginCredentials {
    email: string;
    password: string;
}

export interface RegisterCredentials extends LoginCredentials {
    name: string;
}

export interface AuthResponse {
    token: string;
    user: {
        id: string;
        email: string;
        name: string;
    };
}

export const authService = {
    async login(credentials: LoginCredentials): Promise<AuthResponse> {
        const encodedCredentials = btoa(`${credentials.email}:${credentials.password}`);
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'GET',
            headers: {
                'Authorization': `Basic ${encodedCredentials}`,
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            throw new Error('Login failed');
        }

        return response.json();
    },

    async register(credentials: RegisterCredentials): Promise<AuthResponse> {
        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(credentials),
        });

        if (!response.ok) {
            throw new Error('Registration failed');
        }

        return response.json();
    },

    async loginWithGoogle(): Promise<AuthResponse> {
        const response = await fetch(`${API_BASE_URL}/auth/google`, {
            method: 'GET',
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error('Google login failed');
        }

        return response.json();
    },

    async loginWithApple(): Promise<AuthResponse> {
        const response = await fetch(`${API_BASE_URL}/auth/apple`, {
            method: 'GET',
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error('Apple login failed');
        }

        return response.json();
    },
}; 