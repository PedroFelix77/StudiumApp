import { email } from "zod";
import { api } from "../api";

export const authService = {
  login: async (email: string, senha: string) => {
    const response = await api.post("/auth/login", { email, password: senha });
    return response.data; // token, refreshToken, user, etc
  },

  activateAccount: async (token: string, newPassword: string) => {
    return api.post("/auth/activate", 
      { token,
        newPassword: newPassword });
  },

  resendActivation: async (email: string) => {
    return api.post(`/auth/resend/${email}`);
  },

  //solicitar email pra reset
  resetPassword: async (email: string) => {
    return api.post(`/auth/forgot-password`, {email})
  },

  //confirmar reset enviando token + 
  confirmResetPassword: async(token: string, newPassword: string) => {
    return api.post(`/auth/reset-password`, {
      token,
      newPassword
    });
  }
};
