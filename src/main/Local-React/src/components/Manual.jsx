import './Manual.css';
import manual1 from '../assets/manual1.png';
import manual2 from '../assets/manual2.png';
import manual3 from '../assets/manual3.png';
import {useNavigate} from 'react-router-dom';

function Manual() {
  const navigate=useNavigate();
  const manualClose=()=>{
    
    navigate('/main');
  };
  
  return (
    <div className="manual-page">
      <button className="manual-close" onClick={manualClose}>&times;</button>
      <div className="start-ment">
        <p className="first-ment">진주를 더 재밌게 즐기는 법</p>
        <p>지도로 안심을 확인하고, 탐험으로 가치를 모으는 우리 동네 상생 플랫폼</p>
      </div>
      <div className="search-section">
        <img src={manual1} alt="Manual1" className="manual1"/>
        <p>[STEP 1] 탐색하기 🔍</p>
        <p>내 주변의 착한 식당, 문화재, 제로웨이스트 가게를 찾아보세요!</p> <br/>
      </div>
      <div className="verify-section">
        <img src={manual2} alt="Manual2" className="manual2"/>
        <p>[STEP 2] 방문 및 인증하기📸</p>
        <p>직접 방문하여 즐거운 경험을 하고 간단하게 인증하기 완료!</p> <br/>
      </div>
      <div className="point-section">
        <img src={manual3} alt="Manual3" className="manual3"/>
        <p>[STEP 3] 포인트 적립💰</p>
        <p>매일 실천하는 지역 사랑으로 포인트를 차곡차곡 쌓으세요!</p> <br/>
      </div>
    </div>
  );
}

export default Manual;