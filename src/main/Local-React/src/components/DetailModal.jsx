import React, { useState, useEffect, useRef } from 'react';
import './DetailModal.css';
import axios from 'axios';

function DetailModal({ isOpen, data, onClose, onAuthClick, onFavoriteClick }) {
  const [viewMode, setViewMode] = useState('DETAIL');
  const [selectedId, setSelectedId] = useState(null);
  const [currentImgIndex, setCurrentImgIndex] = useState(0);

  const scrollContainerRef = useRef(null);
  const lastTargetIdRef = useRef(null);

  const activeItem = (() => {
    if (!data.isMultiple && data.selectedPlace) {
      return data.selectedPlace;
    }
    
    if (selectedId && data.items) {
      return data.items.find(item => 
        String(item.shopId || item.contentsId) === String(selectedId)
      );
    }

    return data.selectedPlace;
  })();

  useEffect(() => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollTop = 0;
    }
  }, [viewMode, activeItem]);

 useEffect(() => {
  if (!isOpen) {
      lastTargetIdRef.current = null;
      setSelectedId(null);
      return;
    }
  if (data) {
    const isNewClick = lastTargetIdRef.current !== data.targetId;
      if (isNewClick) {
        if (data.isMultiple) {
          setViewMode('LIST');   
          setSelectedId(null); 
        } else {
          setViewMode('DETAIL');
          
          setSelectedId(data.selectedPlace?.shopId || data.selectedPlace?.contentsId);
        }
        lastTargetIdRef.current = data.targetId;
      } 
    }
  }, [data, isOpen]);
  if (!isOpen || !data) return null;

  const handleItemSelect = async (item) => {
    const id = item.shopId || item.contentsId;
    const token = localStorage.getItem('token');
    setSelectedId(id);
    setCurrentImgIndex(0);
  try {
    const isShop = !!item.shopId;
    const targetId = isShop ? item.shopId : item.contentsId;
    
    const params = isShop ? `shopId=${targetId}` : `contentId=${targetId}`;
    const [resFav, resProof] = await Promise.all([
      axios.get(`http://localhost:8080/fav/count?${params}`),
      axios.get(`http://localhost:8080/userproof/verifycount?${params}`, {
        headers: { Authorization: token.startsWith("Bearer ") ? token : `Bearer ${token}` }
      })
    ]);
    item.favCount = resFav.data;
    item.proofCount = resProof.data;

   setViewMode('DETAIL'); 
  } catch (err) {
    setViewMode('DETAIL');
  }
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

  const images = getImages(activeItem?.imgUrl);

  const handleImageScroll = (e) => {
    const nextIndex = Math.round(e.target.scrollLeft / e.target.offsetWidth);
    if (currentImgIndex !== nextIndex) setCurrentImgIndex(nextIndex);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div ref={scrollContainerRef} className="place-modal-content" onClick={(e) => e.stopPropagation()}>
        
        <header className="modal-header">
          <div className="header-left" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            {viewMode === 'DETAIL' && data.isMultiple && (
              <button className="btn-back" onClick={() => setViewMode('LIST')}>⬅</button>
            )}
            <h2 className="modal-title" style={{ margin: 0 }}>
              {viewMode === 'LIST' ? "📍 이 근처 장소" : (activeItem?.contentsTitle ?? activeItem?.shopTitle ?? "정보없음")}
            </h2>
          </div>

          <div className="header-right" style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            
            {viewMode === 'DETAIL' && activeItem && (
              <button 
                className={`btn-favorite ${activeItem.isFavorite ? 'active' : ''}`} 
                style={{ background: 'none', border: 'none', fontSize: '1.5rem', cursor: 'pointer' }}
                onClick={(e) => {
                  e.stopPropagation();
                  onFavoriteClick({
                    id: activeItem.contentsId ?? activeItem.shopId,
                    type: activeItem.shopId ? 'SHOP' : 'CONTENT',
                    isFavorite: activeItem.isFavorite,
                    favId: activeItem.favId
                  });
                }}
              >
                {activeItem.isFavorite ? '⭐' : '☆'}
              </button>
            )}
            <button className="btn-close" onClick={onClose}>&times;</button>
          </div>
        </header>

        <div className="modal-body">
          {viewMode === 'LIST' ? (
            <ul className="place-list">
              {data.items.map((item, idx) => (
                <li key={idx} className="place-item" onClick={() => handleItemSelect(item)}>
                  <div className="item-info">
                    {/* ⭐ 별 표시 삭제! 깔끔하게 이름만 나옵니다. */}
                    <span className="item-name">{item.contentsTitle ?? item.shopTitle}</span>
                    <span className="item-category">{item.rawCategory || '상세보기 >'}</span>
                  </div>
                </li>
              ))}
            </ul>
          ) : (
            <div className="place-detail">
              <div className="detail-slider-wrap">
                <div className="detail-slider" onScroll={handleImageScroll}>
                  {images.map((url, idx) => (
                    <img key={idx} src={url} alt={`장소 이미지 ${idx + 1}`} className="slider-image" />
                  ))}
                </div>
                {images.length > 1 && (
                  <div className="slider-dots">
                    {images.map((_, idx) => (
                      <span key={idx} className={`dot ${currentImgIndex === idx ? 'active' : ''}`} />
                    ))}
                  </div>
                )}
              </div>

              <div className="detail-description">
                <p>{activeItem?.contentDes || activeItem?.rawCategory || "이 장소에 대한 설명이 아직 없습니다."}</p>
              </div>

              <div className="detail-stats">
                <div className="stat-box">
                  <span className="stat-label">📸 인증 수</span>
                  <span className="stat-value">{activeItem?.proofCount || 0}회</span>
                </div>
                <div className="stat-box">
                  <span className="stat-label">⭐ 즐겨찾기</span>
                  <span className="stat-value">{activeItem?.favCount || 0}개</span>
                </div>
              </div>

              <footer className="detail-footer">
                <button 
                    className={`btn-auth-main ${!data.isAuthable ? 'disabled' : ''}`}
                    disabled={!data.isAuthable}
                    onClick={() => onAuthClick({
                    id: activeItem.contentsId ?? activeItem.shopId,
                    type: activeItem.shopId ? 'SHOP' : 'CONTENT',
                    title: activeItem.contentsTitle ?? activeItem.shopTitle
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