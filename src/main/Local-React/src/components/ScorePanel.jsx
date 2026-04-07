import React from 'react';
import './ScorePanel.css';

function ScorePanel({ isOpen, onClose, scoreData }) {
  if (!scoreData) return null;

  const { grade, totalScore, cctvScore, cctvCount, streetLampScore, streetLampCount, noiseScore, noiseAvg, airScore, pm10, pm25 } = scoreData;

  const maxScore = 60;

  const bars = [
    { label: 'CCTV', score: cctvScore, color: '#378ADD' },
    { label: '가로등', score: streetLampScore, color: '#1D9E75' },
    ...(noiseAvg !== 0 ? [{ label: '소음', score: noiseScore, color: '#EF9F27' }] : []),
    { label: '대기질', score: airScore, color: '#E24B4A' },
  ];  // 점수 바 설정

  const gradeColor = {
    'A': { bg: '#E6F1FB', text: '#0C447C' },
    'B': { bg: '#EAF3DE', text: '#3B6D11' },
    'C': { bg: '#FAEEDA', text: '#854F0B' },
    'D': { bg: '#FCEBEB', text: '#A32D2D' },
  }[grade] || { bg: '#F1EFE8', text: '#5F5E5A' }; // 등급 바 설정

  return (
    <>
      {isOpen && <div className="score-panel-overlay" onClick={onClose} />}
      <div className={`score-panel ${isOpen ? 'open' : ''}`}>
        <div className="score-panel-handle-wrap" onClick={onClose}>
          <div className="score-panel-handle" />
        </div>

        {/* 헤더 */}
        <div className="score-panel-header">
          <div className="score-panel-left">
            <div className="grade-badge" style={{ background: gradeColor.bg, color: gradeColor.text }}>
              {grade}
            </div>
            <div>
              <div className="score-panel-title">정주여건 점수</div>
              <div className="score-panel-sub">반경 500m 기준</div>
            </div>
          </div>
          <div className="score-total">
            총점 <span>{totalScore}</span>
          </div>
        </div>

        {/* 막대 차트 */}
        <div className="score-bars">
          {bars.map((item) => (
            <div className="score-item" key={item.label}>
              <div className="score-label-row">
                <span>{item.label}</span>
                <span>{item.score}점</span>
              </div>
              <div className="bar-bg">
                <div
                  className="bar-fill"
                  style={{
                    width: `${Math.max((item.score / maxScore) * 100, 2)}%`,
                    background: item.color,
                  }}
                />
              </div>
            </div>
          ))}
        </div>

        <div className="score-divider" />

        {/* 상세 카드 */}
        <div className="score-detail-title">상세 정보</div>
        <div className="detail-grid">
          <div className="detail-card">
            <div className="detail-label">CCTV 수</div>
            <div className="detail-value">{cctvCount}개</div>
          </div>
          <div className="detail-card">
            <div className="detail-label">가로등 수</div>
            <div className="detail-value">{streetLampCount}개</div>
          </div>
          {noiseAvg !== 0 && (  // 소음 데이터가 있으면 실행
          <div className="detail-card">
            <div className="detail-label">소음 평균</div>
            <div className="detail-value">{noiseAvg}dB</div>
          </div>
          )}
          <div className="detail-card">
            <div className="detail-label">미세먼지 PM10</div>
            <div className="detail-value">{pm10}</div>
          </div>
          <div className="detail-card">
            <div className="detail-label">초미세먼지 PM2.5</div>
            <div className="detail-value">{pm25}</div>
          </div>
        </div>
      </div>
    </>
  );
}

export default ScorePanel;
