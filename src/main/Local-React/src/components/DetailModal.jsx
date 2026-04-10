import React, { useState, useEffect } from 'react';
import './DetailModal.css'; // 아래 CSS 참고

function DetailModal({ isOpen, data, onClose, onAuthClick }) {
  // 현재 보고 있는 화면 모드 ('LIST' 또는 'DETAIL')
  const [viewMode, setViewMode] = useState('DETAIL');
  // 현재 상세 보기 중인 장소 데이터
  const [detailItem, setDetailItem] = useState(null);
  const [currentImgIndex, setCurrentImgIndex] = useState(0);

  // 모달이 새로 열릴 때마다 초기 상태 설정
  useEffect(() => {
    if (data) {
      if (data.isMultiple) {
        setViewMode('LIST');
      } else {
        setViewMode('DETAIL');
        setDetailItem(data.selectedPlace);
      }
      setCurrentImgIndex(0);
    }
  }, [data]);

  if (!isOpen || !data) return null;

  // 리스트에서 항목을 클릭했을 때 상세화면으로 전환하는 함수
  const handleItemSelect = (item) => {
    setDetailItem(item);
    setViewMode('DETAIL');
  };

  const getImages = (rawData) => {
    if (!rawData) return ['https://placehold.co/600x400?text=No+Image'];
    if (Array.isArray(rawData)) return rawData; 
    
    
    if (typeof rawData === 'string' && rawData.includes('[')) {
      return rawData
        .replace(/[\[\]]/g, '') 
        .split(',')             
        .map(url => url.trim()) 
        .filter(url => url !== "");
    }
    return [rawData];
  };

  const images = getImages(detailItem?.imgUrl);

 
  const handleImageScroll = (e) => {
    const nextIndex = Math.round(e.target.scrollLeft / e.target.offsetWidth);
    if (currentImgIndex !== nextIndex) setCurrentImgIndex(nextIndex);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="place-modal-content" onClick={(e) => e.stopPropagation()}>
        {/* 상단 헤더: 제목 및 닫기 버튼 */}
        <header className="modal-header">
          {viewMode === 'DETAIL' && data.isMultiple && (
            <button className="btn-back" onClick={() => setViewMode('LIST')}>⬅</button>
          )}
          <h2 className="modal-title">
            {viewMode === 'LIST' ? "📍 이 근처 장소" : (detailItem?.contentsTitle ?? detailItem?.shopTitle)}
          </h2>
          <button className="btn-close" onClick={onClose}>&times;</button>
        </header>

        <div className="modal-body">
          {viewMode === 'LIST' ? (
            /* --- [1] 리스트 뷰 --- */
            <ul className="place-list">
              {data.items.map((item, idx) => (
                <li key={idx} className="place-item" onClick={() => handleItemSelect(item)}>
                  <div className="item-info">
                    <span className="item-name">{item.contentsTitle ?? item.shopTitle}</span>
                    <span className="item-category">{item.rawCategory || '상세보기 >'}</span>
                  </div>
                </li>
              ))}
            </ul>
          ) : (
            /* --- [2] 상세 정보 뷰 --- */
            <div className="place-detail">
              {/* 이미지 영역 */}
              <div className="detail-slider-wrap">
                <div className="detail-slider" onScroll={handleImageScroll}>
                  {images.map((url, idx) => (
                    <img 
                      key={idx} 
                      src={url} 
                      alt={`장소 이미지 ${idx + 1}`} 
                      className="slider-image" 
                    />
                  ))}
                </div>
                {/* 사진이 여러 장일 때만 점(Dot) 표시 */}
                {images.length > 1 && (
                  <div className="slider-dots">
                    {images.map((_, idx) => (
                      <span key={idx} className={`dot ${currentImgIndex === idx ? 'active' : ''}`} />
                    ))}
                  </div>
                )}
              </div>

              {/* 설명 영역 */}
              <div className="detail-description">
                <p>{detailItem?.contentDes || 
                    detailItem?.rawCategory || 
                    "이 장소에 대한 설명이 아직 없습니다."}</p>
              </div>

              {/* 통계 데이터 영역 (인증 수, 즐겨찾기 수) */}
              <div className="detail-stats">
                <div className="stat-box">
                  <span className="stat-label">📸 인증 수</span>
                  <span className="stat-value">{detailItem?.proofCount || 0}회</span>
                </div>
                <div className="stat-box">
                  <span className="stat-label">⭐ 즐겨찾기</span>
                  <span className="stat-value">{detailItem?.favCount || 0}개</span>
                </div>
              </div>

              {/* 하단 인증 버튼 */}
              <footer className="detail-footer">
                <button 
                    className={`btn-auth-main ${!data.isAuthable ? 'disabled' : ''}`}
                    disabled={!data.isAuthable} // 50m 밖이면 클릭 불가
                    onClick={() => onAuthClick({
                    id: detailItem.contentsId ?? detailItem.shopId,
                    type: detailItem.shopId ? 'SHOP' : 'CONTENT',
                    title: detailItem.contentsTitle ?? detailItem.shopTitle
                    })}
                >
                    {data.isAuthable ? "인증하기 📸" : "📍 50m 밖 (인증 불가)"}
                </button>
              </footer>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default DetailModal;