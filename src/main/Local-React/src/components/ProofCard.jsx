import React from 'react';
import './ProofCard.css';

function ProofCard({ proof, index, onReview }) {
  // 상태에 따른 CSS 클래스 결정
  let statusClass = '';
  
  if (proof.status === '대기중') {
    
    statusClass = index % 2 === 0 ? 'status-waiting-blue' : 'status-waiting-purple';
  } else if (proof.status === '승인') {
    statusClass = 'status-approved';
  } else {
    statusClass = 'status-rejected';
  }
  

  return (
    <div className={`proof-card ${statusClass}`}>
      {/* 1. 왼쪽: 이미지 영역 */}
      <div className="proof-image-section">
        <span className={`status-label ${statusClass}-text`}>{proof.status}</span>
        <img src={proof.imageUrl ? `http://localhost:8080/upload/${proof.imageUrl}` : 'https://placehold.co/600x400'} 
            alt="원본 인증샷" className="proof-thumb" 
            onError={(e) => { e.target.src = 'https://placehold.co/600x400'; }}
            />
      </div>

      {/* 2. 중앙: 정보 영역 */}
      <div className="proof-info-section">
        <h3 className="user-nickname">{proof.nickname}</h3>
        <p className="content-category">{proof.contentTitle}</p>
        <p className="upload-date">{proof.createdAt}</p>
      </div>

      {/* 3. 오른쪽: 액션 & 관리자 영역 */}
      <div className="proof-action-section">
        <button className="review-btn" onClick={onReview}>검토하기</button>
        
        <div className="admin-info">
          {proof.status !== '대기' && (
            <>
              {proof.status === '반려' && <p className="reject-reason">{proof.rejectReason}</p>}
              <p className="admin-name">{proof.admin_name || '관리자'}</p>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProofCard;