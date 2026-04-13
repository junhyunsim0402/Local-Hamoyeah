import React, { useState } from 'react';
import './Main.css';
import logo from '../assets/logo.png';
import Kakaomap from '../components/KakaoMap'
import AuthModal from '../components/AuthModal';
import MyPageModal from '../components/MyPageModal';
import ScorePanel from '../components/ScorePanel';
import NewsPanel from '../components/NewsPanel';
import DetailModal from '../components/DetailModal';
import RecommendBanner from '../components/RecommendBanner';

function MainPage() {
  const [viewType, setViewType] = useState('noise');
  const [newsCategory, setNewsCategory] = useState({ contentType: 1, contentCategory: 1 }); // 뉴스카테고리 기본값 : 관광
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [authData, setAuthData] = useState({ id: null, type: '', title: '' });
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isMyPageOpen, setIsMyPageOpen] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [shopCategory, setShopCategory] = useState('0');      // shop 마커 기본값 전체
  const [contentCategory, setContentCategory] = useState('0');  // contents 기본값 전체
  const [isPanelOpen, setIsPanelOpen] = useState(false);  // 정주여건 패널 열림/닫힘
  const [scoreData, setScoreData] = useState(null); // 정주여건 점수 데이터
  const [toastVisible, setToastVisible] = useState(false); // 토스트 바 표시 여부
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false); // 상세 모달 열림 여부
  const [selectedPlaceInfo, setSelectedPlaceInfo] = useState(null);  // 클릭된 장소의 정보
  const [isRecommendOpen, setIsRecommendOpen] = useState(false);  // 진주 추천 창 여부
  const [moveToLocation, setMoveToLocation] = useState(null);   // 추천 content 클릭 여부


  const handleScoreReady = (data) => {
    setScoreData(data); // 정주여건 점수 저장
    setToastVisible(true);   // 패널 바로 열지 않고 토스트만 표시
    setIsPanelOpen(false); // 정주여건 패널 성공
  };  // 정주여건 패널 점수 함수

  const handleMarkerClick = (data) => {
    setToastVisible(false);
    setSelectedPlaceInfo(data);
    setIsDetailModalOpen(true);
  };

  const openAuthModal = (data) => {
    setAuthData(data);
    setIsDetailModalOpen(false);
    setIsAuthModalOpen(true);
  };

  const handleOpenMyPage = async () => {
    setIsMenuOpen(false);

    setIsMyPageOpen(true);
  };

  const handleRecommendClick = (item) => {
    setIsRecommendOpen(false);
    setMoveToLocation({ lat: item.lat, lng: item.lng });
};

  return (
    <div className="main-page">
      {/* 최상단: 로고와 설정 버튼 */}
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
                localStorage.removeItem('token');
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
            className={`toggle-btn ${viewType === 'news' ? 'active' : ''}`}
            onClick={() => setViewType('news')}
          >
            진주시 뉴스
          </button>
        </div>
      </header>

      {/* 4. 하단: 지도 영역 */}
      <main className="map-section">
        {/* 3. 카테고리 선택 영역 */}
        <div className="category-select-wrap">
          {viewType === 'noise' ? (
            // 정주여건 탭일 때 기존 select
            <>
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
            </>
          ) : (
            // 진주시 뉴스 탭일 때 뉴스 카테고리 select
            <select
              onChange={(e) => {
                const [type, category] = e.target.value.split(',');
                setNewsCategory({ contentType: Number(type), contentCategory: Number(category) });
              }}
            >
              <option value="1,1">관광지</option>
              <option value="1,2">축제</option>
              <option value="1,3">문화재</option>
              <option value="2,1">운동시설</option>
              <option value="3,1">공공미술</option>
              <option value="3,2">건축물미술</option>
            </select>
          )}
        </div>
        {/* 토스트 바 */}
        {toastVisible && (
          <div className="score-toast">
            <span>📍 이 지역의 정주여건을 확인해보세요</span>
            <button onClick={() => {
              setIsPanelOpen(true);
              setToastVisible(false);
            }}>
              정주여건 확인
            </button>
          </div>
        )}
        {viewType === 'news' ? (
          <NewsPanel newsCategory={newsCategory} />
        ) : (
          <>
            <button
              className="recommend-btn"
              onClick={() => setIsRecommendOpen(!isRecommendOpen)}
            >
              🏆 {isRecommendOpen ? '추천 닫기' : '오늘의 추천'}
            </button>
            {isRecommendOpen && <RecommendBanner onContentClick={handleRecommendClick} />}
            <Kakaomap
              viewType={viewType}
              onAuthBtnClick={openAuthModal}
              shopCategory={shopCategory}
              contentCategory={contentCategory}
              onScoreReady={handleScoreReady}
              onMarkerClick={handleMarkerClick}
              moveToLocation={moveToLocation}
            />
          </>
        )}
      </main>
      <AuthModal
        isOpen={isAuthModalOpen}
        onClose={() => {
          setIsPanelOpen(false);
          setToastVisible(false); // 패널 닫으면 토스트도 숨김
          setIsAuthModalOpen(false);
        }}
        targetId={authData.id}
        targetType={authData.type}
        targetTitle={authData.title}
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
      {isDetailModalOpen && (
        <DetailModal
          isOpen={isDetailModalOpen}
          data={selectedPlaceInfo}
          onClose={() => setIsDetailModalOpen(false)}
          onAuthClick={openAuthModal}
        />
      )}
    </div>
  );
}
export default MainPage;