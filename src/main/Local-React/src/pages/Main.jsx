import React, { useState } from 'react';
import './Main.css';
import logo from '../assets/logo.png';
import Kakaomap from '../components/KakaoMap'
import AuthModal from '../components/AuthModal';
function MainPage() {
  const [viewType, setViewType] = useState('noise');
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [selectedPlace, setSelectedPlace] = useState('');
  const [shopCategory, setShopCategory] = useState('0');      // shop 마커 기본값 전체
  const [contentCategory, setContentCategory] = useState('0');  // contents 기본값 전체


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

      {/* 3. 카테고리 선택 영역 */}
      <div>
        <select value={shopCategory} onChange={(e) => setShopCategory(e.target.value)}>
          <option value="0">가맹점 전체</option>
          <option value="FOOD">음식점</option>
          <option value="CAFE">카페/디저트</option>
          <option value="STORE">편의점/마트</option>
          <option value="MEDICAL">의료/약국</option>
          <option value="LIFE">생활/미용</option>
          <option value="ETC">기타</option>
          <option value="NONE">선택 안함</option>
        </select>

        <select value={contentCategory} onChange={(e) => setContentCategory(e.target.value)}>
          <option value="0">관광/문화 전체</option>
          <option value="1">관광</option>
          <option value="2">축제</option>
          <option value="3">문화재</option>
          <option value="4">공공 체육시설</option>
          <option value="5">건축 미술</option>
          <option value="6">공공 미술</option>
          <option value="NONE">선택 안함</option>
        </select>
      </div>

      {/* 4. 하단: 지도 영역 */}
      <main className="map-section">
        <div className="map-placeholder">
          {viewType === 'noise' ? '🔊 정주여건 로딩 중...' : '🛍️ 지역탐방 로딩 중...'}

        </div>
        <Kakaomap
          viewType={viewType}
          onAuthBtnClick={openAuthModal}
          shopCategory={shopCategory}      // ✅ props 전달
          contentCategory={contentCategory} // ✅ props 전달
        />
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