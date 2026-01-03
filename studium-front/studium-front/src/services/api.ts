import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  // Tenta pegar o token de access_token primeiro
  let token = localStorage.getItem("access_token");
  
  // Se não encontrar, tenta pegar do objeto auth
  if (!token) {
    const auth = localStorage.getItem("auth");
    if (auth) {
      try {
        const parsed = JSON.parse(auth);
        token = parsed.token;
      } catch (e) {
        // Ignora erro de parse
      }
    }
  }
  
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export { api };