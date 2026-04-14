import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ProofCard from './ProofCard'; 
import './MyPageModal.css';

function MyPageModal({ isOpen, onClose }) {
    const [userInfo, setUserInfo] = useState(null);
    const [userProofs, setUserProofs] = useState([]);
    const [loading, setLoading] = useState(true);

    // 모달이 열릴 때마다 데이터를 새로 가져옵니다.
    useEffect(() => {
        if (isOpen) {
            fetchMyPageData();
        }
    }, [isOpen]);

    const fetchMyPageData = async () => {
        setLoading(true);
        try {
            const token = localStorage.getItem('token'); // 저장된 토큰 가져오기
            const headers = { Authorization: token };

            // 1) 내 기본 정보 가져오기 (이메일, 닉네임, 포인트)
            const userRes = await axios.get("http://localhost:8080/user/myinfo", { headers });
            
            // 2) 내 인증 내역만 가져오기 
            const proofRes = await axios.get("http://localhost:8080/userproof/usermylist", { headers });

            setUserInfo(userRes.data);
            setUserProofs(proofRes.data);
        } catch (e) {
            console.error("마이페이지 데이터 로드 실패", e);
        } finally {
            setLoading(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="mypage-overlay" onClick={onClose}>
            <div className="mypage-content" onClick={(e) => e.stopPropagation()}>
                <button className="mypage-close" onClick={onClose}>&times;</button>
                
                {loading ? (
                    <div className="loading-msg">정보를 불러오는 중...</div>
                ) : (
                    <>
                        <div className="mypage-header">
                            {/* <div className="user-avatar">👤</div>*/}
                            <h2 className="user-nickname">{userInfo?.nickname} 님</h2>
                            <p className="user-email">{userInfo?.email}</p>
                        </div>

                        <div className="mypage-body">
                            {/* 포인트 영역  */}
                            <div className="info-card point-card">
                                <span>💰 내 포인트</span>
                                <strong>{userInfo?.totalPoints?.toLocaleString() || 0} P</strong>
                            </div>

                            {/* 활동 내역 리스트 */}
                            <div className="activity-section">
                                <h3>📋 최근 인증 내역</h3>
                                <div className="activity-list" style={{ maxHeight: '400px', overflowY: 'auto' }}>
                                    {userProofs.length > 0 ? (
                                        [...userProofs].reverse.map((proof, index) => (
                                            <ProofCard 
                                                key={proof.proofId} 
                                                proof={proof} 
                                                index={index}
                                                onReview={() => {}} 
                                            />
                                        ))
                                    ) : (
                                        <p className="empty-msg">아직 인증한 내역이 없어요. ㅠㅠ</p>
                                    )}
                                </div>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}

export default MyPageModal;