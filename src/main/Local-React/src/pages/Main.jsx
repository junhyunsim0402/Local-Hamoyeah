import React, { useState } from 'react';
import './Main.css';
import logo from '../assets/logo.png';
import Kakaomap from '../components/KakaoMap'
function MainPage() {
  const [viewType, setViewType] = useState('noise');

  return (
    <div className="main-page">
      {/* 1. 최상단: 로고와 설정 버튼 */}
      <div className="top-bar">
        <img src={logo} alt="Project Logo" className="project-logo" />
        <button className="settings-btn">⚙️</button>
      </div>

      {/* 2. 중간: 토글 버튼 */}
      <header className="header-tabs">
        <div className="toggle-container">
          <button 
            className={`toggle-btn ${viewType === 'noise' ? 'active' : ''}`}
            onClick={() => setViewType('noise')}
          >
            정주여건
          </button>
          <button 
            className={`toggle-btn ${viewType === 'shop' ? 'active' : ''}`}
            onClick={() => setViewType('shop')}
          >
            지역 탐방
          </button>
        </div>
      </header>

      {/* 3. 하단: 지도 영역 */}
      <main className="map-section">
        <div className="map-placeholder">
          {viewType === 'noise' ? '🔊 정주여건 로딩 중...' : '🛍️ 지역탐방 로딩 중...'}
            
        </div>
        <Kakaomap viewType={viewType} />
      </main>
    </div>
  );
}

export default MainPage;