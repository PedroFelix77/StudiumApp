import { api } from "../api";

export const authService = {
  login: async (email: string, senha: string) => {
    const response = await api.post("/api/auth/login", { email, password: senha });
    return response.data; // token, refreshToken, user, etc
  },

  activateAccount: async (token: string, newPassword: string) => {
    return api.post("/api/auth/activate", 
      { token,
        newPassword: newPassword });
  },

  resendActivation: async (email: string) => {
    return api.post(`/api/auth/resend/${email}`);
  },

  //solicitar email pra reset
  resetPassword: async (email: string) => {
    return api.post(`/api/auth/forgot-password`, {email})
  },

  //confirmar reset enviando token + 
  confirmResetPassword: async(token: string, newPassword: string) => {
    return api.post(`/api/auth/reset-password`, {
      token,
      newPassword
    });
  }
};
