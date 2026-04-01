import React, { useState } from 'react';
import './Main.css';
import logo from '../assets/logo.png';
import Kakaomap from '../components/KakaoMap'
import AuthModal from '../components/AuthModal';
function MainPage() {
  const [viewType, setViewType] = useState('noise');
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [selectedPlace, setSelectedPlace] = useState('');

  const openAuthModal = (title) => {
    setSelectedPlace(title);
    setIsAuthModalOpen(true);
  };

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

      {/* 3. 중하단: 카테고리 선택 영역 */}
      <div>
        <select defaultValue="">
          <option value="" disabled>가맹점 선택</option>
          <option value="1">음식점</option>
          <option value="2">카페/디저트</option>
          <option value="3">편의점/마트</option>
          <option value="4">약국/약국</option>
          <option value="5">생활/미용</option>
          <option value="6">기타</option>
        </select>
        {/* contents 카테고리 */}
        <select defaultValue="">
          <option value="" disabled>관광/문화 선택</option>
          <option value="1">관광</option>
          <option value="2">축제</option>
          <option value="3">문화재</option>
          <option value="4">공공 체육시설</option>
          <option value="5">건축 미술</option>
          <option value="6">공공 미술</option>
        </select>
      </div>

      {/* 4. 하단: 지도 영역 */}
      <main className="map-section">
        <div className="map-placeholder">
          {viewType === 'noise' ? '🔊 정주여건 로딩 중...' : '🛍️ 지역탐방 로딩 중...'}

        </div>
        <Kakaomap viewType={viewType} onAuthBtnClick={openAuthModal} />
      </main>
      <AuthModal 
        isOpen={isAuthModalOpen} 
        onClose={() => setIsAuthModalOpen(false)} 
        targetTitle={selectedPlace} 
      />
    </div>
  );
}

export default MainPage;