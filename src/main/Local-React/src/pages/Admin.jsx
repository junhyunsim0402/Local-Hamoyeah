import React, { useState } from 'react';
import ProofCard from '../components/ProofCard';
import './Admin.css'

function AdminPage() {
  const [proofs, setProofs] = useState([
    { id: 1, status: '대기', nickname: '민수', content_category: '카페 투어', created_at: '2026-03-30 14:00', image_url: '' },
    { id: 2, status: '대기', nickname: '민수', content_category: '카페 투어', created_at: '2026-03-30 14:00', image_url: '' },
    { id: 3, status: '대기', nickname: '민수', content_category: '카페 투어', created_at: '2026-03-30 14:00', image_url: '' },
    { id: 4, status: '승인', nickname: '길동', content_category: '러닝 인증', created_at: '2026-03-30 13:30', admin_name: '관리자A' },
    { id: 5, status: '반려', nickname: '철수', content_category: '헬스장 인증', created_at: '2026-03-30 12:00', reject_reason: '화질 저하', admin_name: '관리자B' },
  ]);

  return (
    <div className="admin-page-root">
      <h1 className="admin-title">관리자 인증 화면</h1>
      <div className="admin-header-info">
        <span>오늘 날짜: 2026-03-31</span>
        <select className="admin-sort-select">
          <option>오래된 순</option>
          <option>최신순</option>
        </select>
      </div>

      <div className="proof-list">
        {proofs.map((proof, index) => (
            <ProofCard 
            key={proof.id} 
            proof={proof} 
            index={index}
            />
        ))}
        </div>
    </div>
  );
}

export default AdminPage;