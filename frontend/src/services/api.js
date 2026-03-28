import axios from 'axios';

const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// Content APIs
export const contentApi = {
  generate: (data) => api.post('/content/generate', data),
  saveDraft: (data) => api.post('/content/draft', data),
  schedule: (postId, data) => api.post(`/content/${postId}/schedule`, data),
  submitForApproval: (postId) => api.post(`/content/${postId}/submit-for-approval`),
  getByUser: (userId, status) => api.get(`/content/user/${userId}`, { params: { status } }),
  getById: (postId) => api.get(`/content/${postId}`),
  delete: (postId) => api.delete(`/content/${postId}`),
};

// Auth APIs
export const authApi = {
  googleLogin: (credential) => api.post('/auth/google', { credential }),
};

// Engagement APIs
export const engagementApi = {
  processComment: (data) => api.post('/engagement/comments/process', data),
  getPending: (userId) => api.get(`/engagement/comments/pending/${userId}`),
  getByPost: (postId, status) => api.get(`/engagement/comments/post/${postId}`, { params: { status } }),
  reply: (commentId, text) => api.post(`/engagement/comments/${commentId}/reply`, text),
};

// Analytics APIs
export const analyticsApi = {
  getDashboard: (userId, startDate, endDate) =>
    api.get('/analytics/dashboard', { params: { userId, startDate, endDate } }),
  predictViral: (content, platform) =>
    api.post('/analytics/predict-viral', null, { params: { content, platform } }),
};

// Approval APIs
export const approvalApi = {
  getPending: (approverId) => api.get(`/approval/pending/${approverId}`),
  approve: (workflowId, approverId) =>
    api.post(`/approval/${workflowId}/approve`, null, { params: { approverId } }),
  reject: (workflowId, approverId, reason) =>
    api.post(`/approval/${workflowId}/reject`, { reason }, { params: { approverId } }),
  requestRevision: (workflowId, feedback) =>
    api.post(`/approval/${workflowId}/revision`, { feedback }),
};

export default api;
