// import axios from "axios";

// export const API_BASE_URL = "http://localhost:8081"

// // const jwt = localStorage.getItem("jwt");			

// export const api = axios.create({				
//     baseURL: API_BASE_URL,					
//     // headers:{
//     //     "Authorization":`Bearer ${jwt}`,  
//     //     "Content-Type":"application/json"
//     // }
// })

// ////for cookie
// // export const api = axios.create({
// //     baseURL: 'http://localhost:8081',
// //     withCredentials: true,
// //     headers: {
// //       'Content-Type': 'application/json',
// //     }
// //   });

// //interceptor to attach jwt to requests
// api.interceptors.request.use(async (config) => {
//     const token = localStorage.getItem("jwt");
//     if(token){
//         config.headers.Authorization = `Bearer ${token}`;
//     }
//     return config;
// },(error) => {
//     return Promise.reject(error);
// });

// //interceptor to handle expired accesstoken(jwt) and refresh it
// api.interceptors.response.use((response) => {
//     return response;
// }, async (error) => {
//     const originalRequest = error.config;
//     if(error.response.status === 401 && !originalRequest._retry){
//         originalRequest._retry = true;
//         try {
//             console.log("retrying the refresh-method");
//             const response = await api.post("/auth/refresh-token", null, {withCredentials : true});
//             const newAccessToken = response.data.newAccessToken;
//             localStorage.setItem("jwt", newAccessToken);
//             originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
//             return api(originalRequest);
//         } catch (refreshError) {
//             console.log("Refresh Token Failed:", refreshError);
//             return Promise.reject(refreshError);
//         }
//     }
//     return Promise.reject(error);
// }
// )


import axios from "axios";

export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8081";
export const api = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true, // This will send cookies with every request
});
let isRefreshing = false;
let failedQueue = [];
const processQueue = (error, token = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });
    failedQueue = [];
};
// // Request interceptor to add JWT to headers
// api.interceptors.request.use(
//     (config) => {
//         const token = localStorage.getItem("jwt");
//         if (token) {
//             config.headers["Authorization"] = `Bearer ${token}`;
//         }
//         return config;
//     },
//     (error) => {
//         return Promise.reject(error);
//     }
// );
// Response interceptor to handle token refresh
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response.status === 401 && !originalRequest._retry) {
            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({resolve, reject});
                }).then(token => {
                    originalRequest.headers['Authorization'] = 'Bearer ' + token;
                    return api(originalRequest);
                }).catch(err => {
                    return Promise.reject(err);
                });
            }
            originalRequest._retry = true;
            isRefreshing = true;
            try {
                // const {data} = await axios.post("http://localhost:8081/auth/refresh-token", {}, {withCredentials: true});
                const {data} = await api.post("/auth/refresh-token", {}, {withCredentials: true});
                const newToken = data.accessToken; // Adjust based on your API response
                localStorage.setItem('jwt', newToken);
                api.defaults.headers['Authorization'] = 'Bearer ' + newToken;
                originalRequest.headers['Authorization'] = 'Bearer ' + newToken;
                processQueue(null, newToken);
                return api(originalRequest);
            } catch (refreshError) {
                processQueue(refreshError, null);
                // Clear stored tokens and redirect to login
                localStorage.removeItem('jwt');
                window.location.href = '/login'; // Adjust based on your routing
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }
        return Promise.reject(error);
    }
);
// Optional: Log all requests
api.interceptors.request.use(request => {
    console.log('Starting Request', JSON.stringify(request, null, 2));
    return request;
});
// Optional: Log all responses
api.interceptors.response.use(response => {
    console.log('Response:', JSON.stringify(response, null, 2));
    return response;
}, error => {
    console.log('Response Error:', error);
    return Promise.reject(error);
});
export default api;