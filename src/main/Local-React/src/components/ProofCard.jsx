import React from 'react';
import './ProofCard.css';

function ProofCard({ proof, index }) {
  // 상태에 따른 CSS 클래스 결정
  let statusClass = '';
  
  if (proof.status === '대기') {
    
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
        <img src={proof.image_url || 'placeholder.png'} alt="인증샷" className="proof-thumb" />
      </div>

      {/* 2. 중앙: 정보 영역 */}
      <div className="proof-info-section">
        <h3 className="user-nickname">{proof.nickname}</h3>
        <p className="content-category">{proof.content_category}</p>
        <p className="upload-date">{proof.created_at}</p>
      </div>

      {/* 3. 오른쪽: 액션 & 관리자 영역 */}
      <div className="proof-action-section">
        <button className="review-btn">검토하기</button>
        
        <div className="admin-info">
          {proof.status !== '대기' && (
            <>
              {proof.status === '반려' && <p className="reject-reason">{proof.reject_reason}</p>}
              <p className="admin-name">{proof.admin_name || '관리자'}</p>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProofCard;