import logo from './logo.svg';
import './App.css';
import LoginPage from './components/Login';
import { Route, Routes } from 'react-router';
import Home from './components/Home';

function App() {
  return (
    <Routes >
      <Route path='/' element={<LoginPage />} />
      <Route path='home' element={<Home />} />
    </Routes>
  );
}

export default App;
