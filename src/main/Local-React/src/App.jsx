import React from 'react';
import './App.css';
import MainPage from './pages/Main';

function App() {
  return (
    <div className="app-container">
      {/* 상단 로고부터 지도까지 포함된 메인 페이지 */}
      <MainPage />
      
      {/* 하단 경계선이 있는 흰색 푸터 */}
      <footer className="app-footer">
        <span className="footer-text">© 2026 HAMOYEAH Project</span>
      </footer>
    </div>
  );
}

export default App;