import React from 'react';
import './MyPageModal.css';

function MyPageModal({ isOpen, onClose, userInfo }) {
    if (!isOpen) return null;

    return (
        <div className="mypage-overlay" onClick={onClose}>
            <div className="mypage-content" onClick={(e) => e.stopPropagation()}>
                <button className="mypage-close" onClick={onClose}>&times;</button>
                
                <div className="mypage-header">
                    <div className="user-avatar">👤</div>
                    <h2 className="user-nickname">{userInfo?.nickname || '즐거운 하모'} 님</h2>
                    <p className="user-email">{userInfo?.email || 'test@gmail.com'}</p>
                </div>

                <div className="mypage-body">
                    {/* 포인트 영역 */}
                    <div className="info-card point-card">
                        <span>💰 내 포인트</span>
                        <strong>{userInfo?.point || 0} P</strong>
                    </div>

                    {/* 활동 내역 리스트 */}
                    <div className="activity-section">
                        <h3>📋 최근 인증 내역</h3>
                        <div className="activity-list">
                            {/* 데이터가 없을 때 예시 */}
                            <p className="empty-msg">아직 인증한 내역이 없어요. ㅠㅠ</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default MyPageModal;