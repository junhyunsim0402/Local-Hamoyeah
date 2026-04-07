import React, { useState } from 'react';
import './Main.css';
import logo from '../assets/logo.png';
import Kakaomap from '../components/KakaoMap'
import AuthModal from '../components/AuthModal';
import MyPageModal from '../components/MyPageModal';
import ScorePanel from '../components/ScorePanel';

function MainPage() {
  const [viewType, setViewType] = useState('noise');
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [selectedPlace, setSelectedPlace] = useState('');
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isMyPageOpen, setIsMyPageOpen] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [shopCategory, setShopCategory] = useState('0');      // shop 마커 기본값 전체
  const [contentCategory, setContentCategory] = useState('0');  // contents 기본값 전체
  const [isPanelOpen, setIsPanelOpen] = useState(false);  // 정주여건 패널 열림/닫힘
  const [scoreData, setScoreData] = useState(null); // 정주여건 점수 데이터

  const handleScoreReady = (data) => {
    setScoreData(data); // 정주여건 점수 저장
    setIsPanelOpen(true); // 정주여건 패널 성공
  };  // 정주여건 패널 점수 함수

  const openAuthModal = (title) => {
    setSelectedPlace(title);
    setIsAuthModalOpen(true);
  };

  const handleOpenMyPage = async () => {
    setIsMenuOpen(false); // 1. 열려있던 설정 드롭다운을 닫기

    // 2. 서버에서 내 정보를 가져옵니다. (나중에 API 연결)
    // const res = await axios.get('/api/user/me');
    // setUserInfo(res.data);

    setIsMyPageOpen(true); // 3. 모달을 켭니다.
  };

  return (
    <div className="main-page">
      {/* 1. 최상단: 로고와 설정 버튼 */}
      <div className="top-bar">
        <img src={logo} alt="Project Logo" className="project-logo" />
        {/* 설정 버튼 클릭 시 토글 */}
        <button className="settings-btn" onClick={() => setIsMenuOpen(!isMenuOpen)}>⚙️</button>

        {/* 메뉴가 열려있을 때만 렌더링 */}
        {isMenuOpen && (
          <>
            {/* 메뉴 바깥 클릭 시 닫히게 하는 투명 레이어 */}
            <div className="menu-overlay" onClick={() => setIsMenuOpen(false)}></div>
            <div className="settings-menu">
              <div className="menu-item" onClick={handleOpenMyPage}>
                👤 마이페이지
              </div>
              <div className="menu-item logout" onClick={() => {
                localStorage.removeItem('accessToken');
                window.location.href = '/login';
              }}>
                🚪 로그아웃
              </div>
            </div>
          </>
        )}
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
          shopCategory={shopCategory}
          contentCategory={contentCategory}
          onScoreReady={handleScoreReady}
        />
      </main>
      <AuthModal
        isOpen={isAuthModalOpen}
        onClose={() => setIsAuthModalOpen(false)}
        targetTitle={selectedPlace}
      />
      <MyPageModal
        isOpen={isMyPageOpen}
        onClose={() => setIsMyPageOpen(false)}
        userInfo={userInfo}
      />
      <ScorePanel
        isOpen={isPanelOpen}
        onClose={() => setIsPanelOpen(false)}
        scoreData={scoreData}
      />
    </div>
  );
}

export default MainPage;