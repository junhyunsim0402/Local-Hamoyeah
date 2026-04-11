import React, { useState, useEffect, useRef } from 'react';
import './DetailModal.css';

function DetailModal({ isOpen, data, onClose, onAuthClick, onFavoriteClick }) {
  const [viewMode, setViewMode] = useState('DETAIL');
  const [detailItem, setDetailItem] = useState(null);
  const [currentImgIndex, setCurrentImgIndex] = useState(0);

  const scrollContainerRef = useRef(null);

  useEffect(() => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollTop = 0;
    }
  }, [viewMode, detailItem]);

  useEffect(() => {
    if (data) {
      if (viewMode === 'DETAIL' && data.isMultiple) {
        const isShop = !!detailItem?.shopId;
        const currentId = isShop ? detailItem.shopId : detailItem.contentsId;
        
        const updatedItem = data.items?.find(item => 
          String(isShop ? item.shopId : item.contentsId) === String(currentId)
        );
        
        if (updatedItem) setDetailItem(updatedItem);
      } 
      else if (!detailItem) { 
        if (data.isMultiple) {
          setViewMode('LIST');
        } else {
          setViewMode('DETAIL');
          setDetailItem(data.selectedPlace);
        }
      } 
      else if (!data.isMultiple) {
        setDetailItem(data.selectedPlace);
      }
    }
  }, [data]); 

  if (!isOpen || !data) return null;

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
      <div ref={scrollContainerRef} className="place-modal-content" onClick={(e) => e.stopPropagation()}>
        
        <header className="modal-header">
          <div className="header-left" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            {viewMode === 'DETAIL' && data.isMultiple && (
              <button className="btn-back" onClick={() => setViewMode('LIST')}>⬅</button>
            )}
            <h2 className="modal-title" style={{ margin: 0 }}>
              {viewMode === 'LIST' ? "📍 이 근처 장소" : (detailItem?.contentsTitle ?? detailItem?.shopTitle)}
            </h2>
          </div>

          <div className="header-right" style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            
            {viewMode === 'DETAIL' && (
              <button 
                className={`btn-favorite ${data.isFavorite ? 'active' : ''}`} 
                style={{ background: 'none', border: 'none', fontSize: '1.5rem', cursor: 'pointer' }}
                onClick={(e) => {
                  e.stopPropagation();
                  onFavoriteClick({
                    id: detailItem.contentsId ?? detailItem.shopId,
                    type: detailItem.shopId ? 'SHOP' : 'CONTENT',
                    isFavorite: data.isFavorite,
                    favId: data.favId
                  });
                }}
              >
                {data.isFavorite ? '⭐' : '☆'}
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
                <p>{detailItem?.contentDes || detailItem?.rawCategory || "이 장소에 대한 설명이 아직 없습니다."}</p>
              </div>

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

              <footer className="detail-footer">
                <button 
                    className={`btn-auth-main ${!data.isAuthable ? 'disabled' : ''}`}
                    disabled={!data.isAuthable}
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