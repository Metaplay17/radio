import type { NavigateFunction } from "react-router-dom";
import type { ErrorResponse } from "./interfaces";

export class ErrorResponseException extends Error {
    constructor(message : string) {
        super(message);
    }
}

export const makeSafeAuthGet = async (path : string, navigate : NavigateFunction) => {
    try {
        const API_URL = import.meta.env.VITE_API_URL;
        const token : string | null = localStorage.getItem('token');
        const response = await fetch(API_URL + path, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });
        if (response.ok) {
            const json = await response.json();
            return json;
        }
        else if (response.status == 401) {
            navigate('/login');
        }
        else {
            const json : ErrorResponse = await response.json();
            throw new ErrorResponseException(json.message);
        }
    } catch (e) {
        if (e instanceof TypeError) {
            throw new ErrorResponseException(e.message);
        }
        else {
            throw e;
        }
    }
}

export const makeSafeAuthPost = async (path : string, navigate : NavigateFunction, body : any) => {
    try {
        const API_URL = import.meta.env.VITE_API_URL;
        const token : string | null = localStorage.getItem('token');
        const response = await fetch(API_URL + path, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });
        if (response.ok) {
            const json = await response.json();
            return json;
        }
        else if (response.status == 401) {
            navigate('/login');
        }
        else {
            const json : ErrorResponse = await response.json();
            throw new ErrorResponseException(json.message);
        }
    } catch (e) {
        if (e instanceof TypeError) {
            throw new ErrorResponseException(e.message);
        }
        else {
            throw e;
        }
    }
    
}

export const logout = (navigate : NavigateFunction) => {
    localStorage.removeItem('token');   
    localStorage.removeItem('username');
    navigate('/login');
}