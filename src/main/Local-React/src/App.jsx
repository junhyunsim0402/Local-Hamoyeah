import React from 'react';
import './App.css';
import MainPage from './pages/Main';
import LoginPage from './pages/Login';
import JoinPage from './pages/Join';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
function App() {
  return (
    <Router>
      <div className="app-container">
        {/* 1. 여기에 상단 바가 공통으로 들어갈 수도 있음 */}
        
        {/* 2. 주소에 따라 바뀌는 메인 콘텐츠 영역 */}
        <div className="app-content">
          <Routes>
            <Route path="/" element={<Navigate to="/login" />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/join" element={<JoinPage />} />
            <Route path="/main" element={<MainPage />} />
          </Routes>
        </div>

        {/* 3.  모든 페이지에서 공통으로 보일 푸터 */}
        <footer className="app-footer">
          <span className="footer-text">© 2026 하모예. All rights reserved.</span>
        </footer>
      </div>
    </Router>
  );
}

export default App;