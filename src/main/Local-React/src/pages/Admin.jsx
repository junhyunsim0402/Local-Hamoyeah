import React, { useState } from 'react';
import ProofCard from '../components/ProofCard';
import ReviewModal from '../components/ReviewModal';
import './Admin.css'

function AdminPage() {
const [proofs, setProofs] = useState([
            { 
            id: 1, status: '대기', nickname: '민수', content_category: '카페 투어', 
            created_at: '2026-03-30 14:00', 
            // 400x300 사이즈 / 배경 연하늘(E6F1FD) / 글자색 검정 / 텍스트 "Cafe"
            image_url: 'https://placehold.co/600x400' 
        },
        { 
            id: 2, status: '대기', nickname: '민수', content_category: '카페 투어', 
            created_at: '2026-03-30 14:00', 
            // 400x300 사이즈 / 배경 연하늘(E6F1FD) / 글자색 검정 / 텍스트 "Cafe"
            image_url: 'https://placehold.co/600x400' 
        },
        { 
            id: 3, status: '대기', nickname: '민수', content_category: '카페 투어', 
            created_at: '2026-03-30 14:00', 
            // 400x300 사이즈 / 배경 연하늘(E6F1FD) / 글자색 검정 / 텍스트 "Cafe"
            image_url: 'https://placehold.co/600x400' 
        },
        { 
            id: 4, status: '대기', nickname: '정우', content_category: '오운완 인증', 
            created_at: '2026-03-30 14:10', 
            // 400x300 사이즈 / 배경 연보라(EBEBFF) / 글자색 검정 / 텍스트 "Workout"
            image_url: 'https://placehold.co/600x400' 
        },
        { 
            id: 5, status: '승인', nickname: '길동', content_category: '러닝 인증', 
            created_at: '2026-03-30 13:30', 
            // 승인된 건 회색 배경
            image_url: 'https://placehold.co/600x400'
        }
    ]);

    const [selectedStatus, setSelectedStatus] = useState('전체'); // 상태 필터링용 State
    const [sortOrder, setSortOrder] = useState('오래된 순'); // 정렬용 State
    const [selectedProof, setSelectedProof] = useState(null);

    // 1. 상태 업데이트 로직 나중에 API 연결
    const handleReviewConfirm = (id, newStatus, reason = '') => {
        setProofs(prev => prev.map(p => 
          p.id === id ? { ...p, status: newStatus, reject_reason: reason, admin_name: '관리자' } : p
        ));
        setSelectedProof(null);
    };

    const filteredProofs = proofs
        .filter(p => selectedStatus === '전체' || p.status === selectedStatus)
        .sort((a, b) => {
            if (sortOrder === '최신순') return new Date(b.created_at) - new Date(a.created_at);
            return new Date(a.created_at) - new Date(b.created_at);
        });

    


    return (
        <div className="admin-page-root">
            <h1 className="admin-title">관리자 인증 화면</h1>
            <div className="admin-header-info">
                <div className="admin-left-filters">
                    <select 
                        className="admin-status-select"
                        value={selectedStatus}
                        onChange={(e) => setSelectedStatus(e.target.value)}
                    >
                        <option value="전체">전체 보기</option>
                        <option value="대기">대기 중</option>
                        <option value="승인">승인 완료</option>
                        <option value="반려">반려 건</option>
                    </select>
                </div>

                <div className="admin-right-info">
                    <span>오늘 날짜: 2026-03-31</span>
                    <select 
                        className="admin-sort-select"
                        value={sortOrder}
                        onChange={(e) => setSortOrder(e.target.value)}
                    >
                        <option value="오래된 순">오래된 순</option>
                        <option value="최신순">최신순</option>
                    </select>
                </div>
            </div>

            <div className="proof-list">
                {filteredProofs.map((proof, index) => (
                    <ProofCard 
                        key={proof.id} 
                        proof={proof} 
                        index={index} 
                        onReview={() => setSelectedProof(proof)} 
                    />
                ))}
            </div>
                {/* 모달 렌더링 조건 */}
                {selectedProof && (
                    <ReviewModal 
                    proof={selectedProof} 
                    onClose={() => setSelectedProof(null)}
                    onConfirm={handleReviewConfirm}
                    />
                )}
        </div>
    );
}

export default AdminPage;