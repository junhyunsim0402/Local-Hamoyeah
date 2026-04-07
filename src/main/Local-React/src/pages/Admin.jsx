import React, { useEffect, useState } from 'react';
import ProofCard from '../components/ProofCard';
import ReviewModal from '../components/ReviewModal';
import axios from 'axios';
import './Admin.css'

function AdminPage() {
    const [proofs, setProofs] = useState([]);
    const [loading, setLoading] = useState(true);

    const [selectedStatus, setSelectedStatus] = useState('대기중'); // 상태 필터링용 State
    const [sortOrder, setSortOrder] = useState('오래된 순'); // 정렬용 State
    const [selectedProof, setSelectedProof] = useState(null);


    const fetchProofs = async () => {
        try{
            const token = localStorage.getItem('token');
            const response = await axios.get("http://localhost:8080/userproof/verifyuser", 
                {headers: {Authorization: token}}
            );
            console.log(response)
            setProofs(response.data);
            setLoading(false);
        } catch(e){
            console.error("데이터 로딩 실패", e);
            setLoading(false);
        }
    };

    useEffect(()=>{
        fetchProofs();
    },[]);

    const handleReviewConfirm = async (proofId, newStatus, reason = '') => {
        try{
            const token = localStorage.getItem('token');
            const requestData = {
                proofId: proofId,
                status: newStatus, // "승인" 또는 "반려"
                rejectReason: reason // 반려일 때만 내용이 들어감
             }

             const response = await axios.post('http://localhost:8080/userproof/status',
                requestData,
                {headers: {Authorization: token}}
             );

             alert(response.data);

             fetchProofs();
             setSelectedProof(null);
        }catch (error) {
            console.error("처리 중 에러 발생:", error);
            // 백엔드에서 보낸 에러 메시지가 있다면 그걸 보여줌
            const errorMsg = error.response?.data || "승인/반려 처리 중 오류가 발생했습니다.";
            alert(errorMsg);
        }
    };

    const filteredProofs = proofs
        .filter(p => selectedStatus === '전체' || p.status === selectedStatus)
        .sort((a, b) => {
            if (sortOrder === '최신순') return new Date(b.created_at) - new Date(a.created_at);
            return new Date(a.created_at) - new Date(b.created_at);
        });

    

    if (loading) return <div className="admin-loading">데이터를 불러오는 중...</div>;

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
                        <option value="전체">전체</option>
                        <option value="대기중">대기중</option>
                        <option value="승인">승인</option>
                        <option value="반려">반려</option>
                    </select>
                </div>

                <div className="admin-right-info">
                    <span>오늘 날짜: {new Date().toLocaleDateString()}</span>
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
                {filteredProofs.length > 0 ? (
                    filteredProofs.map((proof, index) => (
                        <ProofCard 
                            key={proof.proofId} // id -> proofId
                            proof={proof} 
                            index={index} 
                            onReview={() => setSelectedProof(proof)} 
                            isAdmin={true}
                        />
                    ))
                ) : (
                    <div className="empty-msg">해당하는 인증 내역이 없습니다.</div>
                )}
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