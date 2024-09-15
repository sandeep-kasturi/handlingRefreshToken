import axios from 'axios';
import React, { useEffect, useState } from 'react'
import { api } from './ApiConfig';
import { useNavigate } from 'react-router';

const Home = () => {
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const token = localStorage.getItem("jwt");

  useEffect(() => {
    const fetchMessage = async () => {
      try {
        // const { data } = await api.get("http://localhost:8081/usr/getMsg");
        const { data } = await api.get("/usr/getMsg", {headers:{Authorization:`Bearer ${token}`}, withCredentials: true});

        console.log('data of getMsg call: ', data);
        setMessage(data);
      } catch (error) {
        if(error.response && error.response.status === 401){
          console.log("Unauthorized, attempting to refresh the token...");
        }else{
          console.log("Error fetching message", error);
        }
      }
    };
    fetchMessage();
  }, [message]);

  const handleLogout = async() => {
    try {
      const {data} = await axios.get("http://localhost:8081/auth/logout");
      console.log('data for logout call: ', data);
      alert('logout successful');
      navigate('/');
    } catch (error) {
      console.log(error);
      alert('logout failed');
    }
  }
  return (
    <div className='flex flex-col'>
      <div className='flex justify-center'>
        Welcome Home, {message} 
      </div>
      <div>
        <button className='flex justify-end' onClick={handleLogout}>Logout</button>
      </div>
    </div>
  )
}

export default Home
