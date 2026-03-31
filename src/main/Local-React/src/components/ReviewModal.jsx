import React, { useState } from 'react';
import './ReviewModal.css';

function ReviewModal({ proof, onClose, onConfirm }) {
  const [rejectReason, setRejectReason] = useState('');

  if (!proof) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="close-x-btn" onClick={onClose}>&times;</button>
        
        <h2 className="modal-title">인증 내역 검토</h2>

        <div className="modal-body">
          {/* 1. 이미지 크게 보기 */}
          <div className="modal-image-wrap">
            <img src={proof.image_url || 'placeholder.png'} alt="원본 인증샷" />
          </div>

          {/* 2. 유저 정보 요약 */}
          <div className="modal-user-info">
            <p><strong>닉네임:</strong> {proof.nickname}</p>
            <p><strong>카테고리:</strong> {proof.content_category}</p>
          </div>

          {/* 3. 반려 사유 입력창 */}
          <div className="modal-input-group">
            <label>반려 사유 (반려 시 필수 입력)</label>
            <textarea 
              placeholder="사유를 입력하세요..."
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
            />
          </div>
        </div>

        {/* 4. 하단 버튼 영역 */}
        <div className="modal-footer-btns">
          <button 
            className="btn-reject" 
            onClick={() => onConfirm(proof.id, '반려', rejectReason)}
          >
            반려하기
          </button>
          <button 
            className="btn-approve" 
            onClick={() => onConfirm(proof.id, '승인')}
          >
            승인하기
          </button>
        </div>
      </div>
    </div>
  );
}

export default ReviewModal;