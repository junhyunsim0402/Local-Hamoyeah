import React, { useState, useEffect, useRef } from 'react';
import './DetailModal.css';
import axios from 'axios';
const YOUTUBE_KEYS = [
    import.meta.env.VITE_YOUTUBE_API_KEY_1,
    import.meta.env.VITE_YOUTUBE_API_KEY_2,
    import.meta.env.VITE_YOUTUBE_API_KEY_3,
  ].filter(key => key);
function DetailModal({ isOpen, data, onClose, onAuthClick, onFavoriteClick }) {
  const [viewMode, setViewMode] = useState('DETAIL');
  const [selectedId, setSelectedId] = useState(null);
  const [currentImgIndex, setCurrentImgIndex] = useState(0);
  const [youtubeVideos, setYoutubeVideos] = useState([]);
  const keyIndexRef = useRef(0);
  const scrollContainerRef = useRef(null);
  const lastTargetIdRef = useRef(null);
  const lastSearchedIdRef = useRef(null);

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

  const fetchYoutubeVideos = async (query) => {
    let attempts = 0;
    while (attempts < YOUTUBE_KEYS.length) {
      const currentKey = YOUTUBE_KEYS[keyIndexRef.current];
      
      try {
        const res = await axios.get('https://www.googleapis.com/youtube/v3/search', {
          params: {
            part: 'snippet',
            q: `진주 ${query}`,
            maxResults: 3,
            type: 'video',
            key: currentKey,
            safeSearch: 'strict',
            relevanceLanguage: 'ko',
            regionCode: 'KR',
            order: 'relevance'
          }
        });
        
        setYoutubeVideos(res.data.items);
        return; 

      } catch (err) {
        
        if (err.response?.status === 403) {
          console.warn(`키 ${keyIndexRef.current + 1}번 소진! 다음 키로 교체합니다.`);
          
          keyIndexRef.current = (keyIndexRef.current + 1) % YOUTUBE_KEYS.length;
          attempts++;
        } else {
          console.error("유튜브 로드 실패:", err);
          break;
        }
      }
    }
    console.error("모든 유튜브 API 키의 할당량이 소진");
  };

  useEffect(() => {
    if (scrollContainerRef.current) {
      scrollContainerRef.current.scrollTop = 0;
    }
  }, [viewMode, activeItem]);

  useEffect(() => {
    const contentType = Number(activeItem?.categoryId);
    const currentId = activeItem?.contentsId || activeItem?.shopId;

    if (isOpen && [1, 2, 3].includes(contentType) && activeItem?.contentsTitle) {
      if (lastSearchedIdRef.current !== currentId) {
        fetchYoutubeVideos(activeItem.contentsTitle);
        lastSearchedIdRef.current = currentId; 
      }
    } else if (!isOpen) {
      setYoutubeVideos([]);
      lastSearchedIdRef.current = null;
    }
  }, [activeItem, isOpen]);

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

              {youtubeVideos.length > 0 && (
                <div className="youtube-section" style={{ marginTop: '20px', padding: '0 15px' }}>
                  <h3 style={{ fontSize: '1rem', marginBottom: '10px', color: '#333' }}>📺 관련 유튜브 영상</h3>
                  <div className="youtube-list" style={{ 
                    display: 'flex', 
                    gap: '12px', 
                    overflowX: 'auto', 
                    paddingBottom: '10px',
                    WebkitOverflowScrolling: 'touch' // 모바일 부드러운 스크롤
                  }}>
                    {youtubeVideos.map((video, idx) => (
                      <a 
                        key={idx} 
                        href={`https://www.youtube.com/watch?v=${video.id.videoId}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        style={{ textDecoration: 'none', color: 'inherit', minWidth: '160px', width: '160px' }}
                      >
                        <img 
                          src={video.snippet.thumbnails.medium.url} 
                          alt={video.snippet.title} 
                          style={{ width: '100%', borderRadius: '8px', aspectRatio: '16/9', objectFit: 'cover' }} 
                        />
                        <p style={{ 
                          fontSize: '0.8rem', 
                          marginTop: '6px', 
                          lineHeight: '1.3',
                          display: '-webkit-box', 
                          WebkitLineClamp: 2, 
                          WebkitBoxOrient: 'vertical', 
                          overflow: 'hidden' 
                        }}>
                          {video.snippet.title}
                        </p>
                      </a>
                    ))}
                  </div>
                </div>
              )}

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